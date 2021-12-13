package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderCancelDto {
    @SerializedName("action")
    String action;
}
