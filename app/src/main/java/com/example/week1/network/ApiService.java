package com.example.week1.network;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    /* Contact */
    @GET("/Users/Name/{Name}/Phone/{Phone}")
    Call <User> getUserfrom_Name_Number(@Path("Name") String Name, @Path("Phone") String Phone);

    @POST("/Users")
    Call <User> post_User(@Body User user);






    /* Gallery */
    @GET("/Gallery/Login_id/{Login_id}/Image_id/{Image_id}")
    Call <String> getURlofImage(@Path("Login_id") String login_id, @Path("Image_id") String image_id);

    @GET("/{ImageName}")
    Call <ResponseBody> getImage(@Url String url, @Path("ImageName") String image_name);

    @POST("/Gallery/Login_id/{Login_id}")
    Call <Image> req_uploadImage(@Path("Login_id") String login_id);

    @Multipart
    @POST("/Gallery/{Login_id}/{Image_id}")
    Call <ResponseBody> uploadImage(@Part MultipartBody.Part file, @Path("Login_id") String login_id, @Path("Image_id") String image_id);




}
