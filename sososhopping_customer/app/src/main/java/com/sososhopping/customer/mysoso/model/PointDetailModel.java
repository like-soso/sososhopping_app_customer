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
public class PointDetailModel {

    @SerializedName("pointAmount")
    int pointAmount;

    @SerializedName("resultAmount")
    int resultAmount;

    @SerializedName("createdAt")
    String createdAt;
}
