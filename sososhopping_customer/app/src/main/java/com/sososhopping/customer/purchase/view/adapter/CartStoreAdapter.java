package com.sososhopping.customer.purchase.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.R;
import com.sososhopping.customer.purchase.dto.CartItemDto;
import com.sososhopping.customer.purchase.dto.CartStoreDto;
import com.sososhopping.customer.purchase.dto.CartUpdateDto;
import com.sososhopping.customer.databinding.ItemCartStoreBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

@Getter
public class CartStoreAdapter extends  RecyclerView.Adapter<CartStoreAdapter.ViewHolder>{

    ArrayList<CartStoreDto> cartstores = new ArrayList<>();
    ItemCartStoreBinding binding;
    OnItemClickListenerCartStore itemClickListener;

    //내부 아이템 어댑터
    CartItemAdapter itemAdapter;

    //장바구니 개수 수정사항
    HashMap<Integer, Integer> updateSet = new HashMap<>();

    //Context
    Context context;

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ItemCartStoreBinding.inflate(inflater, parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bindItem(cartstores.get(position));
    }

    @Override
    public int getItemCount() {
        return cartstores.size();
    }

    public void setOnItemClickListener(OnItemClickListenerCartStore listener){
        this.itemClickListener = listener;
    }

    public void setCartstores(ArrayList<CartStoreDto> c){
        this.cartstores = c;
    }
    public ArrayList<CartStoreDto> getCartstores(){return this.cartstores;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemCartStoreBinding binding;

        public ViewHolder(ItemCartStoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.recyclerviewItem.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL, false));
            binding.buttonMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        //리스너 호출
                        if(itemClickListener != null){
                            itemClickListener.onButtonStoreClick(cartstores.get(pos).getStoreId());
                        }
                    }
                }
            });

            binding.buttonPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        ArrayList<CartUpdateDto> purchaseList = new ArrayList<>();

                        for(CartItemDto c : cartstores.get(pos).getCartItems()){
                            if(c.isPurchase()){
                                purchaseList.add(new CartUpdateDto(c.getItemId(), c.getNum()));
                            }
                        }

                        //이걸 보내기
                        if(itemClickListener != null){
                            itemClickListener.onButtonPurchaseClick(cartstores.get(pos), purchaseList);
                        }

                    }
                }
            });
        }

        public void bindItem(CartStoreDto cartStoreDto){
            binding.textViewStoreName.setText(cartStoreDto.getStoreName());
            binding.textViewTotalStorePrice.setText(cartStoreDto.getTotalPrice()+"원");

            //아이템 추가
            itemAdapter = new CartItemAdapter(cartStoreDto.getCartItems(), R.id.cartMainFragment);
            binding.recyclerviewItem.setAdapter(itemAdapter);

            itemAdapter.setOnItemClickListener(new OnItemClickListenerCartItem() {

                @Override
                public void itemDelete(int itempos) {
                    int pos = getAdapterPosition();
                    //장바구니 삭제
                    if(itemClickListener != null){
                        itemClickListener.itemDelete(pos, itempos, cartStoreDto.getCartItems().get(itempos));
                    }

                }

                @Override
                public void itemCountChanged(int itemId, int val) {
                    int changedVal =  cartStoreDto.getCartItems().get(itemId).getPrice() * val;
                    cartStoreDto.setTotalPrice( cartStoreDto.getTotalPrice() + changedVal);
                    binding.textViewTotalStorePrice.setText(cartStoreDto.getTotalPrice()+"원");

                    itemClickListener.itemCountChanged(changedVal);
                }
            });
        }
    }
}
