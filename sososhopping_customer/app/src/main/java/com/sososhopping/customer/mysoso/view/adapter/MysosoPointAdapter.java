package com.sososhopping.customer.mysoso.view.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ItemMysosoPointBinding;
import com.sososhopping.customer.mysoso.model.PointInfoModel;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MysosoPointAdapter extends RecyclerView.Adapter<MysosoPointAdapter.ViewHolder>{
    ArrayList<PointInfoModel> items = new ArrayList<>();
    ItemMysosoPointBinding binding;
    OnItemClickListenerBoard itemClickListener;

    @NonNull
    @Override
    public MysosoPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemMysosoPointBinding.inflate(inflater, parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MysosoPointAdapter.ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //클릭 이벤트
    public interface OnItemClickListenerBoard{
        void onItemClick(int storeId);
    }

    public void setOnItemClickListener(OnItemClickListenerBoard listener){
        this.itemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemMysosoPointBinding binding;

        public ViewHolder(ItemMysosoPointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(items.get(pos).getStoreId());
                        }
                    }
                }
            });
        }

        public void bindItem(PointInfoModel pointInfoModel){
            binding.textViewShopTitle.setText(pointInfoModel.getStoreName());
            binding.editTextShopPoint.setText(Integer.toString(pointInfoModel.getPoint()));


            //이미지
            Glide.with(itemView)
                    .load(pointInfoModel.getImgUrl())
                    .override(100,100)
                    .transform(new CenterCrop(),new CircleCrop())
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .into(binding.imageViewShop);


        }

    }
}
