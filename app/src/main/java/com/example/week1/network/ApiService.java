package com.example.week1.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {


    @GET("/Users/{Login_id}")
    Call <User> getUserfromID(@Path("Login_id") String Login_id);

    @POST("/Users")
    Call <User> post_User(@Body User user);




}
