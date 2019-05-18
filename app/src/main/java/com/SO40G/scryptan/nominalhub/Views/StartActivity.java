package com.SO40G.scryptan.nominalhub.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetAnimals;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetColors;
import com.SO40G.scryptan.nominalhub.Service.GetNewNick;
import com.SO40G.scryptan.nominalhub.Service.Objects.Animals;
import com.SO40G.scryptan.nominalhub.Service.Objects.Colors;
import com.SO40G.scryptan.nominalhub.Service.Objects.Nick;
import com.SO40G.scryptan.nominalhub.Service.RefactorData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    private Random rand = new Random();
    private List<Colors> colors;
    private List<Animals> animals;
    private String timeNick;
    private GetNewNick getNewNick;
    private GetAnimals getAnimals;
    private GetColors getColors;
    private Gson gson;
    private GsonBuilder builder;
    private String TAG = "StartActivity";
    private Date date;
    private LinearLayout myLL;
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        myLL = (LinearLayout) findViewById(R.id.myLL);
        builder = new GsonBuilder();
        gson = builder.create();
        getAnimals = ApiUtils.getAnimals();
        getColors = ApiUtils.getColors();
        if(hasConnection(this)) {
            getNewNick = new GetNewNick();
            getMemoryPermission();
        }else{
            myLL.setBackgroundResource(R.drawable.start_no_internet);
        }
    }


    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    private void checkNick(Nick myNewNick){
        if(readFile("Nick")==null || readFile("Nick")=="") {
            Nick nick = myNewNick;
            writeFile(gson.toJson(nick), "Nick");
            startMainActivity();
        }else{
            Nick nick = gson.fromJson(readFile("Nick"), Nick.class);
            timeNick = nick.time;
            Date date = new Date();
            if ((date.getTime()-Long.parseLong(timeNick))>=5*60*60*1000){
                nick = myNewNick;
                writeFile(gson.toJson(nick), "Nick");
                startMainActivity();
            }else{
                writeFile(gson.toJson(nick), "Nick");
                startMainActivity();
            }
        }
    }

    private void getMemoryPermission() {
        Log.i("TAG", "getMemoryPermission");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                getNewNick();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
            try {
                //Thread.sleep(5000);
            }catch (Exception e){

            }

        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, ThreadActivity.class);
        startActivity(intent);
        finish();
    }

    public void writeFile(String dataJSON, String FILENAME) {
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

    public String readFile(String FILENAME) {
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

    public void getNewNick() {
        if(colors == null || animals == null){
            getMyColors();
        }
    }

    private void getMyColors(){
        Log.e(TAG, "getMyColors: " );
        getColors.getColors().enqueue(new Callback<List<Colors>>() {
            @Override
            public void onResponse(Call<List<Colors>> call, Response<List<Colors>> response) {
                if(response.isSuccessful()) {
                    colors = response.body();
                    getMyAnimals();
                    Log.e(TAG, "onResponse: COLORS SUC" );
                }
            }

            @Override
            public void onFailure(Call<List<Colors>> call, Throwable t) {

            }
        });
    }

    private void getMyAnimals(){
        getAnimals.getAnimals().enqueue(new Callback<List<Animals>>() {
            @Override
            public void onResponse(Call<List<Animals>> call, Response<List<Animals>> response) {
                if(response.isSuccessful()) {
                    animals = response.body();
                    int colorInt = rand.nextInt(colors.size());
                    String colorName = colors.get(colorInt).name,
                            animalName = animals.get(rand.nextInt(animals.size())).name,
                            colorHEX = colors.get(colorInt).HEX;
                    Log.e(TAG, "getNewNick: "+animals.size()+" "+colors.size() );
                    date = new Date();
                    Nick newNick = new Nick("[" + colorName + " " + animalName + "]", colorHEX, date.getTime()+"");
                    Log.e(TAG, "onResponse: ANIMALS---------------------- " );
                    checkNick(newNick);
                }
            }

            @Override
            public void onFailure(Call<List<Animals>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getNewNick();
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),"I NEED F*** PERMISSIONS",Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
    }
}
