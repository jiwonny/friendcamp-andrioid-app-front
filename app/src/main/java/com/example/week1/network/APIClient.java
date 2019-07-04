package com.example.week1.network;

import android.content.Context;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
        apiService = create(ApiService.class);
        return this;
    }


    /*
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    public void getUserfromID(String Login_id, final APICallback callback){
        apiService.getUserfromID(Login_id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void post_User(User user, final APICallback callback){
        apiService.post_User(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


}
