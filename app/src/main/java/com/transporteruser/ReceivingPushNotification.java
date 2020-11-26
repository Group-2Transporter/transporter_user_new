package com.transporteruser;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivingPushNotification extends FirebaseMessagingService {
    String currentUserId;

    @Override
    public void onNewToken(String token) {
        updateToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> map = remoteMessage.getData();
        String title = map.get("title");
        String description = map.get("body");

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId = "My id";
        String channelName = "My channel";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this,channelId);
        nb.setContentTitle(title);
        nb.setContentText(description);
        nb.setSmallIcon(R.drawable.logoeagle);
        manager.notify(1,nb.build());

    }

    private void updateToken(final String token) {
        currentUserId = FirebaseAuth.getInstance().getUid();
        final UserService.UserApi userApi  = UserService.getTransporterApiIntance();
        Call<User> call = userApi.getCurrentUser(currentUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200){
                    User user =  response.body();
                    user.setToken(token);
                    Call<User> call1 = userApi.updateUser(user);
                    call1.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(ReceivingPushNotification.this, ""+t, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

}