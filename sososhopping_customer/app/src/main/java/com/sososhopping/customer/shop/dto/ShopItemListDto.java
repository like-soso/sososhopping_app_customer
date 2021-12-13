package com.sososhopping.customer.shop.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.shop.model.ShopItemModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemListDto {
    @SerializedName("results")
    ArrayList<ShopItemModel> shopItemModels = new ArrayList<>();
}
