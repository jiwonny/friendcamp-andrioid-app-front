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
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {



    /* Contact */
    @GET("/Users/Name/{Name}/Phone/{Phone}")
    Call <User> getUserfrom_Name_Number(@Path("Name") String Name, @Path("Phone") String Phone);


    @GET("/Users/Name/{Name}/Login_id/{Login_id}")
    Call <User> getUserfrom_Name_LoginId(@Path("Name") String Name, @Path("Login_id") String Login_id);

    @GET("/Users/search/{Login_id}")
    Call <List<User>> searchUserfrom_LoginId(@Path("Login_id") String Login_id);

    @POST("/Users")
    Call <User> post_User(@Body User user);

    @PUT("/Users/Login_id/{Login_id}")
    Call <User> update_User(@Path("Login_id") String Login_id, @Body User user);

    @PUT("/Users/Profile/{Login_id}/{Profile}")
    Call <User> update_UserProfile(@Path("Login_id") String Login_id, @Path("Profile") String Profile);




    /* Gallery */


    @GET("/Gallery/getImageList/{Login_id}")
    Call <List<Image_f>> getImageList(@Path("Login_id") String login_id);

    @Multipart
    @POST("/Gallery/{Login_id}/{Name}")
    Call <ResponseBody> uploadImage(@Part MultipartBody.Part file, @Path("Login_id") String login_id, @Path("Name") String name);

    @DELETE("/Gallery/delete/{Login_id}/{url}")
    Call <ResponseBody> deleteImage(@Path("Login_id") String login_id, @Path("url") String url);


}
