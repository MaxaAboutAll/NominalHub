package com.SO40G.scryptan.nominalhub.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.SO40G.scryptan.nominalhub.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigImageActivity extends AppCompatActivity {

    private ImageView myImageIV;
    private String imageURL, TAG = "BigImageActivity";
    private Button saveBTN, backBTN;
    private String folderToSave;
    private boolean isHudOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        Context context = getApplicationContext();
        Intent intent = getIntent();
        imageURL = intent.getStringExtra("imageURL");
        myImageIV = (ImageView) findViewById(R.id.myImageIV);
        saveBTN = (Button) findViewById(R.id.saveBTN);
        backBTN = (Button) findViewById(R.id.backBTN);
        Picasso.get()
                .load(imageURL)
                .into(myImageIV);
        folderToSave = context.getCacheDir().toString();
    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.saveBTN:
                Toast.makeText(getApplicationContext(),SavePicture(myImageIV,folderToSave),Toast.LENGTH_SHORT).show();
                break;
            case R.id.backBTN:
                finish();
                break;
            case R.id.myImageIV:
               workWithHUD();
        }
    }

    private String SavePicture(ImageView iv, String folderToSave) {
        OutputStream fOut;
        Time time = new Time();
        time.setToNow();

        try {
            File file = new File(folderToSave, Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) +".jpg"); // создать уникальное имя для файла основываясь на дате сохранения
            fOut = new FileOutputStream(file);

            Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName());
        }
        catch (Exception e)
        {
            Log.e(TAG, "SavePicture: ", e);
            return e.getMessage();
        }
        return "Successful save";
    }

    private void workWithHUD(){
        if(isHudOn){
            saveBTN.setVisibility(View.GONE);
            backBTN.setVisibility(View.GONE);
            isHudOn = false;
        }else{
            saveBTN.setVisibility(View.VISIBLE);
            backBTN.setVisibility(View.VISIBLE);
            isHudOn = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
