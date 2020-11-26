package com.transporteruser;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.transporteruser.databinding.CreateNewLoadBinding;

public class CreateNewLoadActivity extends AppCompatActivity {
    CreateNewLoadBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateNewLoadBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}
