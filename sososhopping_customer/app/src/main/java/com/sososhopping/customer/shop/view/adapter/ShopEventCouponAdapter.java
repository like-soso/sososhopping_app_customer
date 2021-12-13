package com.sososhopping.customer.shop.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ItemShopCouponBinding;
import com.sososhopping.customer.shop.model.CouponModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShopEventCouponAdapter extends RecyclerView.Adapter<ShopEventCouponAdapter.ViewHolder>{
    ArrayList<CouponModel> couponModels = new ArrayList<>();
    public String storeName;
    ItemShopCouponBinding binding;
    OnItemClickListenerCoupon itemClickListener;


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemShopCouponBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(couponModels.get(position));
    }

    @Override
    public int getItemCount() {
        return couponModels.size();
    }
    public void setCouponModels(ArrayList<CouponModel> couponModels){this.couponModels = couponModels;}
    public ArrayList<CouponModel> getCouponModels(){
        return this.couponModels;
    }

    //클릭 이벤트
    public interface OnItemClickListenerCoupon{
        void onItemClick(CouponModel couponModel);
    }

    public void setOnItemClickListener(OnItemClickListenerCoupon listener){
        this.itemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemShopCouponBinding binding;

        public ViewHolder(ItemShopCouponBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindItem(CouponModel couponModel){
            binding.textViewCouponCode.setText(couponModel.getCouponCode());
            binding.textViewCouponAmount.setText(couponModel.amount());

            //코드, 이름
            binding.textViewCouponName.setText(couponModel.getCouponName());
            binding.textViewStoreName.setText(storeName);

            //제한조건
            if(couponModel.getMinimumOrderPrice() != 0){
                binding.textViewMinimum.setText(Integer.toString(couponModel.getMinimumOrderPrice()));
            }
            else{
                binding.linerlayoutMinimum.setVisibility(View.GONE);
            }

            if(couponModel.getStartDate() == null && couponModel.getEndDate() == null){
                binding.linearLayoutDate.setVisibility(View.GONE);
            }
            else{
                if(couponModel.getStartDate() != null){
                    binding.textViewStartDate.setText(DateFormatMethod.dateFormatDay(couponModel.getStartDate()));
                }
                if(couponModel.getEndDate() != null){
                    binding.textViewEndDate.setText(DateFormatMethod.dateFormatDay(couponModel.getEndDate())+"까지 발급");
                }
            }

            if(couponModel.getExpiryDate() != null){
                binding.textViewCouponExpire.setText("저장 후 "+DateFormatMethod.dateFormatDay(couponModel.getExpiryDate())+"까지 사용가능");
            }

            binding.buttonAddCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //쿠폰 저장 API
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(couponModels.get(pos));
                        }
                    }
                }
            });

            //화면 바뀌게
            binding.linearLayoutCouponInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.linearLayoutCouponCode.setVisibility(View.VISIBLE);
                    binding.linearLayoutCouponInfo.setVisibility(View.INVISIBLE);
                }
            });
            binding.linearLayoutCouponCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.linearLayoutCouponCode.setVisibility(View.INVISIBLE);
                    binding.linearLayoutCouponInfo.setVisibility(View.VISIBLE);
                }
            });

        }
    }
}
