package com.transporteruser;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.transporteruser.adapter.BidReceivedShowAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.bean.Transporter;
import com.transporteruser.databinding.BidBinding;
import com.transporteruser.databinding.BiddingRecievedBinding;
import com.transporteruser.databinding.ReceiveBiddingAlrtdilogBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidActivity extends AppCompatActivity {
    BidBinding binding;
    ArrayList<Bid> bidList;
    BidReceivedShowAdapter adapter;
    String address;
    Lead lead;
    Transporter transporter;
    UserService.UserApi userApi;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BidBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        lead = (Lead) intent.getSerializableExtra("lead");;
        address = intent.getStringExtra("location");
        userApi = UserService.getTransporterApiIntance();
        Call<ArrayList<Bid>>call =  userApi.getAllBidsByLeadId(lead.getLeadId());
        call.enqueue(new Callback<ArrayList<Bid>>() {
            @Override
            public void onResponse(Call<ArrayList<Bid>> call, Response<ArrayList<Bid>> response) {
                if(response.code() == 200){
                    bidList = response.body();
                    adapter = new BidReceivedShowAdapter(bidList);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setLayoutManager(new LinearLayoutManager(BidActivity.this));
                    adapter.onRecyclerViewClick(new BidReceivedShowAdapter.OnBidRecyclerViewClickLisner() {
                        @Override
                        public void onItemClick(Bid bid, int position) {
                            Toast.makeText(BidActivity.this, ""+bid.getAmount(), Toast.LENGTH_SHORT).show();
                            getAlertDialog(bid);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bid>> call, Throwable t) {
                Toast.makeText(BidActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });


        binding.toolbar.setTitle(address);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void getAlertDialog(final Bid bid){
        getTransporterById(bid.getTransporterId());
        final AlertDialog ab = new AlertDialog.Builder(this).create();
        ReceiveBiddingAlrtdilogBinding binding = ReceiveBiddingAlrtdilogBinding.inflate(LayoutInflater.from(this));
        ab.setView(binding.getRoot());
        ab.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.tvAddress.setText(address);
        binding.tvdate.setText(bid.getEstimatedDate());
        binding.tvRate.setText(""+bid.getAmount());
        binding.tvRemark.setText(bid.getRemark());
        binding.tvTransporterName.setText(bid.getTransporterName());
        binding.tvMaterial.setText(bid.getMaterialType());
        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab.dismiss();
            }
        });
        ab.setCancelable(false);
        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lead.setDealLockedWith(bid.getTransporterId());
                lead.setTransporterName(bid.getTransporterName());
                lead.setStatus("confirmed");
                lead.setAmount(bid.getAmount());
                lead.setRemark(bid.getRemark());
              Call<Lead> call=  userApi.updateLead(lead.getLeadId(),lead);
              call.enqueue(new Callback<Lead>() {
                  @Override
                  public void onResponse(Call<Lead> call, Response<Lead> response) {
                      if (response.code()==200){
                          deleteBidsByLead(response.body().getLeadId());
                          notification(transporter.getToken());
                          Toast.makeText(BidActivity.this, "Bid Accepted", Toast.LENGTH_SHORT).show();
                          Intent in = new Intent(BidActivity.this,MainActivity.class);
                          startActivity(in);
                          finish();
                      }
                  }

                  @Override
                  public void onFailure(Call<Lead> call, Throwable t) {

                  }
              });

            }
        });
        ab.show();
    }

    private void getTransporterById(String transporterId) {
        userApi.getTransporter(transporterId).enqueue(new Callback<Transporter>() {
            @Override
            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                if(response.code() == 200){
                    transporter = response.body();
                }
            }

            @Override
            public void onFailure(Call<Transporter> call, Throwable t) {
                Toast.makeText(BidActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void deleteBidsByLead(String leadId){
        userApi.deleteBidsByLeadId(leadId).enqueue(new Callback<ArrayList<Bid>>() {
            @Override
            public void onResponse(Call<ArrayList<Bid>> call, Response<ArrayList<Bid>> response) {
                if(response.code() == 200){

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bid>> call, Throwable t) {
                Toast.makeText(BidActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void notification(String token) {
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title","Bid Accepted by  : "+lead.getUserName());
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
            Toast.makeText(this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
