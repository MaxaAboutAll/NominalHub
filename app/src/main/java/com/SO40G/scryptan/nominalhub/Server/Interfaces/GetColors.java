package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Service.Objects.Colors;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface GetColors {
    @GET("/getColors")
    Call<List<Colors>> getColors();
}
