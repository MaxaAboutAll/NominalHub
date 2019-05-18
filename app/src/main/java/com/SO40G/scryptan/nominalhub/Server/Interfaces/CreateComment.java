package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Server.Objects.Article;
import com.SO40G.scryptan.nominalhub.Server.Objects.Comment;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CreateComment {
    @Multipart
    @POST("/createComment")
    Call<Comment> upload(
            @Part MultipartBody.Part file,
            @Part("nick") RequestBody nick,
            @Part("text") RequestBody text,
            @Part("color") RequestBody color,
            @Part("thread") RequestBody thread,
            @Part("date") RequestBody date
    );
}
