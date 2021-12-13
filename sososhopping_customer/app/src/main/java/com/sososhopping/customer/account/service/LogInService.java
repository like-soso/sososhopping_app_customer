package com.sososhopping.customer.account.service;

import com.sososhopping.customer.account.dto.ChangePasswordDto;
import com.sososhopping.customer.account.dto.FindEmailDto;
import com.sososhopping.customer.account.dto.LogInRequestDto;
import com.sososhopping.customer.account.dto.LogInResponseDto;
import com.sososhopping.customer.account.dto.FindInfoDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LogInService {

    @POST("auth/login")
    Call<LogInResponseDto> requestLogin(@Body LogInRequestDto dto);

    @POST("auth/findEmail")
    Call<ResponseBody> requestEmail(@Body FindEmailDto dto);

    @POST("auth/findPassword")
    Call<Void> requestPassword(@Body FindInfoDto dto);

    @POST("auth/changePassword")
    Call<Void> changePassword(@Body ChangePasswordDto dto);
}
