package com.SO40G.scryptan.nominalhub.Service;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.SO40G.scryptan.nominalhub.Server.ApiUtils;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetAnimals;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetColors;
import com.SO40G.scryptan.nominalhub.Service.Objects.Animals;
import com.SO40G.scryptan.nominalhub.Service.Objects.Colors;
import com.SO40G.scryptan.nominalhub.Service.Objects.Nick;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class GetNewNick extends AppCompatActivity {

    private Random rand = new Random();
    private List<Colors> colors;
    private List<Animals> animals;
    private Gson gson;
    private GsonBuilder builder;
    private GetAnimals getAnimals;
    private GetColors getColors;
    private RefactorData refactorData;
    private Date date;
    private String TAG = "GetNewNick";
    private Context context;

    public GetNewNick() {
        this.context = context;
        date = new Date();
        refactorData = new RefactorData();
        colors = new ArrayList<>();
        animals = new ArrayList<>();
        builder = new GsonBuilder();
        gson = builder.create();
        getAnimals = ApiUtils.getAnimals();
        getColors = ApiUtils.getColors();
        getMyColors();
        getMyAnimals();
    }

    public Nick getNewNick(){
        if(colors == null || animals == null){
            getMyColors();
            getMyAnimals();
        }
        while (colors ==null || animals == null){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int colorInt = rand.nextInt(colors.size());
        String colorName = colors.get(colorInt).name,
                animalName = animals.get(rand.nextInt(animals.size())).name,
                colorHEX = colors.get(colorInt).HEX;
        Log.e(TAG, "getNewNick: "+animals.size()+" "+colors.size() );
        date = new Date();
        Nick newNick = new Nick("[" + colorName + " " + animalName + "]", colorHEX, date.getTime()+"");
        return newNick;
    }

    private void getMyColors(){
        getColors.getColors().enqueue(new Callback<List<Colors>>() {
            @Override
            public void onResponse(Call<List<Colors>> call, Response<List<Colors>> response) {
                if(response.isSuccessful()) {
                    colors = response.body();
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
                if(response.isSuccessful())
                    animals = response.body();
            }

            @Override
            public void onFailure(Call<List<Animals>> call, Throwable t) {

            }
        });
    }

    private List<Colors> getDownloadedColors(){
        return colors;
    }
}
