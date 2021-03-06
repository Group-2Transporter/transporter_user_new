package com.transporteruser.api;

import com.transporteruser.bean.States;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class StatesService {

    public static StatesService.StatesApi statesApi;

    public static StatesService.StatesApi getStatesApiIntance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAddress.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        if (statesApi == null)
            statesApi = retrofit.create(StatesService.StatesApi.class);
        return statesApi;
    }

    public interface StatesApi {

        @GET("states/")
        public Call<ArrayList<States>> getAllStates();


    }


}
