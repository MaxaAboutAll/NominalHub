package com.SO40G.scryptan.nominalhub.Server;

import com.SO40G.scryptan.nominalhub.Server.Interfaces.CreateArticle;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.CreateComment;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetAnimals;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetArticle;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetColors;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetComments;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetThread;
import com.SO40G.scryptan.nominalhub.Server.Interfaces.GetThreads;

public class ApiUtils {
    public static final String BASE_URl = "http://90.157.46.5:3000/";

    public static GetThread getThread(){
        return RetrofitClient.getClient(BASE_URl).create(GetThread.class);
    }

    public static GetThreads getThreads(){
        return RetrofitClient.getClient(BASE_URl).create(GetThreads.class);
    }
    public static GetColors getColors(){
        return RetrofitClient.getClient(BASE_URl).create(GetColors.class);
    }
    public static GetAnimals getAnimals(){
        return RetrofitClient.getClient(BASE_URl).create(GetAnimals.class);
    }
    public static GetArticle getArticle(){
        return RetrofitClient.getClient(BASE_URl).create(GetArticle.class);
    }
    public static GetComments getComments(){
        return RetrofitClient.getClient(BASE_URl).create(GetComments.class);
    }
    public static CreateArticle createArticle(){
        return RetrofitClient.getClient(BASE_URl).create(CreateArticle.class);
    }
    public static CreateComment createComment(){
        return RetrofitClient.getClient(BASE_URl).create(CreateComment.class);
    }
}