package com.sososhopping.customer.common.types.enumType;

import com.google.gson.annotations.SerializedName;

public enum OrderType {

    @SerializedName("픽업")
    ONSITE("픽업"),

    @SerializedName("배송")
    DELIVERY("배송");

    private final String value;
    OrderType(String value){
        this.value = value;
    }
    public String getValue(){ return value; }
}
