package com.sososhopping.customer.mysoso.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ItemMysosoPointDetailBinding;
import com.sososhopping.customer.mysoso.model.PointDetailModel;

import java.util.ArrayList;

public class MysosoPointDetailAdapter extends RecyclerView.Adapter<MysosoPointDetailAdapter.ViewHolder>{
    ArrayList<PointDetailModel> items = new ArrayList<>();
    ItemMysosoPointDetailBinding binding;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemMysosoPointDetailBinding.inflate(inflater, parent,false);
        return new MysosoPointDetailAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<PointDetailModel> items){
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemMysosoPointDetailBinding binding;

        public ViewHolder(ItemMysosoPointDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(PointDetailModel pointDetailModel){
            binding.textViewDate.setText(DateFormatMethod.dateFormatTimetoDay(pointDetailModel.getCreatedAt()));
            binding.textViewChanged.setText(pointDetailModel.getPointAmount()+"p");
            if(pointDetailModel.getPointAmount() < 0){
                binding.textViewType.setText("차감");
            }else{
                binding.textViewType.setText("적립");
            }
            binding.textViewTotal.setText(pointDetailModel.getResultAmount()+"p");
        }

    }
}