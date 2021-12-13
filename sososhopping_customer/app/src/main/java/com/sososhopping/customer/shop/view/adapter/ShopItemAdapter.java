package com.sososhopping.customer.shop.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ItemShopGoodsBinding;
import com.sososhopping.customer.shop.model.ShopItemModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ViewHolder>{
    ArrayList<ShopItemModel> shopItemModels = new ArrayList<>();
    OnItemClickListener itemClickListener;
    ItemShopGoodsBinding binding;

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemShopGoodsBinding.inflate(inflater, parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(shopItemModels.get(position));
    }

    @Override
    public int getItemCount() {
        return shopItemModels.size();
    }

    public void setShopItemModels(ArrayList<ShopItemModel> shopItemModels){this.shopItemModels = shopItemModels;}
    public ArrayList<ShopItemModel> getShopItemModelLists(){return shopItemModels;}

    //클릭 이벤트
    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onItemAdd(View v, int pos, int num);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }


    //ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemShopGoodsBinding binding;

        public ViewHolder(ItemShopGoodsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bindItem(ShopItemModel shopItemModel){
            binding.textViewGoods.setText(shopItemModel.getName());
            binding.textViewItemDescription.setText(shopItemModel.getDescription());
            binding.textViewItemScale.setText(shopItemModel.getPurchaseUnit());
            binding.textViewItemPrice.setText(Integer.toString(shopItemModel.getPrice()));

            //판매불가면 못누르게
            if(!shopItemModel.getSaleStatus()){
                binding.textViewUnAvailable.setVisibility(View.VISIBLE);
            }

            //이미지
            Glide.with(itemView)
                    .load(shopItemModel.getImgUrl())
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_box)
                    .error(R.drawable.icon_app_box)
                    .fallback(R.drawable.icon_app_box)
                    .into(binding.imageViewGoods);


            binding.buttonItemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(itemClickListener != null){
                            itemClickListener.onItemAdd(v,pos,Integer.parseInt(binding.textViewItemNum.getText().toString()));
                        }
                    }
                }
            });

            //-버튼
            binding.buttonCountMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(binding.textViewItemNum.getText().toString());
                    //0까지만
                    if(count > 1){
                        binding.textViewItemNum.setText(Integer.toString(--count));
                    }
                }
            });

            //+버튼
            binding.buttonCountPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(binding.textViewItemNum.getText().toString());
                    //99까지만
                    if(count < 99){
                        binding.textViewItemNum.setText(Integer.toString(++count));
                    }
                }
            });

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //세부정보 나타내는 페이지로 이동해야함
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
