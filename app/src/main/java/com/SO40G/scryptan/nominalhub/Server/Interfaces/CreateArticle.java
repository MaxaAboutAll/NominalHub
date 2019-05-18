package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Server.Objects.Article;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CreateArticle {
    @Multipart
    @POST("/createArticle")
    Call<Article> upload(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("text") RequestBody text,
            @Part("date") RequestBody date,
            @Part("theme") RequestBody theme,
            @Part("color") RequestBody color
    );
}
