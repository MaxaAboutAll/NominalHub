package com.SO40G.scryptan.nominalhub.Views;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.Comment;
import com.SO40G.scryptan.nominalhub.Views.Fragments.ArticleFragment;
import com.SO40G.scryptan.nominalhub.Views.Fragments.CommentCreateFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> values;
    private Context context;
    private String TAG = "CommentAdapter", _id;
    private FragmentManager fragmentManager;
    private ArticleFragment articleFragment;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nickTV, textTV, dateTV;
        public Button replyBTN;
        public ImageView iconImg;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            nickTV = (TextView) v.findViewById(R.id.commentNickTV);
            textTV = (TextView) v.findViewById(R.id.commentTextTV);
            iconImg = (ImageView) v.findViewById(R.id.commentPicIV);
            dateTV = (TextView) v.findViewById(R.id.dateTV);
            replyBTN = (Button) v.findViewById(R.id.replyBTN);
            articleFragment = new ArticleFragment();
        }
    }

    public void add(int position, Comment item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public CommentAdapter(List<Comment> myDataset, Context context, FragmentManager fragmentManager, String _id) {
        values = myDataset;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this._id = _id;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.comment_card, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.ViewHolder holder, final int position) {
        final String nick = values.get(position).nick;
        final String text = values.get(position).text;
        String pic = values.get(position).pic;
        final String date = values.get(position).date;
        holder.nickTV.setText(nick);
        holder.nickTV.setTextColor(Color.parseColor(values.get(position).color));
        holder.textTV.setText(text);
        holder.dateTV.setText(date);
        if(pic == null||pic.equals("")) {
            holder.iconImg.setVisibility(View.GONE);
        }else {
            holder.iconImg.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(pic)
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.iconImg);
        }

        holder.iconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigImageActivity.class);
                intent.putExtra("imageURL", values.get(position).pic);
                startActivity(context,intent,Bundle.EMPTY);
            }
        });

        holder.replyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentCreateFragment commentCreateFragment = new CommentCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putString("_id", _id);
                bundle.putString("nick", values.get(position).nick);
                commentCreateFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, commentCreateFragment, "article")
                        .commit();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
