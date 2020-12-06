package com.transporteruser.fragement;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.MainActivity;
import com.transporteruser.ProgressBar;
import com.transporteruser.adapter.CreatedLoadAdapter;
import com.transporteruser.api.StatesService;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.bean.Message;
import com.transporteruser.bean.States;
import com.transporteruser.bean.Token;
import com.transporteruser.bean.User;
import com.transporteruser.databinding.CreateNewLoadBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    CreateNewLoadBinding binding;
    String check;
    Context context;
    int position;
    UserService.UserApi userApi;
    Lead lead, lead1;
    String userName;
    String[] pickupAddress;
    String[] deliveryAddress;
    CreatedLoadAdapter adapter;
    ArrayList<Lead> leadList;
    ArrayList<String> stateList = new ArrayList<>();
    ArrayList<Token> tokenList;
    private int year,month,day;
    DatePickerDialog datePickerDialog;
    public BottomSheetFragment(Context context, CreatedLoadAdapter adapter,ArrayList<Lead> leadList) {
        this.context = context;
        this.adapter = adapter;
        this.leadList =leadList;
    }
    public BottomSheetFragment(){}

    public BottomSheetFragment(Context context, Lead lead, String check) {
        this.check = check;
        lead1 = lead;
        this.context = context;
        deliveryAddress = lead1.getDeliveryAddress().split(",");
        pickupAddress = lead1.getPickUpAddress().split(",");
    }
    public BottomSheetFragment(Context context) {
        this.context = context;
        leadList = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewLoadBinding.inflate(LayoutInflater.from(context));
        SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
        stateList.add("States");
        getStatesList();



        userApi = UserService.getTransporterApiIntance();
        userApi.getCurrentUser(FirebaseAuth.getInstance().getUid()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code() == 200){
                    userName = response.body().getName();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        token();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stateList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.pkState.setAdapter(dataAdapter);
        binding.dvState.setAdapter(dataAdapter);
        binding.pkState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.pkState.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.dvState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.dvState.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (check != null) {
            binding.pkStreet.setText(pickupAddress[0]);
            binding.pkCity.setText(pickupAddress[1]);
            binding.pickupContacte.setText(lead1.getContactForPickup());
            binding.materialtype.setText(lead1.getTypeOfMaterial());
            binding.weight.setText(lead1.getWeight());
            binding.etLastdate.setText(lead1.getDateOfCompletion());
            binding.dvStreet.setText(deliveryAddress[0]);
            binding.dvCity.setText(deliveryAddress[1]);

            binding.dvContacte.setText(lead1.getContactForDelivery());
            binding.btnCreateLoad.setText("update load");

        }
        binding.etLastdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog =new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.etLastdate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        binding.btnCreateLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String materialType = binding.materialtype.getText().toString();
                if (materialType.isEmpty()) {
                    binding.materialtype.setError("please Enter Materialtype");
                    return;
                }
                String pkStreet = binding.pkStreet.getText().toString();
                if (pkStreet.isEmpty()) {
                    binding.pkStreet.setError("please Enter Street");
                    return;
                }
                String pkCity = binding.pkCity.getText().toString();
                if (pkCity.isEmpty()) {
                    binding.pkCity.setError("please Enter City");
                    return;
                }
                String pkState = binding.pkState.getSelectedItem().toString();
                if (pkState.isEmpty()) {
                    Toast.makeText(context, "please select Pickup State", Toast.LENGTH_SHORT).show();
                    return;
                }
                String dvStreet = binding.dvStreet.getText().toString();
                if (dvStreet.isEmpty()) {
                    binding.dvStreet.setError("please Enter Street");
                    return;
                }
                String dvcity = binding.dvCity.getText().toString();
                if (dvcity.isEmpty()) {
                    binding.dvCity.setError("please Enter City");
                    return;
                }
                String dvState = binding.dvState.getSelectedItem().toString();
                if (dvState.isEmpty()) {
                    Toast.makeText(context, "please select Delivery State", Toast.LENGTH_SHORT).show();
                    return;
                }
                String weight = binding.weight.getText().toString();
                if (weight.isEmpty()) {
                    binding.weight.setError("please Enter weight");
                    return;
                }
                String pkContact = binding.pickupContacte.getText().toString();
                if (pkContact.length()<=9) {
                    binding.pickupContacte.setError("please Enter Contact");
                    return;
                }
                String dvContact = binding.dvContacte.getText().toString();
                if (dvContact.length()<=9) {
                    binding.dvContacte.setError("please Enter contact number");
                    return;
                }


                String etLasteDate = binding.etLastdate.getText().toString();
                if (etLasteDate.isEmpty()) {
                    binding.etLastdate.setError("please Enter Date");
                    return;
                }
                String currentUser = FirebaseAuth.getInstance().getUid();
                lead = new Lead();
                lead.setPickUpAddress(pkStreet + "," + pkCity + "," + pkState);
                lead.setContactForPickup(pkContact);
                lead.setTypeOfMaterial(materialType);
                lead.setDeliveryAddress(dvStreet + "," + dvcity + "," + dvState);
                lead.setContactForPickup(pkContact);
                lead.setContactForDelivery(dvContact);
                lead.setWeight(weight);
                lead.setDateOfCompletion(etLasteDate);
                lead.setUserName(userName);
                lead.setUserId(currentUser);
                lead.setMaterialStatus("");
                lead.setStatus("create");
                String button = binding.btnCreateLoad.getText().toString();
                if (button.equalsIgnoreCase("update load")) {

                    lead.setLeadId(lead1.getLeadId());
                    Call<Lead> call = userApi.updateLead(lead1.getLeadId(), lead);
                    call.enqueue(new Callback<Lead>() {
                        @Override
                        public void onResponse(Call<Lead> call, Response<Lead> response) {
                            if (response.code() == 200) {
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

                } else if (button.equalsIgnoreCase("Create Load")) {
                    Toast.makeText(context, "Create Load", Toast.LENGTH_SHORT).show();
                    long timeStamp = Calendar.getInstance().getTimeInMillis();
                    lead.setTimestamp(timeStamp);
                    Call<Lead> call = userApi.createLead(lead);
                    call.enqueue(new Callback<Lead>() {
                        @Override
                        public void onResponse(Call<Lead> call, Response<Lead> response) {
                            if (response.code() == 200) {
                                sendNotification(response.body());
                                if(leadList != null) {
                                    leadList.add(response.body());
                                    adapter.notifyDataSetChanged();
                                }else{
                                    Intent i = new Intent(getContext(),MainActivity.class);
                                    startActivity(i);
                                }
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
        binding.bottomclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return binding.getRoot();
    }

    private void getStatesList() {
        final ProgressBar pd = new ProgressBar(getActivity());
        pd.startLoadingDialog();
        final StatesService.StatesApi statesApi = StatesService.getStatesApiIntance();
        statesApi.getAllStates().enqueue(new Callback<ArrayList<States>>() {
            @Override
            public void onResponse(Call<ArrayList<States>> call, Response<ArrayList<States>> response) {
                if(response.code() == 200){
                    for(States s : response.body()){
                        stateList.add(s.getStateName());
                    }
                }
                if(check !=  null){
                    position = stateList.indexOf(pickupAddress[2]);

                }
                pd.dismissDialog();
            }

            @Override
            public void onFailure(Call<ArrayList<States>> call, Throwable t) {
                pd.dismissDialog();
                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void token() {
        userApi.getTokens().enqueue(new Callback<ArrayList<Token>>() {
            @Override
            public void onResponse(Call<ArrayList<Token>> call, Response<ArrayList<Token>> response) {
                if(response.code() == 200){
                    tokenList = response.body();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Token>> call, Throwable t) {

            }
        });
    }
    private void sendNotification(Lead lead){
        for(Token token : tokenList){
            notification(lead,token.getToken());
        }
    }
    private void notification(Lead lead,String token) {
        try{
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title","Create New Load Type : "+lead.getTypeOfMaterial());
            data.put("body", "Address : "+lead.getPickUpAddress());

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
            Toast.makeText(context, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
