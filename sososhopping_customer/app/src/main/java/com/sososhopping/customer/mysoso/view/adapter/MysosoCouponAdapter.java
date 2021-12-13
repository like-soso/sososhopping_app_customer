package com.sososhopping.customer.mysoso.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.R;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ItemMysosoCouponBinding;
import com.sososhopping.customer.databinding.ItemMysosoCouponHeaderBinding;
import com.sososhopping.customer.shop.model.CouponModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MysosoCouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    ArrayList<ExpandableCouponData> items;

    ItemMysosoCouponHeaderBinding headerBinding;
    ItemMysosoCouponBinding childBinding;
    OnItemClickListenerChild itemClickListenerChild;

    public MysosoCouponAdapter(ArrayList<ExpandableCouponData> items) {
        this.items = items;
    }

    public void setItems(ArrayList<ExpandableCouponData> items){
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case HEADER:{
                headerBinding = ItemMysosoCouponHeaderBinding.inflate(inflater, parent, false);
                return new HeaderViewHolder(headerBinding);
            }
            case CHILD:{
                childBinding = ItemMysosoCouponBinding.inflate(inflater, parent, false);
                return new ChildViewHolder(childBinding);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final ExpandableCouponData item = items.get(position);
        switch (item.getDataType()){
            case HEADER:{
                 ((HeaderViewHolder) holder).bindItem(items.get(position), position);
                 break;
            }
            case CHILD:{
                ((ChildViewHolder) holder).bindItem(items.get(position));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getDataType();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        ItemMysosoCouponHeaderBinding binding;

        public HeaderViewHolder(ItemMysosoCouponHeaderBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
        }

        public void bindItem(ExpandableCouponData item, int pos){
            binding.headerTitle.setText(item.getCouponModel().getStoreName());

            //버튼 설정
            if(item.getInvisibleChild() == null){
                binding.btnExpandToggle.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            }else{
                binding.btnExpandToggle.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
            }

            binding.btnExpandToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //숨은자식 x에서 눌림 ->
                    if (item.getInvisibleChild() == null) {
                        item.setInvisibleChild(new ArrayList<ExpandableCouponData>());
                        int count = 0;
                        while (items.size() > pos + 1 && items.get(pos + 1).dataType == CHILD) {
                            item.getInvisibleChild().add(items.remove(pos + 1));
                            count++;
                        }
                        notifyItemRangeRemoved(pos + 1, count);
                        binding.btnExpandToggle.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                    } else {
                        int index = pos + 1;
                        for (ExpandableCouponData i : item.getInvisibleChild()) {
                            items.add(index, i);
                            index++;
                        }
                        notifyItemRangeInserted(pos + 1, index - pos - 1);
                        binding.btnExpandToggle.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        item.setInvisibleChild(null);
                    }
                }
            });
        }
    }

    //클릭 이벤트
    public interface OnItemClickListenerChild{
        void onItemClick(CouponModel couponModel);
        void onItemLongClick(CouponModel couponModel);
    }

    public void setOnItemClickListener(OnItemClickListenerChild listener){
        this.itemClickListenerChild = listener;
    }


    //자식 ViewHolder
    public class ChildViewHolder extends RecyclerView.ViewHolder{
        ItemMysosoCouponBinding binding;
        boolean state = true;

        public ChildViewHolder(ItemMysosoCouponBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.buttonWatchCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(state){
                        binding.linearLayoutCouponCode.setVisibility(View.VISIBLE);
                        binding.linearLayoutCouponInfo.setVisibility(View.INVISIBLE);
                    }
                    else{
                        binding.linearLayoutCouponCode.setVisibility(View.INVISIBLE);
                        binding.linearLayoutCouponInfo.setVisibility(View.VISIBLE);
                    }
                    state = !state;
                }
            });

            //삭제 / 이동 등의 다이얼로그 고려
            binding.linearLayoutCouponInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListenerChild != null){
                            itemClickListenerChild.onItemClick(items.get(pos).getCouponModel());
                        }
                    }
                }
            });

            binding.linearLayoutCouponInfo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListenerChild != null){
                            itemClickListenerChild.onItemLongClick(items.get(pos).getCouponModel());
                        }
                    }
                    return false;
                }
            });

        }

        public void bindItem(ExpandableCouponData item){
            CouponModel couponModel = item.getCouponModel();

            binding.textViewCouponCode.setText(couponModel.getCouponCode());
            binding.textViewCouponAmount.setText(couponModel.amount());

            //코드, 이름
            binding.textViewCouponName.setText(couponModel.getCouponName());

            //제한조건
            binding.textViewMinimum.setText(Integer.toString(couponModel.getMinimumOrderPrice()));

            if(couponModel.getExpiryDate() != null){
                binding.textViewCouponExpire.setText("저장 후 "+DateFormatMethod.dateFormatDay(couponModel.getExpiryDate())+"까지 사용가능");
            }

        }
    }
}
