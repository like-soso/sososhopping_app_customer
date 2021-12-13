package com.sososhopping.customer.shop.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ItemShopReviewBinding;
import com.sososhopping.customer.shop.model.ReviewModel;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;

public class ShopReviewAdapter extends RecyclerView.Adapter<ShopReviewAdapter.ViewHolder>{
    ArrayList<ReviewModel> reviewModels = new ArrayList<>();
    ItemShopReviewBinding binding;

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemShopReviewBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(reviewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public void setReviewModels(ArrayList<ReviewModel> reviewModels){
        this.reviewModels =reviewModels;
    }

    public ArrayList<ReviewModel> getReviewModels(){
        return reviewModels;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ItemShopReviewBinding binding;

        public ViewHolder(ItemShopReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(ReviewModel reviewModel){
            binding.textViewNickname.setText(reviewModel.getNickname());
            binding.textViewReviewContents.setText(reviewModel.getContent());
            binding.textViewRating.setText(Float.toString(reviewModel.getScore()));
            binding.textViewReviewDate.setText(DateFormatMethod.dateFormatMin(reviewModel.getCreatedAt()));
        }
    }
}
