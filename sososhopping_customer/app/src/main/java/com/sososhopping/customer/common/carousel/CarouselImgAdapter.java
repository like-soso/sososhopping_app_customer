package com.sososhopping.customer.common.carousel;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ItemShopCarouselImgBinding;

import java.util.ArrayList;

public class CarouselImgAdapter extends RecyclerView.Adapter<CarouselImgAdapter.ViewHolder> {
    private ItemShopCarouselImgBinding binding;
    private ArrayList<String> sliderImage;

    public CarouselImgAdapter(ArrayList<String> sliderImage){
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemShopCarouselImgBinding.inflate(inflater, parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(sliderImage.get(position),binding.imageSlider);
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public void setSliderImage(ArrayList<String> sliderImage){
        this.sliderImage = sliderImage;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(ItemShopCarouselImgBinding binding) {
            super(binding.getRoot());
        }

        public void bindItem(String imageURL, ImageView imageView) {
            //이미지
            Glide.with(itemView)
                    .load(imageURL)
                    .transform(new CenterCrop())
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .into(imageView);
        }
    }
}