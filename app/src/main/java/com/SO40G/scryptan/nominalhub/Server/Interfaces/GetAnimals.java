package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Service.Objects.Animals;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetAnimals {
    @GET("/getAnimals")
    Call<List<Animals>> getAnimals();
}
