package com.sososhopping.customer.purchase.view.adapter;

import com.sososhopping.customer.purchase.dto.CartItemDto;
import com.sososhopping.customer.purchase.dto.CartStoreDto;
import com.sososhopping.customer.purchase.dto.CartUpdateDto;

import java.util.ArrayList;

//클릭 이벤트
public interface OnItemClickListenerCartStore{
    void onButtonPurchaseClick(CartStoreDto cartStoreDto, ArrayList<CartUpdateDto> purchaseList);
    void onButtonStoreClick(int storeId);
    void itemDelete(int storePos, int itemPos, CartItemDto cartItemDto);
    void itemCountChanged(int val);

}