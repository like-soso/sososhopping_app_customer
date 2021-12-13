package com.sososhopping.customer.account.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogInRequestDto {
    @SerializedName("email")
    String email;

    @SerializedName("password")
    String password;
}
