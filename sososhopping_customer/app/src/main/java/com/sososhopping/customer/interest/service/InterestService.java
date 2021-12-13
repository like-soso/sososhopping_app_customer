package com.sososhopping.customer.interest.service;

import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.shop.dto.StoreIdDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface InterestService {
    @GET("my/interest_store")
    Call<ShopListDto> requestInterestList(@Header("token") String token);

    @POST("my/interest_store")
    Call<Void> changeInterest(@Header("token") String token, @Body StoreIdDto storeIdDto);
}
