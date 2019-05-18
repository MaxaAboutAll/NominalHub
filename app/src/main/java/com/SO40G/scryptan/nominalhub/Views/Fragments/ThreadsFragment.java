package com.SO40G.scryptan.nominalhub.Views.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetThreads;
import com.SO40G.scryptan.nominalhub.Service.Objects.Threads;
import com.SO40G.scryptan.nominalhub.Views.ThreadActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThreadsFragment extends Fragment {

    private GetThreads getThreads;
    private ListView threadsLV;
    private Context context;
    private ThreadActivity threadActivity;
    private FragmentManager fragmentManager;
    private String TAG = "ThreadsFragment";
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads,  container, false);
        context = inflater.getContext();
        getThreads = ApiUtils.getThreads();
        threadActivity = (ThreadActivity) getActivity();
        threadsLV = (ListView) view.findViewById(R.id.threadsLV);
        //Log.e(TAG, "onCreateView: "+threadsLV );
        if(threadActivity.getPlusBoolean())
            threadActivity.workWithMenuPlus();
        getAllThreads();
        return view;
    }

    private void getAllThreads(){
        getThreads.getThreads().enqueue(new Callback<List<Threads>>() {
            @Override
            public void onResponse(Call<List<Threads>> call, Response<List<Threads>> response) {
                if(response.isSuccessful()){
                    String[] threads = new String[response.body().size()];
                    for (int i = 0; i < response.body().size(); i++) {
                        threads[i] = response.body().get(i).name;
                    }
                    adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, threads);
                    threadsLV.setAdapter(adapter);
                    listClick();
                }
            }

            @Override
            public void onFailure(Call<List<Threads>> call, Throwable t) {

            }
        });
    }

    private void listClick(){
        threadsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ThreadFragment threadFragment = new ThreadFragment();
                Bundle bundle = getArguments();
                if(bundle == null) bundle = new Bundle();
                bundle.putString("_id", ((TextView) view).getText().toString());
                threadFragment.setArguments(bundle);
                threadActivity.setThread(((TextView) view).getText().toString());
                fragmentManager = threadActivity.getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, threadFragment, "article")
                        .commit();
            }
        });
    }

}