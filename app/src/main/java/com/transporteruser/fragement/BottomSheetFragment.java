package com.transporteruser.fragement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.MainActivity;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.CreateNewLoadBinding;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    CreateNewLoadBinding binding;
    String check;
    Context context;
    UserService.UserApi userApi;
    Lead lead,lead1;
    String userName;
    public BottomSheetFragment(Context context){
        this.context = context;
    }
    public BottomSheetFragment(Context context,Lead lead,String check){
        this.check=check;
        lead1=lead;
        this.context =context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewLoadBinding.inflate(LayoutInflater.from(context));
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        userName = sp.getString("name","");
        userApi = UserService.getTransporterApiIntance();
        if (check!=null){
            String[] pickupAddress = lead1.getPickUpAddress().split(",");
            binding.pkStreet.setText(pickupAddress[0]);
            binding.pkCity.setText(pickupAddress[1]);
            binding.pkState.setText(pickupAddress[2]);
            binding.pickupContacte.setText(lead1.getContactForPickup());
            binding.materialtype.setText(lead1.getTypeOfMaterial());
            binding.weight.setText(lead1.getWeight());
            binding.etLastdate.setText(lead1.getDateOfCompletion());
            String[] deliveryAddress = lead1.getDeliveryAddress().split(",");
            binding.dvStreet.setText(deliveryAddress[0]);
            binding.dvCity.setText(deliveryAddress[1]);
            binding.dvState.setText(deliveryAddress[2]);
            binding.dvContacte.setText(lead1.getContactForDelivery());
            binding.btnCreateLoad.setText("update load");
        }

        binding.btnCreateLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String materialType = binding.materialtype.getText().toString();
                if (materialType.isEmpty()){
                    binding.materialtype.setError("please Enter Materialtype");
                    return;
                }
                String pkStreet = binding.pkStreet.getText().toString();
                if (pkStreet.isEmpty()){
                    binding.pkStreet.setError("please Enter Street");
                    return;
                }
                String pkCity = binding.pkCity.getText().toString();
                if (pkCity.isEmpty()){
                    binding.pkCity.setError("please Enter City");
                    return;
                }
                String pkState = binding.pkState.getText().toString();
                if (pkState.isEmpty()){
                    binding.pkState.setError("please Enter State");
                    return;
                }
                String dvStreet = binding.dvStreet.getText().toString();
                if (dvStreet.isEmpty()){
                    binding.dvStreet.setError("please Enter Street");
                    return;
                }
                String dvcity = binding.dvCity.getText().toString();
                if (dvcity.isEmpty()){
                    binding.dvCity.setError("please Enter City");
                    return;
                }
                String dvState = binding.dvState.getText().toString();
                if (dvState.isEmpty()){
                    binding.dvState.setError("please Enter State");
                    return;
                }
                String weight = binding.weight.getText().toString();
                if (weight.isEmpty()){
                    binding.weight.setError("please Enter weight");
                    return;
                }
                String pkContact = binding.pickupContacte.getText().toString();
                if (pkContact.isEmpty()){
                    binding.pickupContacte.setError("please Enter Contact");
                    return;
                }
                String dvContact = binding.dvContacte.getText().toString();
                if (dvContact.isEmpty()){
                    binding.dvContacte.setError("please Enter contact number");
                    return;
                }


                String etLasteDate = binding.etLastdate.getText().toString();
                if (etLasteDate.isEmpty()){
                    binding.etLastdate.setError("please Enter Date");
                    return;
                }
                String currentUser = FirebaseAuth.getInstance().getUid();
                lead = new Lead();
                lead.setPickUpAddress(pkStreet+","+pkCity+","+pkState);
                lead.setContactForPickup(pkContact);
                lead.setTypeOfMaterial(materialType);
                lead.setDeliveryAddress(dvStreet+","+dvcity+","+dvState);
                lead.setContactForPickup(pkContact);
                lead.setContactForDelivery(dvContact);
                lead.setWeight(weight);
                lead.setDateOfCompletion(etLasteDate);
                lead.setUserName(userName);
                lead.setUserId(currentUser);
                lead.setMaterialStatus("");
                lead.setStatus("create");
                String button = binding.btnCreateLoad.getText().toString();
                if(button.equalsIgnoreCase("update load")){

                    lead.setLeadId(lead1.getLeadId());
                    Call<Lead> call = userApi.updateLead(lead1.getLeadId(),lead);
                    call.enqueue(new Callback<Lead>() {
                        @Override
                        public void onResponse(Call<Lead> call, Response<Lead> response) {
                            if (response.code()==200){
                                Intent in = new Intent(getContext(), MainActivity.class);
                                startActivity(in);
                                dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Lead> call, Throwable t) {
                            Toast.makeText(getContext(), "Somthing Want wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(button.equalsIgnoreCase("Create Load")){
                    Toast.makeText(context, "Create Load", Toast.LENGTH_SHORT).show();
                    long timeStamp = Calendar.getInstance().getTimeInMillis();
                    lead.setTimestamp(timeStamp);
                    Call<Lead> call = userApi.createLead(lead);
                    call.enqueue(new Callback<Lead>() {
                        @Override
                        public void onResponse(Call<Lead> call, Response<Lead> response) {
                            if (response.code()==200){
                                dismiss();
                            }

                        }

                        @Override
                        public void onFailure(Call<Lead> call, Throwable t) {
                            Toast.makeText(getContext(), "Somthig Want wrong ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }


}
