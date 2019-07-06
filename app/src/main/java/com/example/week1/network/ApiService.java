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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {


    @GET("/Users/Name/{Name}/Phone/{Phone}")
    Call <User> getUserfrom_Name_Number(@Path("Name") String Name, @Path("Phone") String Phone);


    @GET("/Users/Name/{Name}/Login_id/{Login_id}")
    Call <User> getUserfrom_Name_LoginId(@Path("Name") String Name, @Path("Login_id") String Login_id);

    @GET
    Call <ResponseBody> getImage(@Url String url);


    @POST("/Users")
    Call <User> post_User(@Body User user);

    @PUT("/Users/Login_id/{Login_id}")
    Call <User>  update_User(@Path("Login_id") String Login_id, @Body User user);

    @Multipart
    @POST("/Gallery")
    Call <ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("Login_id") RequestBody login_id, @Part("Image_id") RequestBody image_id);




}
