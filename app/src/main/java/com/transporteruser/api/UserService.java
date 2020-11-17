package com.transporteruser.api;

import com.firebase.ui.auth.data.model.User;
import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class UserService {
    private final static String BASE_URL = "http://192.168.43.216:8080/";
    public static UserApi transportApi;

    public static UserApi getTransporterApiIntance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        if (transportApi == null)
            transportApi = retrofit.create(UserApi.class);
        return transportApi;
    }

    public interface UserApi {
        @GET("lead/createLead/{userId}")
        public Call<ArrayList<Lead>> getCreateLoadsByUserId(@Path("userId") String userId);

        @GET("lead/confirmLead/{userId}")
        public Call<ArrayList<Lead>> getConfirmLoadsByUserId(@Path("userId") String userId);

        @GET("bid/{leadId}")
        public Call<ArrayList<Bid>> getAllBidsByLeadId(@Path("leadId")String leadId);

        @GET("/lead/completeLead/{userId}")
        public Call<ArrayList<Lead>>getAllCompletedLead(@Path("userId")String userId);

        @POST("/lead/update/{leadId}")
        public  Call <Lead> updateLead(@Path("leadId")String leadId,@Body Lead lead);

    }
}

