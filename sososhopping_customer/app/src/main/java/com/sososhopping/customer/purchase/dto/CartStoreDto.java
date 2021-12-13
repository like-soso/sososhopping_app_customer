package com.sososhopping.customer.purchase.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartStoreDto {
    int storeId;
    String storeName;
    @SerializedName("items")
    ArrayList<CartItemDto> cartItems = new ArrayList<>();
    int totalPrice;
}
