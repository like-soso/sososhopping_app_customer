package com.sososhopping.customer.mysoso.model;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.common.types.enumType.OrderType;

import lombok.Getter;

@Getter
public class OrderRecordShortModel {

    @SerializedName("orderId")
    long orderId;

    @SerializedName("storeName")
    String storeName;

    @SerializedName("description")
    String orderDescription;

    @SerializedName("imgUrl")
    String imgUrl;

    @SerializedName("finalPrice")
    int finalPrice;

    @SerializedName("orderType")
    OrderType orderType;

    @SerializedName("orderStatus")
    OrderStatus orderStatus;

    @SerializedName("createdAt")
    String createdAt;

}
