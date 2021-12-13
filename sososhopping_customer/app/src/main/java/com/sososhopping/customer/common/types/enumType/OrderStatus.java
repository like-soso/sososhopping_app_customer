package com.sososhopping.customer.common.types.enumType;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum OrderStatus{

    @SerializedName("PENDING")
    PENDING("승인대기", "PENDING"),

    @SerializedName("APPROVE_ALL")
    APPROVE_ALL("전체","APPROVE_ALL"),

    @SerializedName("APPROVE")
    APPROVE("준비중","APPROVE"),

    @SerializedName("READY")
    READY("준비완료","READY"),

    @SerializedName("CANCEL_ALL")
    CANCEL_ALL("전체", "CANCEL_ALL"),

    @SerializedName("CANCEL")
    CANCEL("주문취소","CANCEL"),

    @SerializedName("REJECT")
    REJECT("주문거절","REJECT"),

    @SerializedName("DONE")
    DONE("완료","DONE");

    private final String value;
    private final String stat;
    OrderStatus(String value, String stat){
        this.value = value;
        this.stat = stat;
    }
    public String getValue(){ return value; }


    @NonNull
    @Override
    public String toString() {
        return this.value;
    }

    public String getStat() {
        return this.stat;
    }
}
