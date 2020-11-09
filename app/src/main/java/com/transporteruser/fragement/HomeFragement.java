package com.transporteruser.fragement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.transporteruser.databinding.HistoryFragementBinding;
import com.transporteruser.databinding.HomeFragmentBinding;

public class HomeFragement extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       HomeFragmentBinding binding = HomeFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}