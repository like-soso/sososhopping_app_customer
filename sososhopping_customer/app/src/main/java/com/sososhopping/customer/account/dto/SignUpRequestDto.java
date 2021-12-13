package com.sososhopping.customer.account.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class SignUpRequestDto {

    @SerializedName("email")
    String email;

    @SerializedName("password")
    String password;

    @SerializedName("name")
    String name;

    @SerializedName("phone")
    String phone;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("street")
    String street;

    @SerializedName("detail")
    String detail;
}
