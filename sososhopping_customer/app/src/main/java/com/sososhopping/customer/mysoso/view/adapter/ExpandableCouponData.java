package com.sososhopping.customer.mysoso.view.adapter;

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
public class ExpandableCouponData {
    //0 for header, 1 for child
    int dataType;
    CouponModel couponModel;
    ArrayList<ExpandableCouponData> invisibleChild;

    public ExpandableCouponData(int dataType, String storeName){
        this.dataType =dataType;
        this.couponModel = new CouponModel(storeName);
    }

    public ExpandableCouponData(int dataType, CouponModel couponModel){
        this.dataType =dataType;
        this.couponModel = couponModel;
    }
}
