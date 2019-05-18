package com.SO40G.scryptan.nominalhub.Views.Fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetArticle;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetComments;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.Comment;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetArticle;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetComment;
import com.SO40G.scryptan.nominalhub.Views.BigImageActivity;
import com.SO40G.scryptan.nominalhub.Views.CommentAdapter;
import com.SO40G.scryptan.nominalhub.Views.MyAdapter;
import com.SO40G.scryptan.nominalhub.Views.ThreadActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleFragment extends Fragment {

    private GetArticle getArticle;
    private Article article;
    private TextView nickTV, dateTV, textTV;
    private FloatingActionButton myFab;
    private ImageView picIV;
    private Context context;
    private GetComments getComments;
    private RecyclerView.Adapter commentAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ScrollView scrollView;
    private FragmentManager fragmentManager;
    private RecyclerView commentRV;
    private ThreadActivity threadActivity;
    private String TAG = "ArticleFragment", articleId;
    private View view;
    private List<Comment> thisComments;
    private UpdateTask updateTask;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getArticle = ApiUtils.getArticle();
        getComments = ApiUtils.getComments();
        bundle = getArguments();
        articleId = bundle.getString("_id");
        //------------------------------------------------------------------------------------------
        context = inflater.getContext();
        fragmentManager = getFragmentManager();
        threadActivity = (ThreadActivity) getActivity();
        thisComments = new ArrayList<>();
        //------------------------------------------------------------------------------------------
        view = inflater.inflate(R.layout.fragment_article,  container, false);
        myFab = (FloatingActionButton) view.findViewById(R.id.answerFAB);
        picIV = (ImageView) view.findViewById(R.id.imageViewArt);
        commentRV = (RecyclerView) view.findViewById(R.id.commentRV);
        scrollView = (ScrollView) view.findViewById(R.id.myScrollView);

        commentRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(inflater.getContext());
        commentRV.setLayoutManager(layoutManager);
        nickTV = (TextView) view.findViewById(R.id.nickArtTV);
        dateTV = (TextView) view.findViewById(R.id.dateArtTV);
        textTV = (TextView) view.findViewById(R.id.textArtTV);
        getThisArticle(articleId);
        getThisComments(articleId);
        OnClick();
        updateTask = new UpdateTask();
        if(threadActivity.getPlusBoolean())
            threadActivity.workWithMenuPlus();
        getThisArticle(articleId);
        setRetainInstance(true);
        return view;
    }

    private void OnClick(){
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("_id",article._id);
                CommentCreateFragment commentCreateFragment = new CommentCreateFragment();
                commentCreateFragment.setArguments(bundle);
                updateTask.cancel(true);
                fragmentManager.beginTransaction()
                        //.add(commentCreateFragment, "create")
                        .addToBackStack(null)
                        .replace(R.id.frame_container, commentCreateFragment, "create")
                        .commit();
            }
        });
        picIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra("imageURL", article.pic);
                startActivity(intent);
            }
        });
    }


//--------------------------------------------------------------------------------------------------
    private void getThisArticle(String articleId){
        getArticle.getArticle(new toGetArticle(articleId)).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                if(response.isSuccessful()){
                    article = response.body();
                    setView();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
            }
        });
    }

    private void getThisComments(String articleId){
        getComments.getComments(new toGetComment(articleId)).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null) {
                        thisComments = response.body();
                        setmAdapter();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });
    }

    private void setView(){
        nickTV.setText(article.name);
        nickTV.setTextColor(Color.parseColor(article.color));
        dateTV.setText(article.date);
        textTV.setText(article.text);
        Picasso.get()
                .load(article.pic)
                .into(picIV);
    }

    private void setmAdapter(){
        commentAdapter = new CommentAdapter(thisComments, context, getFragmentManager(), articleId);
        commentRV.setAdapter(commentAdapter);
        updateTask.execute();
    }


    class UpdateTask extends AsyncTask<Void,Integer,Void>{
        int oldSize, newSize;
        @Override
        protected Void doInBackground(Void... voids) {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                oldSize = thisComments.size();
                getThisComments(articleId);
                if(oldSize == -15) break;
                if(isCancelled()) return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            commentAdapter.notifyItemInserted(values[0]);
            Toast.makeText(threadActivity.getApplicationContext(),"Текст полученного нового коомента: "+thisComments.get(values[0]).text,Toast.LENGTH_SHORT).show();
        }

        private void getThisComments(String articleId){
            getComments.getComments(new toGetComment(articleId)).enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if(response.isSuccessful()){
                        if(response.body()!=null) {
                            List<Comment> newCommets = response.body();
                            newSize = newCommets.size();
                            if(oldSize != newSize){
                                for (int i = oldSize; i <newSize ; i++) {
                                    thisComments.add(newCommets.get(i));
                                    publishProgress(i);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {

                }
            });
        }
    }
}
