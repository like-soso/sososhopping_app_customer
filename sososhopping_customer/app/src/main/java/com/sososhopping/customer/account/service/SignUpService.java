package com.sososhopping.customer.account.service;

import com.sososhopping.customer.account.dto.EmailDupCheckRequestDto;
import com.sososhopping.customer.account.dto.NicknameDupCheckRequestDto;
import com.sososhopping.customer.account.dto.PhoneDupCheckRequestDto;
import com.sososhopping.customer.account.dto.SignUpRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SignUpService {
    @POST("auth/signup")
    Call<Void> requestSignUp(@Body SignUpRequestDto dto);

    @POST("auth/signup/validation")
    Call<Void> requestEmailDuplicationCheck(@Body EmailDupCheckRequestDto dto);

    @POST("auth/signup/nickname")
    Call<Void> requestNicknameDuplicationCheck(@Body NicknameDupCheckRequestDto dto);

    @POST("auth/signup/phone")
    Call<Void> requestPhoneDuplicationCheck(@Body PhoneDupCheckRequestDto phone);
}
