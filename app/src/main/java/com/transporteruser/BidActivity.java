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

import com.transporteruser.adapter.BidReceivedShowAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.BidBinding;
import com.transporteruser.databinding.BiddingRecievedBinding;
import com.transporteruser.databinding.ReceiveBiddingAlrtdilogBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidActivity extends AppCompatActivity {
    BidBinding binding;
    ArrayList<Bid> bidList;
    BidReceivedShowAdapter adapter;
    String address;
    Lead lead;
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
              Call<Lead> call=  userApi.updateLead(lead.getLeadId(),lead);
              call.enqueue(new Callback<Lead>() {
                  @Override
                  public void onResponse(Call<Lead> call, Response<Lead> response) {
                      if (response.code()==200){
                          Toast.makeText(BidActivity.this, "Bid Accepted", Toast.LENGTH_SHORT).show();
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

}
