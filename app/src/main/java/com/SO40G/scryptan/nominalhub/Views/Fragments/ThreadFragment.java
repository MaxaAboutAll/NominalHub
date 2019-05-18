package com.SO40G.scryptan.nominalhub.Views.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetThread;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetThread;
import com.SO40G.scryptan.nominalhub.Views.MyAdapter;
import com.SO40G.scryptan.nominalhub.Views.ThreadActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThreadFragment extends Fragment {


    //----------------------------------------------------------------------------------------------
    private GetThread getThread;
    private Gson gson;
    private GsonBuilder builder;
    private String _id;
    private String TAG = "ThreadFragment";
    //----------------------------------------------------------------------------------------------
    private RecyclerView threadsView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ThreadActivity threadActivity;
    //----------------------------------------------------------------------------------------------

    List<Article> articles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thread_fragment,  container, false);
        //-----------------------------------Ways---------------------------------------------------
        getThread = ApiUtils.getThread();
        //-----------------------------------Json---------------------------------------------------
        builder = new GsonBuilder();
        gson = builder.create();
        articles = null;
        //------------------------------------------------------------------------------------------
        threadActivity = (ThreadActivity) getActivity();

        threadsView = (RecyclerView) view.findViewById(R.id.recyclerThreads);
        threadsView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(inflater.getContext());
        threadsView.setLayoutManager(layoutManager);;
        _id = threadActivity.getThread();
        if(_id == null) _id = "news";
        threadActivity.setThread(_id);
        threadActivity.setTitle(_id);
        getThisThread(_id);
        threadActivity.workWithMenuPlus();
        return view;
    }


    public void getThisThread(String nameOfThread){
        getThread.getThread(new toGetThread(nameOfThread)).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if(response.isSuccessful()){
                    articles = response.body();
                    setmAdapter(articles);
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Toast.makeText(threadActivity.getApplicationContext(), "Turn internet On",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setmAdapter(List<Article> input){
        mAdapter = new MyAdapter(input, threadActivity.getApplicationContext(), threadActivity.getFragmentManager());
        threadsView.setAdapter(mAdapter);
    }
}
