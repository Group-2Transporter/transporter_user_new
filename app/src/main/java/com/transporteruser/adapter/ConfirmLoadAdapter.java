package com.transporteruser.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.ChatActivity;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.ConfirmLoadBinding;
import com.transporteruser.databinding.CreatedLoadBinding;
import com.transporteruser.databinding.MaterialStatusBinding;

import java.util.ArrayList;

public class ConfirmLoadAdapter extends RecyclerView.Adapter<ConfirmLoadAdapter.ConfirmViewHolder> {
    ArrayList<Lead> list;
    public ConfirmLoadAdapter (ArrayList<Lead> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ConfirmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConfirmLoadBinding binding = ConfirmLoadBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ConfirmViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConfirmViewHolder holder, final int position) {
        final Lead lead = list.get(position);
        holder.binding.tvAddress.setText(lead.getPickUpAddress()+" To "+lead.getDeliveryAddress());
        holder.binding.tvLastDate.setText(lead.getDateOfCompletion());
        holder.binding.tvTypeOfaterial.setText(lead.getTypeOfMaterial());
        holder.binding.tvTransporterName.setText(lead.getTransporterName());
        holder.binding.tvWeight.setText(lead.getWeight());
        holder.binding.ivMoreVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), holder.binding.ivMoreVert);
                final Menu menu = popupMenu.getMenu();
                menu.add("Material Status");
                menu.add("Chat with Transporter");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String title = menuItem.getTitle().toString();
                        if (title.equals("Material Status")) {
                            getAlertDialog(lead,holder.itemView.getContext());
                        } else if (title.equals("Chat with Transporter")) {
                            Intent in = new Intent(holder.itemView.getContext(), ChatActivity.class);
                            in.putExtra("transporterId",lead.getDealLockedWith());
                            in.putExtra("leadId",lead.getLeadId());
                            holder.itemView.getContext().startActivity(in);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class CreateLoadViewHolder extends RecyclerView.ViewHolder {
        CreatedLoadBinding binding;

        public CreateLoadViewHolder(final CreatedLoadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


    public class ConfirmViewHolder extends RecyclerView.ViewHolder{
        ConfirmLoadBinding binding;
        public ConfirmViewHolder(ConfirmLoadBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    private void getAlertDialog(Lead lead, Context context){
        final AlertDialog ab = new AlertDialog.Builder(context).create();
        MaterialStatusBinding alertBinding = MaterialStatusBinding.inflate(LayoutInflater.from(context));
        ab.setView(alertBinding.getRoot());
        ab.setCancelable(false);
        ab.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(lead.getMaterialStatus().equalsIgnoreCase("loaded")){
            alertBinding.checkLoaded.setVisibility(View.VISIBLE);
        }else if(lead.getMaterialStatus().equalsIgnoreCase("inTransit")){
            alertBinding.checkLoaded.setVisibility(View.VISIBLE);
            alertBinding.checkedTransit.setVisibility(View.VISIBLE);
        }else if(lead.getMaterialStatus().equalsIgnoreCase("reached")){
            alertBinding.checkLoaded.setVisibility(View.VISIBLE);
            alertBinding.checkedTransit.setVisibility(View.VISIBLE);
            alertBinding.checkedReached.setVisibility(View.VISIBLE);
        }
        alertBinding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ab.dismiss();
            }
        });
        ab.show();

    }
}