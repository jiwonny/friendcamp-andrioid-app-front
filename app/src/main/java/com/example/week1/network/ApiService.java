package com.example.week1.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    /* Contact */
    @GET("/Users/Name/{Name}/Phone/{Phone}")
    Call <User> getUserfrom_Name_Number(@Path("Name") String Name, @Path("Phone") String Phone);

    @POST("/Users")
    Call <User> post_User(@Body User user);






    /* Gallery */

    @GET("/{ImageName}")
    Call <ResponseBody> getImage(@Url String url, @Path("ImageName") String image_name);

    @GET("/Gallery/getImageList/{Login_id}")
    Call <List<Image_f>> getImageList(@Path("Login_id") String login_id);



    @Multipart
    @POST("/Gallery/{Login_id}")
    Call <ResponseBody> uploadImage(@Part MultipartBody.Part file, @Path("Login_id") String login_id);

    @DELETE("/Gallery/delete/{Login_id}/{url}")
    Call <ResponseBody> deleteImage(@Path("Login_id") String login_id, @Path("url") String url);


}
