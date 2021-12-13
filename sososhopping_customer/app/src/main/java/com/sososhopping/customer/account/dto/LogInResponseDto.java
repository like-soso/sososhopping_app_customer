package com.sososhopping.customer.account.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogInResponseDto {

    @SerializedName("token")
    String token;

    @SerializedName("firebaseToken")
    String firebaseToken;

    public String getToken() {
        return token;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }
}
