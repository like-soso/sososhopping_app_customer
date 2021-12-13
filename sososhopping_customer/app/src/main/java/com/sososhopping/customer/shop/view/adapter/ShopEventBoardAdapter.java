package com.sososhopping.customer.shop.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ItemShopBoardBinding;
import com.sososhopping.customer.shop.model.EventItemModel;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;

import lombok.SneakyThrows;

public class ShopEventBoardAdapter extends RecyclerView.Adapter<ShopEventBoardAdapter.ViewHolder>{
    ArrayList<EventItemModel> eventItemModels = new ArrayList<>();
    OnItemClickListenerBoard itemClickListener;
    ItemShopBoardBinding binding;

    @NonNull
    @NotNull
    @Override
    public ShopEventBoardAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemShopBoardBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @SneakyThrows
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(eventItemModels.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return eventItemModels.size();
    }

    public void setShopBoardItemModels(ArrayList<EventItemModel> eventItemModels){this.eventItemModels = eventItemModels;}
    public ArrayList<EventItemModel> getShopBoardItemModels(){
        return this.eventItemModels;
    }

    //클릭 이벤트
    public interface OnItemClickListenerBoard{
        void onItemClick(int writingId);
    }

    public void setOnItemClickListener(OnItemClickListenerBoard listener){
        this.itemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemShopBoardBinding binding;

        public ViewHolder(ItemShopBoardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(EventItemModel eventItemModel) throws ParseException {
            binding.textViewEventTitle.setText(eventItemModel.getTitle());
            binding.textViewEventOrNews.setText(eventItemModel.getWritingType().getValue());
            binding.textViewEventWriteDate.setText(DateFormatMethod.dateFormatDay(eventItemModel.getCreatedAt()));
            if(eventItemModel.getDescription().length() >= 50){
                binding.textViewEventDescription.setText(eventItemModel.getDescription().substring(0,49) + "...");
            }
            else{
                binding.textViewEventDescription.setText(eventItemModel.getDescription());
            }

            //이미지
            Glide.with(itemView)
                    .load(eventItemModel.getImgUrl())
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .into(binding.imageViewBoard);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(eventItemModels.get(pos).getWritingId());
                        }
                    }
                }
            });
        }
    }
}
