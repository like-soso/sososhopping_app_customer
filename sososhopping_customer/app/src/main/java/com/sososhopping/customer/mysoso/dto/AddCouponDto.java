package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCouponDto {
    /*@SerializedName("couponId")
    int couponId;*/

    @SerializedName("couponCode")
    String couponCode;
}
