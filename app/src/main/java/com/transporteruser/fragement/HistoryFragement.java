package com.transporteruser.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.adapter.HistoryAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.HistoryFragementBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragement extends Fragment {
    HistoryAdapter adapter;
    String currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentUser = FirebaseAuth.getInstance().getUid();
        final HistoryFragementBinding binding = HistoryFragementBinding.inflate(getLayoutInflater());
        UserService.UserApi userApi = UserService.getTransporterApiIntance();
        Call<ArrayList<Lead>> call = userApi.getAllCompletedLead(currentUser);
        call.enqueue(new Callback<ArrayList<Lead>>() {
            @Override
            public void onResponse(Call<ArrayList<Lead>> call, Response<ArrayList<Lead>> response) {
                if (response.code()==200){
                    ArrayList<Lead> leadList = response.body();
                    adapter = new HistoryAdapter(leadList);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Lead>> call, Throwable t) {

            }
        });
        return binding.getRoot();
    }
}