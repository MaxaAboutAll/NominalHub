package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetThread;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetThread {
    @POST("/getThread")
    Call<List<Article>> getThread(@Body toGetThread thread);
}
