package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Service.Objects.Threads;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetThreads {
    @GET("/getThreads")
    Call<List<Threads>> getThreads();
}
