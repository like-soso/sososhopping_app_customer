package com.sososhopping.customer.mysoso.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointInfoModel {

    @SerializedName("storeId")
    int storeId;

    @SerializedName("storeName")
    String storeName;

    @SerializedName("imgUrl")
    String imgUrl;

    @SerializedName("point")
    int point;

    @SerializedName("isInterestStore")
    boolean isInterestStore;
}
