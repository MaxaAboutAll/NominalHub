package com.SO40G.scryptan.nominalhub.Views.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.CreateArticle;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.CreateComment;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.Comment;
import com.SO40G.scryptan.nominalhub.Service.Objects.Nick;
import com.SO40G.scryptan.nominalhub.Views.ThreadActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class CommentCreateFragment extends Fragment implements View.OnClickListener {

    private String TAG = "CommentCreateFragment", textFrom, _id, alienNick;
    final int GALLERY_REQUEST = 1;
    private Uri selectedImage;
    private Context context;
    private Button sendBTN, photoBTN;
    private ProgressBar sendPB;
    private ImageView previewIV;
    private EditText textET;
    private CreateComment createComment;
    private FragmentManager fragmentManager;
    private Nick nick;
    private GsonBuilder gsonBuilder;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_fragment,  container, false);
        //------------------------------------------------------------------------------------------
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        ThreadActivity threadActivity = (ThreadActivity) getActivity();
        nick = threadActivity.getNick();
        fragmentManager = getFragmentManager();
        //------------------------------------------------------------------------------------------
        Bundle bundle = getArguments();
        _id = bundle.getString("_id");
        Log.e(TAG, "onCreateView: "+_id );
        try{
            alienNick = bundle.getString("nick")+", ";
        }catch (Exception e){
            alienNick = "";
        }
        //Log.e(TAG, "onCreateView: "+alienNick+" "+alienNick.length() );
        if(alienNick.equals("null, ")){
            alienNick = "";
        }
        createComment = ApiUtils.createComment();
        sendBTN = (Button) view.findViewById(R.id.sendBTN);
        photoBTN = (Button) view.findViewById(R.id.photoBTN);
        textET = (EditText) view.findViewById(R.id.textET);
        sendPB = (ProgressBar) view.findViewById(R.id.sendPB);
        previewIV = (ImageView) view.findViewById(R.id.previewIV);
        sendBTN.setOnClickListener(this);
        photoBTN.setOnClickListener(this);
        previewIV.setOnClickListener(this);
        context = inflater.getContext();
        sendPB.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: "+view.getId() );
        switch (view.getId()){
            case R.id.sendBTN:
                int ogranich = 3;
                if(textET.getText().toString() == null||textET.getText().toString().length()<ogranich){
                    Toast.makeText(context, "Fill more than "+ogranich+" symbols", Toast.LENGTH_SHORT).show();
                }else
                    UploadComment(selectedImage);
                return;
            case R.id.photoBTN:
                addImage();
                return;
            case R.id.previewIV:
                addImage();
                return;
        }
    }

    private void UploadComment(Uri fileUri){
        sendBTN.setVisibility(View.GONE);
        sendPB.setVisibility(View.VISIBLE);
        selectedImage = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String thisDate = dateFormatGmt.format(new Date());
        RequestBody name = null;
        RequestBody text = null;
        RequestBody color = null;
        RequestBody theme = null;
        RequestBody time = null;
        MultipartBody.Part body = null;
        if(fileUri!=null) {
            File file = new File(getRealPathFromURI(context, fileUri));
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body =
                    MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
            text = RequestBody.create(MediaType.parse("application/json"), alienNick+textET.getText().toString());
            color = RequestBody.create(MediaType.parse("application/json"), nick.color);
            theme = RequestBody.create(MediaType.parse("application/json"), _id);
            name = RequestBody.create(MediaType.parse("application/json"), nick.name);
            time = RequestBody.create(MediaType.parse("application/json"), thisDate);
        }else {
            body = null;
            text = RequestBody.create(MediaType.parse("application/json"), alienNick+textET.getText().toString());
            color = RequestBody.create(MediaType.parse("application/json"), nick.color);
            theme = RequestBody.create(MediaType.parse("application/json"), _id);
            name = RequestBody.create(MediaType.parse("application/json"), nick.name);
            time = RequestBody.create(MediaType.parse("application/json"), thisDate);
        }
        Log.e(TAG, "UploadComment: "+body );
        createComment.upload(body,name,text,color,theme, time).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.isSuccessful()){
                    ArticleFragment articleFragment = new ArticleFragment();
                    Bundle bundle = new Bundle();
                    //Log.e(TAG, "onResponse: "+_id );
                    bundle.putString("_id", _id);
                    articleFragment.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, articleFragment, "article")
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context,"Error download",Toast.LENGTH_SHORT).show();
                sendBTN.setVisibility(View.VISIBLE);
                sendPB.setVisibility(View.GONE);
            }
        });
    }

    public void addImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    if(selectedImage!=null){
                        photoBTN.setVisibility(View.GONE);
                        previewIV.setImageURI(selectedImage);
                    }
                    try {
                        Log.e(TAG, "onActivityResult: "+selectedImage.toString() );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

}
