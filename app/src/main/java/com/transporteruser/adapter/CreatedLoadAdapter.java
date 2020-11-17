package com.transporteruser.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.BidActivity;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.CreatedLoadBinding;

import java.util.ArrayList;

public class CreatedLoadAdapter extends RecyclerView.Adapter<CreatedLoadAdapter.CreateLoadViewHolder> {
    OnRecyclerViewClickLisner lisner;
    ArrayList<Lead> list;
    Context context;
    public CreatedLoadAdapter(ArrayList<Lead> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CreateLoadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CreatedLoadBinding binding = CreatedLoadBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new CreateLoadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CreateLoadViewHolder holder, int position) {
        final Lead lead = list.get(position);
        holder.binding.tvAddress.setText(lead.getPickUpAddress() + " To " + lead.getDeliveryAddress());
        holder.binding.tvLastDate.setText(lead.getDateOfCompletion());
        holder.binding.tvTypeOfaterial.setText(lead.getTypeOfMaterial());
        if(lead.getBidCount() >= 1 && lead.getBidCount()<=9) {
            holder.binding.counter.setVisibility(View.VISIBLE);
            holder.binding.tvCount.setText("0" + lead.getBidCount());
        }
        else if (lead.getBidCount() == 0)
            holder.binding.counter.setVisibility(View.GONE);
        else {
            holder.binding.counter.setVisibility(View.VISIBLE);
            holder.binding.tvCount.setText("" + lead.getBidCount());
        }
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

            binding.ivMoreVert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(itemView.getContext(), binding.ivMoreVert);
                    final Menu menu = popupMenu.getMenu();
                    menu.add("Edit");
                    menu.add("Delete");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            String title = menuItem.getTitle().toString();
                            final int position = getAdapterPosition();
                            final Lead lead = list.get(position);
                            if (title.equals("Edit")) {
                                if(position!=RecyclerView.NO_POSITION && lisner!=null){
                                    lisner.onItemClick(lead,position,"Edit");
                                }
                            } else if (title.equals("Delete")) {
                                if(position!=RecyclerView.NO_POSITION && lisner!=null){
                                    lisner.onItemClick(lead,position,"Delete");
                                }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            binding.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final Lead lead = list.get(position);
                    if(position!=RecyclerView.NO_POSITION && lisner!=null){
                        lisner.onItemClick(lead,position,"bid");
                    }
                }
            });
        }
    }

    public interface OnRecyclerViewClickLisner {
        public void onItemClick(Lead lead, int position,String status);
    }

    public void onRecyclerViewClick(OnRecyclerViewClickLisner lisner) {
        this.lisner = lisner;
    }

}
