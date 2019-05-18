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
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.CreateArticle;
import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Service.Objects.Nick;
import com.SO40G.scryptan.nominalhub.Views.ThreadActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class CreateFragment extends Fragment implements View.OnClickListener{

    private String TAG = "CreateFragment";
    final int GALLERY_REQUEST = 1;
    private Uri selectedImage;
    private Context context;
    private Button sendBTN, photoBTN;
    private ImageView previewIV;
    private ProgressBar sendPB;
    private EditText textET;
    private CreateArticle createArticle;
    private ThreadActivity threadActivity;
    private FragmentManager fragmentManager;
    private Nick nick;
    private String thread;
    private Gson gson;
    private GsonBuilder gsonBuilder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_fragment,  container, false);
        //------------------------------------------------------------------------------------------
        Bundle bundle = getArguments();
        thread = bundle.getString("thread");
        threadActivity = (ThreadActivity) getActivity();
        createArticle = ApiUtils.createArticle();
        //------------------------------------------------------------------------------------------
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        nick = gson.fromJson(bundle.getString("nick"),Nick.class);
        //------------------------------------------------------------------------------------------
        sendBTN = (Button) view.findViewById(R.id.sendBTN);
        photoBTN = (Button) view.findViewById(R.id.photoBTN);
        textET = (EditText) view.findViewById(R.id.textET);
        sendPB = (ProgressBar) view.findViewById(R.id.sendPB);
        previewIV = (ImageView) view.findViewById(R.id.previewIV);
        sendBTN.setOnClickListener(this);
        photoBTN.setOnClickListener(this);
        previewIV.setOnClickListener(this);
        sendPB.setVisibility(View.GONE);
        //previewIV.setVisibility(View.GONE);
        context = inflater.getContext();
        if(threadActivity.getPlusBoolean())
            threadActivity.workWithMenuPlus();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendBTN:
                try {
                    int ogranich = 3;
                    if (selectedImage == null || textET.getText().toString().length() < ogranich) {
                        Toast.makeText(context, "Select the photo or fill more than "+ogranich+" symbols", Toast.LENGTH_SHORT).show();
                    } else
                        UploadThread(selectedImage);
                }catch (Exception e){
                    Log.e(TAG, "onClick: ---------------------------",e );
                }
                return;
            case R.id.photoBTN:
                try {
                    addImage();
                }catch (Exception e){
                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                }
                return;
            case R.id.previewIV:
                addImage();
                return;
        }
    }

    private void UploadThread(Uri fileUri){
        sendBTN.setVisibility(View.GONE);
        sendPB.setVisibility(View.VISIBLE);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String thisDate = dateFormatGmt.format(new Date());
        File file = new File(getRealPathFromURI(context, fileUri));
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
        RequestBody text = RequestBody.create(MediaType.parse("application/json"), textET.getText().toString());
        RequestBody date = RequestBody.create(MediaType.parse("application/json"), thisDate);
        RequestBody theme = RequestBody.create(MediaType.parse("application/json"), thread);
        RequestBody name = RequestBody.create(MediaType.parse("application/json"), nick.name);
        RequestBody color = RequestBody.create(MediaType.parse("application/json"), nick.color);
        Call<Article> call = createArticle.upload(body, name, text, date, theme, color);
        call.enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                if(response.isSuccessful()){
                    ThreadFragment threadFragment = new ThreadFragment();
                    fragmentManager = threadActivity.getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, threadFragment, "article")
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                sendPB.setVisibility(View.GONE);
                sendBTN.setVisibility(View.VISIBLE);
                Log.e(TAG, "onFailure: ",t);
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
                        //previewIV.setVisibility(View.VISIBLE);
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
