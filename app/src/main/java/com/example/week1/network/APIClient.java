package com.example.week1.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;

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

    /*--------------------------------- IMPLEMENTATION -------------------------------------------*/


    /* GET implementation */

    public void getUserfrom_LoginId(String login_id, final APICallback callback){
        try {
            Response<User> response = apiService.getUserfrom_LoginId(login_id).execute();
            if (response.isSuccessful()) {
                callback.onSuccess(response.code(), response.body());
            } else {
                callback.onFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getUserfrom_Name_Number(String name, String number ,final APICallback callback) {
        try {
            Response<User> response = apiService.getUserfrom_Name_Number(name, number).execute();
            if (response.isSuccessful()) {
                callback.onSuccess(response.code(), response.body());
            } else {
                callback.onFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserfrom_Name_LoginId(String Name, String Login_Id ,final APICallback callback) {
        try {
            Response<User> response = apiService.getUserfrom_Name_LoginId(Name, Login_Id).execute();
            if (response.isSuccessful()) {
                callback.onSuccess(response.code(), response.body());
            } else {
                callback.onFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchUserfrom_LoginId(String Login_id, final APICallback callback){
        apiService.searchUserfrom_LoginId(Login_id).enqueue(new Callback <List<User>>() {
            @Override
            public void onResponse(Call <List<User>> call, Response <List<User>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call <List<User>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getImage(String login_id, String url, final APICallback callback){
        try{
            Response<Image_f> response = apiService.getImage(login_id,url).execute();
            if (response.isSuccessful()) {
                callback.onSuccess(response.code(), response.body());
                Log.d("commmmmmentntntnntnttn", response.body().toString());
            } else {
                callback.onFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getImageList(String login_id, final APICallback callback){
        try {
            Response<List<Image_f>> response = apiService.getImageList(login_id).execute();
            if (response.isSuccessful()) {
                callback.onSuccess(response.code(), response.body());
            } else {
                callback.onFailure(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void uploadImage(MultipartBody.Part file, String login_id, String name, final APICallback callback){
        apiService.uploadImage(file, login_id, name).enqueue(new Callback<ResponseBody>() {
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



    /* PUT implementation */

    public void update_User(String Login_id, User user, final APICallback callback){
        apiService.update_User(Login_id, user).enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response){
                if(response.isSuccessful()){
                    callback.onSuccess(response.code(), response.body());
                }else{
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t){callback.onError(t);}
        });
    }

    public void update_UserProfile(String login_id, String profile, final APICallback callback) {
        apiService.update_UserProfile(login_id, profile).enqueue(new Callback<User>() {
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

    public void update_Comment(String login_id, String url, List<Comment> comments, final APICallback callback){
        apiService.update_Comment(login_id,url, comments).enqueue(new Callback<Image_f>() {
            @Override
            public void onResponse(Call<Image_f> call, Response<Image_f> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<Image_f> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /* DELETE implementation */


    public void deleteImage(String login_id, String url, final APICallback callback){
        apiService.deleteImage(login_id,url).enqueue(new Callback<ResponseBody>() {
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
