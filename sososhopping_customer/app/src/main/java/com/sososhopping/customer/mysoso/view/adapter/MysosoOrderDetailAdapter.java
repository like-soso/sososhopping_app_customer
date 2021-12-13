package com.sososhopping.customer.mysoso.view.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.databinding.ItemOrderDetailItemBinding;
import com.sososhopping.customer.mysoso.dto.OrderDetailItemDto;

import java.util.ArrayList;

public class MysosoOrderDetailAdapter extends RecyclerView.Adapter<MysosoOrderDetailAdapter.ViewHolder>{
    ArrayList<OrderDetailItemDto> items = new ArrayList<>();
    ItemOrderDetailItemBinding binding;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemOrderDetailItemBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<OrderDetailItemDto> i){
        this.items = i;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderDetailItemBinding binding;

        public ViewHolder(ItemOrderDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(OrderDetailItemDto detailDto) {
            binding.textViewGoods.setText(detailDto.getItemName());
            if(TextUtils.isEmpty(detailDto.getDescription())){
                binding.textViewItemDescription.setVisibility(View.GONE);
            }else{
                binding.textViewItemDescription.setText(detailDto.getDescription());
            }
            binding.textViewItemNum.setText(String.valueOf(detailDto.getQuantity()));
            binding.textViewPrice.setText(detailDto.getTotalPrice()+"원");
        }
    }
}
