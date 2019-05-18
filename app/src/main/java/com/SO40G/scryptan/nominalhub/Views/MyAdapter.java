package com.SO40G.scryptan.nominalhub.Views;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Views.Fragments.ArticleFragment;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Article> values;
    private FragmentManager fragmentManager;
    private ArticleFragment articleFragment;
    private Context context;
    private String TAG = "MyAdapter";
    private boolean isLong = false;

    public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView nickTV, textTV, repliesTV, dateTV;
            public ImageView articleIV;
            public View layout;
            public Button gotoBtn;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                nickTV = (TextView) v.findViewById(R.id.nickTV);
                textTV = (TextView) v.findViewById(R.id.textTV);
                repliesTV = (TextView) v.findViewById(R.id.repliesTV);
                dateTV = (TextView) v.findViewById(R.id.dateTV);
                articleIV = (ImageView) v.findViewById(R.id.articleIV);
                gotoBtn = (Button) v.findViewById(R.id.enterBTN);
            }
    }

    public void add(int position, Article item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public MyAdapter(List<Article> myDataset, Context context, FragmentManager fragmentManager) {
        Collections.reverse(myDataset);
        values = myDataset;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.thread_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String nick = values.get(position).name;
        String text = values.get(position).text;
        final String replies = values.get(position).replies;
        final String date = values.get(position).date;
        if (text.length()>232){
            isLong = true;
            text = text.substring(0,232);
            text +=" Show all...";
        }
        holder.nickTV.setText(nick);
        holder.nickTV.setTextColor(Color.parseColor(values.get(position).color));
        holder.textTV.setText(text);
        holder.repliesTV.setText(replies + " replies");
        holder.dateTV.setText(date);
        Picasso.get()
                .load(values.get(position).pic)
                .resize(800,800)
                .centerCrop()
                .into(holder.articleIV);
        holder.textTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLong) {
                    try {
                        articleFragment = new ArticleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("_id", values.get(position)._id);
                        articleFragment.setArguments(bundle);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frame_container, articleFragment, "article")
                                .commit();
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ---------", e);
                    }
                }

            }
        });
        holder.gotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    articleFragment = new ArticleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("_id", values.get(position)._id);
                    articleFragment.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, articleFragment, "article")
                            .commit();
                }catch (Exception e){
                    Log.e(TAG, "onClick: ---------",e );
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}