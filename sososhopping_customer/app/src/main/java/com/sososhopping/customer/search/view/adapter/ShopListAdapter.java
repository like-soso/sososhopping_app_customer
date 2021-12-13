package com.sososhopping.customer.search.view.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.StringFormatMethod;
import com.sososhopping.customer.databinding.ItemShopInfoBinding;
import com.sososhopping.customer.search.model.ShopInfoShortModel;

import java.util.ArrayList;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    ArrayList<ShopInfoShortModel> shopLists = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    ItemShopInfoBinding binding;

    @NonNull
    @Override
    public ShopListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.item_shop_info, parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListAdapter.ViewHolder holder, int position) {
        holder.bindItem(shopLists.get(position));
    }

    @Override
    public int getItemCount() {
        return shopLists.size();
    }

    public void setShopLists(ArrayList<ShopInfoShortModel> shopLists) {
        this.shopLists = shopLists;
    }
    public ArrayList<ShopInfoShortModel> getShopLists(){
        return shopLists;
    }

    //클릭 이벤트
    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onFavoriteClick(View v, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemShopInfoBinding binding;

        public ViewHolder(ItemShopInfoBinding binding){
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(v, pos);
                        }
                    }
                }
            });

            binding.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onFavoriteClick(v, pos);
                        }
                    }
                }
            });
        }

        public void bindItem(ShopInfoShortModel shopInfoShortModel){
            binding.textViewShopName.setText(shopInfoShortModel.getName());
            binding.textViewShopDescription.setText(shopInfoShortModel.getDescription());

            binding.textViewRating.setText(StringFormatMethod.getRating(shopInfoShortModel.getScore()));

            //km로 변환
            binding.textViewDistance.setText(StringFormatMethod.getDistance(shopInfoShortModel.getDistance()));


            //지역화폐, 배달여부
            if(!shopInfoShortModel.isLocalCurrencyStatus()){
                binding.layoutLocalPay.setVisibility(View.GONE);
            }else{
                binding.layoutLocalPay.setVisibility(View.VISIBLE);
            }
            if(!shopInfoShortModel.isDeliveryStatus()){
                binding.layoutDelivery.setVisibility(View.GONE);
            }else{
                binding.layoutDelivery.setVisibility(View.VISIBLE);
            }

            Glide.with(itemView)
                    .load(shopInfoShortModel.getImgUrl())
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            //사용불가능한 사진이면 ImgUrl null로 -> 추후에 에러 안뜨게
                            shopInfoShortModel.setImgUrl(null);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(binding.imageViewStore);

            bindFavorite(shopInfoShortModel.isInterestStore());
        }

        //favorite 색상 변경 함수
        public void bindFavorite(boolean isFavorite){
            int drawableImage = 0;
            if(isFavorite){
                drawableImage = R.drawable.ic_baseline_favorite_24;
            }
            else{
                drawableImage = R.drawable.ic_baseline_favorite_border_24;
            }

            //Draw New Heart
            Drawable wrapped = DrawableCompat.wrap(AppCompatResources.getDrawable(binding.getRoot().getContext(), drawableImage));
            DrawableCompat.setTint(wrapped, binding.getRoot().getResources().getColor(R.color.main_0));
            binding.imageViewFavorite.setImageDrawable(wrapped);
        }
    }
}
