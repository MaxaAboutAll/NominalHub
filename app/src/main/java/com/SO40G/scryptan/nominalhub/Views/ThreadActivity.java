package com.SO40G.scryptan.nominalhub.Views;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Service.GetNewNick;
import com.SO40G.scryptan.nominalhub.Service.Objects.Nick;
import com.SO40G.scryptan.nominalhub.Views.Fragments.CreateFragment;
import com.SO40G.scryptan.nominalhub.Views.Fragments.ThreadFragment;
import com.SO40G.scryptan.nominalhub.Views.Fragments.ThreadsFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.InterstitialAd;


public class ThreadActivity extends AppCompatActivity {

    private Gson gson;
    private GsonBuilder builder;
    private Nick nick;
    private TextView nickTV, timeTV, toolbarTV, greenwichTimeTV;
    private FragmentManager fragmentManager;
    private ThreadFragment threadFragment;
    private CreateFragment createFragment;
    private MenuItem createPost, refreshThread;
    private boolean isCreatePostActive;
    private String TAG = "ThreadActivity", timeNick ,thread, articleId;
    private long h,s,m, timeForChange, time;
    private InterstitialAd mInterstitialAd;
    public String greenwichDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4778676867763953~4382525349");
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createFragment = new CreateFragment();
        threadFragment = new ThreadFragment();
        fragmentManager = getFragmentManager();
        //---------------------------------------ads------------------------------------------------
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4778676867763953/7471723111");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                workWithSections();
            }
            @Override
            public void onAdLoaded() {
                Log.e(TAG, "onAdLoaded: ads is loaded" );
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, "onAdFailedToLoad: "+errorCode );
            }
        });
        //------------------------------------------------------------------------------------------
        builder = new GsonBuilder();
        gson = builder.create();
        try {
            nick = gson.fromJson(readFile("Nick"), Nick.class);
            timeNick = nick.time;
        }catch (Exception e){
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------------
        toolbarTV = (TextView) findViewById(R.id.toolbarTV);
        nickTV = (TextView) findViewById(R.id.nickTV);
        timeTV = (TextView) findViewById(R.id.timeTV);
        greenwichTimeTV = (TextView) findViewById(R.id.greenwichTimeTV);
        nickTV.setText(nick.name);
        nickTV.setTextColor(Color.parseColor(nick.color));
        //------------------------------------------------------------------------------------------

        try {
            Timer timer = new Timer();
            timer.schedule(new UpdateTimeTask(), 0, 1000);
        }catch (Exception e){

        }
        setTitle(getThread());
        isCreatePostActive = false;
    }

    class UpdateTimeTask extends TimerTask {
        public void run() {
            Date date = new Date();
            timeForChange = Long.parseLong(nick.time);
            time = 5*60*60*1000 - (date.getTime()-timeForChange);
            h = time/1000/60/60;
            m = (time/1000/60)%60;
            s = (time/1000)%60;
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            greenwichDate = dateFormatGmt.format(new Date());
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Date date = new Date();
                    if((date.getTime()-timeForChange)>=5*60*60*1000){
                        GetNewNick getNewNick = new GetNewNick();
                        nick = getNewNick.getNewNick();
                        writeFile(gson.toJson(nick), "Nick");
                        nickTV.setText(nick.name);
                        nickTV.setTextColor(Color.parseColor(nick.color));
                    }
                    timeTV.setText(h+":"+m+":"+s+" untill change");
                    greenwichTimeTV.setText("Server time: "+greenwichDate);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        createPost = menu.findItem(R.id.action_plus);
        refreshThread = menu.findItem(R.id.action_refresh);
        fragmentManager.beginTransaction()
                .add(R.id.frame_container, threadFragment, "thread")
                .commit();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_plus:
                workWithCreate();
                return true;
            case R.id.action_section:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {
                    workWithSections();
                    Log.e(TAG, "onOptionsItemSelected: NotLoaded ");
                }
                return true;
            case R.id.action_refresh:
               threadFragment.getThisThread(thread);
                Log.e(TAG, "onOptionsItemSelected: "+thread );
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(checkFragment()){
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, threadFragment)
                    .commit();
        }else {
            finish();
        }
    }

    private boolean checkFragment(){
        if(!(threadFragment.isVisible())){
            return true;
        }else return false;
    }

    private void workWithCreate(){
        createFragment = (CreateFragment) getFragmentManager().findFragmentByTag("create");
        Log.e(TAG, "onClick: "+ createFragment );
        if(!(createFragment != null && createFragment.isVisible())) {
            createFragment = new CreateFragment();
            Bundle bundle = new Bundle();
            bundle.putString("nick", gson.toJson(nick));
            bundle.putString("thread", thread);
            createFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, createFragment, "create")
                    .commit();
        }else {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, threadFragment)
                    .commit();
        }
    }

    private void workWithSections(){
        ThreadsFragment threadsFragment = new ThreadsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, threadsFragment, "create")
                .commitAllowingStateLoss();
    }

    private void writeFile(String dataJSON, String FILENAME) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            bw.write(dataJSON);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String FILENAME) {
        try {
            String returning = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            while ((str = br.readLine()) != null) {
                returning+= str;
            }
            return returning;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void setThread(String thread){
        this.thread = thread;
    }

    public Nick getNick(){
        return nick;
    }

    public String getThread(){
        return thread;
    }

    public void setTitle(String title_tool){
        toolbarTV.setText("#"+title_tool);
    }

    public void workWithMenuPlus(){
        if(isCreatePostActive){
            createPost.setVisible(false);
            refreshThread.setVisible(false);
            isCreatePostActive = false;
        }else {
            createPost.setVisible(true);
            refreshThread.setVisible(true);
            isCreatePostActive = true;
        }
    }

    public boolean getPlusBoolean(){
        return isCreatePostActive;
    }

    public void setArticle(String articleId){
        this.articleId = articleId;
    }
    public String getArticle(){
        return articleId;
    }
}
