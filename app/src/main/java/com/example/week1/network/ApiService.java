package com.example.week1.network;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {



    @GET("/Users/{Login_id}")
    Call <User> getUserfromID(@Query("Login_id") String Login_id);

    @GET
    Call <Image> getImage(@Url String url);

    @POST("/Users")
    Call <User> post_User(@Body User user);

    @Multipart
    @POST("/Gallery")
    Call <ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("Login_id") RequestBody login_id, @Part("Image_id") RequestBody image_id);




}
