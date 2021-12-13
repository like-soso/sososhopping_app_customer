package com.sososhopping.customer.purchase.view;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.R;
import com.sososhopping.customer.purchase.view.adapter.CartItemAdapter;
import com.sososhopping.customer.purchase.view.adapter.OnItemClickListenerCartItem;
import com.sososhopping.customer.purchase.viewmodel.PurchaseViewModel;
import com.sososhopping.customer.databinding.PurchaseMainBinding;

//파트로 나눔
public class PurchaseFragment_Item {

    PurchaseMainBinding binding;
    PurchaseViewModel purchaseViewModel;
    Context context;
    CartItemAdapter cartItemAdapter;

    public PurchaseFragment_Item(PurchaseMainBinding binding, PurchaseViewModel purchaseViewModel, Context context) {
        this.binding = binding;
        this.purchaseViewModel = purchaseViewModel;
        this.context = context;
    }

    protected void setItemLayout(){

        //매장이름설정
        binding.includeLayoutItem.textViewTotalStorePrice.setText(purchaseViewModel.getPurchaseList().getValue().getTotalPrice()+"원");

        //상품들
        binding.includeLayoutItem.recyclerviewItem.setLayoutManager(
                new LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        );
        cartItemAdapter = new CartItemAdapter(
                purchaseViewModel.getPurchaseList().getValue().getCartItems(),
                R.id.purchaseFragment
        );
        binding.includeLayoutItem.recyclerviewItem.setAdapter(cartItemAdapter);


        cartItemAdapter.setOnItemClickListener(new OnItemClickListenerCartItem() {
            @Override
            public void itemDelete(int pos) {

                //0개 구매는 안됨
                if(purchaseViewModel.getPurchaseList().getValue().getCartItems().size() <= 1){
                    Toast.makeText(context, "아무것도 구매하지 않으실 수는 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                //업데이트 알리기
                purchaseViewModel.calTotalItemPriceDelete(pos);
                cartItemAdapter.getItems().remove(pos);
                cartItemAdapter.notifyItemRemoved(pos);

                //갱신
                purchaseViewModel.getPurchaseList().getValue().setCartItems(
                        cartItemAdapter.getItems()
                );

                //쿠폰, 포인트 갱신
                purchaseViewModel.getUseCoupon().setValue(null);
            }

            @Override
            public void itemCountChanged(int pos, int val) {
                int changedVal = cartItemAdapter.getItems().get(pos).getPrice() * val;
                purchaseViewModel.getTotalPrice().setValue(
                        purchaseViewModel.getTotalPrice().getValue() + changedVal
                );

                //쿠폰, 포인트 갱신
                purchaseViewModel.getUseCoupon().setValue(null);
            }
        });
    }
}
