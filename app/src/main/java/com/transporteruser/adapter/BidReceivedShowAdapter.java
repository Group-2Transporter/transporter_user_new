package com.transporteruser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.BiddingRecievedBinding;

import java.util.ArrayList;

public class BidReceivedShowAdapter extends RecyclerView.Adapter<BidReceivedShowAdapter.BidReceivedViewHolder> {
    ArrayList<Bid>bidList;
    OnBidRecyclerViewClickLisner lisner;
    public BidReceivedShowAdapter (ArrayList<Bid>bidList){
        this.bidList = bidList;
    }
    @NonNull
    @Override
    public BidReceivedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BiddingRecievedBinding binding =BiddingRecievedBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new BidReceivedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final BidReceivedViewHolder holder, int position) {
        Bid bid = bidList.get(position);
        holder.binding.tvTransporterName.setText(bid.getTransporterName());
        holder.binding.tvRate.setText(""+bid.getAmount());
        holder.binding.tvLastDate.setText(bid.getEstimatedDate());

    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public class BidReceivedViewHolder extends RecyclerView.ViewHolder {
        BiddingRecievedBinding binding;
        public BidReceivedViewHolder(BiddingRecievedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bid bid = bidList.get(getAdapterPosition());
                    if (getAdapterPosition()!=RecyclerView.NO_POSITION && lisner!=null)
                        lisner.onItemClick(bid,getAdapterPosition());

                }
            });
        }
    }
    public interface OnBidRecyclerViewClickLisner {
        public void onItemClick(Bid bid, int position);
    }

    public void onRecyclerViewClick(OnBidRecyclerViewClickLisner lisner) {
        this.lisner = lisner;
    }


}
