package com.transporteruser.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.HistoryBinding;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistroyViewHoldere> {
    ArrayList<Lead>list;
    public HistoryAdapter(ArrayList<Lead>list){
    this.list=list;

    }
    HistoryBinding binding;
    @NonNull
    @Override
    public HistroyViewHoldere onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HistoryBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new HistroyViewHoldere(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistroyViewHoldere holder, int position) {
        Lead lead =list.get(position);
        holder.binding.tvAddress.setText(lead.getPickUpAddress()+" To "+ lead.getDeliveryAddress());
        holder.binding.tvAddress.setSelected(true);
        holder.binding.tvTypeOfmaterial.setText(lead.getTypeOfMaterial());
        holder.binding.tvTransporterName.setText(lead.getTransporterName());
        holder.binding.tvDate.setText(lead.getDateOfCompletion());
        holder.binding.tvWeight.setText(lead.getWeight());
        //holder.binding.tvrate.setText(lead.);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HistroyViewHoldere extends RecyclerView.ViewHolder {
        HistoryBinding binding;
        public HistroyViewHoldere(HistoryBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

}
