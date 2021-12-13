package com.sososhopping.customer.mysoso.view.adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.databinding.ItemMysosoPointBinding;
import com.sososhopping.customer.databinding.ItemOrderRecordBinding;
import com.sososhopping.customer.mysoso.model.OrderRecordShortModel;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class MysosoOrderListAdapter extends RecyclerView.Adapter<MysosoOrderListAdapter.ViewHolder>{

    ItemOrderRecordBinding binding;
    ArrayList<OrderRecordShortModel> items;
    OnItemClickListener itemClickListener;

    public MysosoOrderListAdapter(ArrayList<OrderRecordShortModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemOrderRecordBinding.inflate(inflater, parent, false);
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

    public void setItems(ArrayList<OrderRecordShortModel> i){
        this.items = i;
    }

    //클릭 이벤트
    public interface OnItemClickListener{
        void onItemClick(long orderId);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemOrderRecordBinding binding;

        public ViewHolder(ItemOrderRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(items.get(pos).getOrderId());
                        }
                    }
                }
            });
        }

        public void bindItem(OrderRecordShortModel orderRecordShortModel){
            binding.textViewShopName.setText(orderRecordShortModel.getStoreName());
            binding.textViewOrderDescription.setText(orderRecordShortModel.getOrderDescription());
            binding.textViewOrderPrice.setText(orderRecordShortModel.getFinalPrice()+"원");
            binding.textViewOrderDate.setText(DateFormatMethod.dateFormatToKorean(orderRecordShortModel.getCreatedAt()));


            //이미지
            Glide.with(itemView)
                    .load(orderRecordShortModel.getImgUrl())
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .thumbnail(0.2f)
                    .placeholder(R.drawable.icon_app_groceries)
                    .error(R.drawable.icon_app_groceries)
                    .fallback(R.drawable.icon_app_groceries)
                    .into(binding.imageViewStore);

            setStatus(orderRecordShortModel);
        }

        public void setStatus(OrderRecordShortModel orderRecordShortModel){
            OrderStatus os = orderRecordShortModel.getOrderStatus();
            int drawableImage = 0;
            int drawableColor = 0;

            switch (os){
                case PENDING:
                    drawableImage = R.drawable.ic_baseline_pending_24;
                    drawableColor = R.color.main_200;
                    break;

                    //준비중
                case APPROVE:
                    drawableImage = R.drawable.ic_baseline_fast_forward_24;
                    drawableColor = R.color.yellow_approve;
                    break;

                case READY:
                    //배송
                    if(orderRecordShortModel.getOrderType().equals(OrderType.DELIVERY)){
                        binding.textViewStatus.setText("배송중");
                        drawableImage = R.drawable.ic_baseline_pedal_bike_24;
                        drawableColor = R.color.teal_delivery;
                    }
                    //픽업
                    else{
                        drawableImage = R.drawable.ic_baseline_shopping_cart_24;
                        drawableColor = R.color.red_cart;
                    }
                    break;
                case CANCEL:
                    drawableImage = R.drawable.ic_baseline_replay_24;
                    drawableColor = R.color.red_cancel;
                    break;
                case REJECT:
                    drawableImage = R.drawable.ic_baseline_remove_circle_outline_24;
                    drawableColor = R.color.red_cancel;
                    break;
                case DONE:
                    drawableImage = R.drawable.ic_baseline_check_circle_outline_24;
                    drawableColor = R.color.green_done;
                    break;
            }

            Drawable wrapped = DrawableCompat.wrap(AppCompatResources.getDrawable(binding.getRoot().getContext(), drawableImage));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(wrapped.mutate(), binding.getRoot().getResources().getColor(drawableColor));
            } else {
                wrapped.mutate().setColorFilter(binding.getRoot().getResources().getColor(drawableColor), PorterDuff.Mode.SRC_IN);
            }
            binding.imageViewStatus.setImageDrawable(wrapped);
            binding.textViewStatus.setText(os.getValue());

        }

    }
}
