package com.sososhopping.customer.error.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class ErrorMsg {
    @SerializedName("Errormessage")
    String errMsg;
}
