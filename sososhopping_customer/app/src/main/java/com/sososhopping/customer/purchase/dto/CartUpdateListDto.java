package com.sososhopping.customer.purchase.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartUpdateListDto {
    @SerializedName("requests")
    ArrayList<CartUpdateDto> updateLists;
}
