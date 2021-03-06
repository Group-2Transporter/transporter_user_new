package com.transporteruser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.transporteruser.adapter.MessageAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Message;
import com.transporteruser.bean.Transporter;
import com.transporteruser.databinding.ChatActivityBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    ChatActivityBinding binding;
    String transporterId;
    String currentUserId;
    DatabaseReference firebaseDatabase;
    MessageAdapter adapter;
    ArrayList<Message>al;
    Transporter transporter;
    String leadId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Intent in = getIntent();
        currentUserId = FirebaseAuth.getInstance().getUid();
        transporterId = in.getStringExtra("transporterId");
        leadId = in.getStringExtra("leadId");
        UserService.UserApi userApi = UserService.getTransporterApiIntance();
        Call<Transporter> call = userApi.getTransporter(transporterId);
        if (InternetUtility.isNetworkConnected(this)) {
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            call.enqueue(new Callback<Transporter>() {
                @Override
                public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                    if(response.code() == 200){
                        transporter = response.body();
                        binding.tvName.setText(transporter.getName());
                        Picasso.get().load(transporter.getImageUrl()).into(binding.civUser);
                    }
                }

                @Override
                public void onFailure(Call<Transporter> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = binding.etMessage.getText().toString();
                if (message.isEmpty()) {
                    return;
                }
                final Calendar c = Calendar.getInstance();
                long timeStamp = c.getTimeInMillis();
                if (InternetUtility.isNetworkConnected(ChatActivity.this)) {
                    final String messageId = FirebaseDatabase.getInstance().getReference().push().getKey();
                    binding.etMessage.setText("");
                    final Message msg = new Message(messageId, currentUserId, transporterId, message, timeStamp);
                    firebaseDatabase.child("Messages").child(leadId).child(currentUserId).child(transporterId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseDatabase.child("Messages").child(leadId).child(transporterId).child(currentUserId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(ChatActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
                                        }else if(task.isSuccessful()){
                                            notification(msg);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else
                    Toast.makeText(ChatActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();

            }
        });
        al = new ArrayList<>();
        firebaseDatabase.child("Messages").child(leadId).child(currentUserId).child(transporterId).orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    al.add(msg);
                    adapter = new MessageAdapter(ChatActivity.this,al);
                    binding.rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notification(Message message) {

        String token = transporter.getToken();
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title","new Message send");
            data.put("body", "From "+message.getMessage());

            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to",token);

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String api_key_header_value = "Key=AAAARoiepkM:APA91bHVqjULid8wCt5Sf_EwC4Y0engqgafGEhEdMMhlb2Ix2TbXQldPyAffP7hEPDxLSBoPo1jizb_hX2hFpADDEaNCa5prcG9fR8uPvJt4xEfF-hYQEKmbG8Gn5zouwyRKAXQ98YCZ";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", api_key_header_value);
                    return headers;
                }
            };
            queue.add(request);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}