package com.sososhopping.customer.purchase.service;

import com.sososhopping.customer.purchase.dto.CartDto;
import com.sososhopping.customer.purchase.dto.CartUpdateListDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Query;

public interface CartService {

    @GET("my/cart")
    Call<CartDto> requestMyCart(@Header("token") String token);

    @DELETE("my/cart")
    Call<Void> requestDeleteItem(@Header("token") String token, @Query(value = "itemId") int itemId);

    @PATCH("my/cart")
    Call<Void> requestUpdateItem(@Header("token") String token, @Body CartUpdateListDto dto);
}
