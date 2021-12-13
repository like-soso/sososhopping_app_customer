package com.sososhopping.customer.account.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailDupCheckRequestDto {
    @SerializedName("email")
    String email;
}
