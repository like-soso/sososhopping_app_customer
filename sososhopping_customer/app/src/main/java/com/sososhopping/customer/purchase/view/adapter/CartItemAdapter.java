package com.sososhopping.customer.purchase.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sososhopping.customer.R;
import com.sososhopping.customer.purchase.dto.CartItemDto;
import com.sososhopping.customer.databinding.ItemCartItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{

    ArrayList<CartItemDto> items;
    com.sososhopping.customer.databinding.ItemCartItemBinding binding;
    OnItemClickListenerCartItem itemClickListener;
    int adapterCase;

    public CartItemAdapter(ArrayList<CartItemDto> items, int adapterCase){
        this.items = items;
        this.adapterCase = adapterCase;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemCartItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListenerCartItem listener){
        this.itemClickListener = listener;
    }

    public ArrayList<CartItemDto> getItems(){
        return this.items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemCartItemBinding binding;

        public ViewHolder(ItemCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        items.get(pos).setPurchase(b);
                    }
                }
            });


            binding.buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        itemClickListener.itemDelete(pos);
                    }
                }
            });

            binding.buttonCountPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        int count = items.get(pos).getNum();

                        if(count < 99){
                            binding.textViewItemNum.setText(Integer.toString(++count));
                            items.get(pos).setNum(count);
                            binding.textViewItemPrice.setText(items.get(pos).getPrice() * count+"원");

                            //장바구니 수정 api
                            itemClickListener.itemCountChanged(pos, 1);
                        }
                    }

                }
            });

            binding.buttonCountMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        int count = items.get(pos).getNum();

                        if(count > 1){
                            binding.textViewItemNum.setText(Integer.toString(--count));
                            items.get(pos).setNum(count);
                            binding.textViewItemPrice.setText(items.get(pos).getPrice() * count+"원");

                            //장바구니 수정 api
                            itemClickListener.itemCountChanged(pos, -1);
                        }
                    }
                }
            });

        }

        public void bindItem(CartItemDto dto){

            binding.textViewGoods.setText(dto.getItemName());
            binding.textViewItemDescription.setText(dto.getDescription());

            binding.textViewItemNum.setText(Integer.toString(dto.getNum()));
            binding.textViewItemPrice.setText(dto.getPrice() * dto.getNum()+"원");


            //판매불가면 못누르게
            if(!dto.getSaleStatus()){
                binding.checkBox.setEnabled(false);
                binding.textViewUnAvailable.setVisibility(View.VISIBLE);
            }else {
                binding.checkBox.setChecked(true);
                dto.setPurchase(true);
            }

            //구매로직은 다르게
            if(adapterCase == R.id.purchaseFragment){
                binding.checkBox.setVisibility(View.GONE);
            }

            //이미지
            Glide.with(itemView)
                    .load(dto.getImgUrl())
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .into(binding.imageViewGoods);
        }
    }
}
