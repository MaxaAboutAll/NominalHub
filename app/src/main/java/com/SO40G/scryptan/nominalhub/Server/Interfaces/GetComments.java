package com.SO40G.scryptan.nominalhub.Server.Interfaces;

import com.SO40G.scryptan.nominalhub.Server.Objects.Comment;
import com.SO40G.scryptan.nominalhub.Server.Objects.toServer.toGetComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetComments {
    @POST("/getComments")
    Call<List<Comment>> getComments(@Body toGetComment toGetComment);
}
