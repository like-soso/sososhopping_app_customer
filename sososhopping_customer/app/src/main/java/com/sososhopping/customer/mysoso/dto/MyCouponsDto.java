package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.shop.model.CouponModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyCouponsDto {
    @SerializedName("results")
    ArrayList<CouponModel> coupons = new ArrayList<>();
}
