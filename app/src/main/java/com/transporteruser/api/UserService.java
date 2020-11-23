package com.transporteruser.api;

import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.bean.Transporter;
import com.transporteruser.bean.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public class UserService {
    private final static String BASE_URL = "http://192.168.0.111:8080/";
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
        @Multipart
        @POST("/user/")
        public Call<User> saveProfile(@Part MultipartBody.Part file,
                                      @Part("userId") RequestBody userId,
                                      @Part("name") RequestBody name,
                                      @Part("address") RequestBody address,
                                      @Part("contactNumber") RequestBody contactNumber,
                                      @Part("token") RequestBody token);

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

        @GET("user/{id}")
        public Call<User> getCurrentUser(@Path("id") String id);

        @GET("transporter/{id}")
        public Call<Transporter> getTransporter(@Path("id") String id);

        @GET("user/update")
        public Call<User> updateUser(@Body User user);

        @POST("/lead/")
        public Call<Lead> createLead(@Body Lead lead);

        @GET("/lead/{leadId}")
        public Call<Lead> singleLeadById(@Path("leadId")String leadId);

    }
}

