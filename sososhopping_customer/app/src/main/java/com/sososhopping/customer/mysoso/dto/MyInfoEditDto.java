package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MyInfoEditDto {

    @SerializedName("password")
    String password;

    @SerializedName("name")
    String name;

    @SerializedName("phone")
    String phone;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("streetAddress")
    String street;

    @SerializedName("detailedAddress")
    String detail;

}
