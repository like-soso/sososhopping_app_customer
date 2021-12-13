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
public class CartDto {
    @SerializedName("results")
    ArrayList<CartStoreDto> cartList = new ArrayList<>();
}
