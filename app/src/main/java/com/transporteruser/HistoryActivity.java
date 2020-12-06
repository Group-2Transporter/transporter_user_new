package com.transporteruser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.adapter.HistoryAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.HistoryActivityBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    HistoryAdapter adapter;
    String currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final HistoryActivityBinding binding = HistoryActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        UserService.UserApi userApi = UserService.getTransporterApiIntance();
        currentUser= FirebaseAuth.getInstance().getUid();
        Call<ArrayList<Lead>> call = userApi.getAllCompletedLead(currentUser);
        call.enqueue(new Callback<ArrayList<Lead>>() {
            @Override
            public void onResponse(Call<ArrayList<Lead>> call, Response<ArrayList<Lead>> response) {
                if (response.code()==200){
                    ArrayList<Lead> leadList = response.body();
                    adapter = new HistoryAdapter(leadList);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Lead>> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
