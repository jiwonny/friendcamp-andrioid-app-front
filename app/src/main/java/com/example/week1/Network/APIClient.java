package com.example.week1.Network;

import android.app.Application;
import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private ApiService apiService;
    private static Context mContext;
    private static Retrofit retrofit;
    private static String mip;
    private static int mport;

    private static class SingletonHolder{
        private static APIClient INSTANCE = new APIClient(mContext, mip, mport);
    }

    public static APIClient getInstance(Context context, String ip, int port) {
        if (context != null){
            mContext = context;
            mip = ip;
            mport = port;
        }
        return SingletonHolder.INSTANCE;
    }


    public APIClient(Context context, String ip, int port) {
        String baseUrl = String.format("http://%s:%d", ip, port);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public APIClient createBaseApi() {
        apiService = retrofit.create(ApiService.class);
        return this;
    }


}
