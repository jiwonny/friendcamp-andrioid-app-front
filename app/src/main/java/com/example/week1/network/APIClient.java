package com.example.week1.network;

import android.content.Context;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
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

    /* GET implementation */
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

    public void getImage(String url, final APICallback callback){
        apiService.getImage(url).enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /* POST implementation */

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

    public void uploadImage(MultipartBody.Part file, RequestBody login_id, RequestBody image_id, final APICallback callback){
        apiService.uploadImage(file, login_id, image_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    callback.onSuccess(response.code(),  response);
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


}
