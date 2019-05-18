package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetArticle;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetArticle {
    @POST("/getArticle")
    Call<Article> getArticle(@Body toGetArticle thread);
}
