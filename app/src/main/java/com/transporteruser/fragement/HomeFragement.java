package com.transporteruser.fragement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.BidActivity;
import com.transporteruser.MainActivity;
import com.transporteruser.adapter.ConfirmLoadAdapter;
import com.transporteruser.adapter.CreatedLoadAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.HomeFragmentBinding;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragement extends Fragment {

    ConfirmLoadAdapter adapter;
    CreatedLoadAdapter createdLoadAdapter;
    String currentUserId;
    String spin;
    ProgressDialog pd;
    ArrayList<Lead> createLeadList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final HomeFragmentBinding binding = HomeFragmentBinding.inflate(getLayoutInflater());
        currentUserId = FirebaseAuth.getInstance().getUid();
        //spinner code here
        final ArrayList<String> list = new ArrayList<>();
        list.add("New Created Loads");
        list.add("Confirmed Loads");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(arrayAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
                spin = binding.spinner.getSelectedItem().toString();
                UserService.UserApi userApi = UserService.getTransporterApiIntance();
                if(spin.equals("New Created Loads")){
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Please Wait......");
                    //pd.setCancelable(false);
                    pd.show();

                    Call<ArrayList<Lead>> call = userApi.getCreateLoadsByUserId(currentUserId);
                    call.enqueue(new Callback<ArrayList<Lead>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Lead>> call, Response<ArrayList<Lead>> response) {
                            Toast.makeText(getContext(), ""+response.code(), Toast.LENGTH_SHORT).show();
                            if(response.code()==200) {
                                createLeadList = response.body();
                                createdLoadAdapter = new CreatedLoadAdapter(createLeadList,getContext());
                                binding.rv.setAdapter(createdLoadAdapter);
                                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                                createdLoadAdapter.onRecyclerViewClick(new CreatedLoadAdapter.OnRecyclerViewClickLisner() {
                                    @Override
                                    public void onItemClick(Lead lead, int position, String status) {
                                        if(status.equals("Edit")){
                                            BottomSheetFragment bottom = new BottomSheetFragment(getContext(),lead,"edit");
                                            bottom.show(getFragmentManager(),"");

                                        }else if (status.equals("Delete")){
                                            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                        }else if (status.equals("bid")){
                                            Intent in = new Intent(getContext(), BidActivity.class);
                                            in.putExtra("lead",lead);
                                            String address = lead.getPickUpAddress()+" To "+lead.getDeliveryAddress();
                                            in.putExtra("location",address);
                                            startActivity(in);
                                        }
                                    }
                                });
                            }
                            else if (response.code()==404){
                                Toast.makeText(getContext(), "No new load Found", Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();
                        }
                        @Override
                        public void onFailure(Call<ArrayList<Lead>> call, Throwable t) {
                            Toast.makeText(getContext(), ""+t, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
                else if (spin.equals("Confirmed Loads")){
                    pd = new ProgressDialog(getContext());
                    pd.setMessage("Please Wait......");
                    pd.show();
                    Toast.makeText(getContext(), "Confirm Loads", Toast.LENGTH_SHORT).show();
                    Call<ArrayList<Lead>> call  = userApi.getConfirmLoadsByUserId(currentUserId);
                    call.enqueue(new Callback<ArrayList<Lead>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Lead>> call, Response<ArrayList<Lead>> response) {
                            if(response.code()==200) {
                                adapter = new ConfirmLoadAdapter(response.body());
                                binding.rv.setAdapter(adapter);
                                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Lead>> call, Throwable t) {
                            Toast.makeText(getContext(), "Soething went wrong "+t, Toast.LENGTH_SHORT).show();
                            Log.e("Error : ","===>"+t);
                            pd.dismiss();
                        }
                    });

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.floting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in = new Intent(MainActivity.this,CreateNewLoadActivity.class);
//                startActivity(in);
                BottomSheetFragment bottom = new BottomSheetFragment(getContext());
                bottom.show(getFragmentManager(),"");
            }
        });



        return binding.getRoot();
    }
}