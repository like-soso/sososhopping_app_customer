package com.sososhopping.customer.mysoso.service;

import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.mysoso.dto.MyCouponsDto;
import com.sososhopping.customer.mysoso.dto.MyInfoEditDto;
import com.sososhopping.customer.mysoso.dto.MyReviewsDto;
import com.sososhopping.customer.mysoso.dto.OrderCancelDto;
import com.sososhopping.customer.mysoso.dto.OrderDetailDto;
import com.sososhopping.customer.mysoso.dto.OrderListDto;
import com.sososhopping.customer.mysoso.dto.PageableOrderListDto;
import com.sososhopping.customer.mysoso.dto.PointDetailDto;
import com.sososhopping.customer.mysoso.dto.PointListDto;
import com.sososhopping.customer.mysoso.model.MyInfoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MysosoService {
    @GET("my/points")
    Call<PointListDto> requestPointList(@Header("token") String token);

    @GET("my/points/{storeId}")
    Call<PointDetailDto> requestPointDetailList(
            @Header("token") String token, @Path("storeId") int storeId, @Query("at") String date);

    @GET("my/reviews")
    Call<MyReviewsDto> requestMyReviews(@Header("token") String token);

    @DELETE("stores/{storeId}/reviews")
    Call<Void> deleteMyReview(@Header("token") String token, @Path("storeId") int storeId);

    @GET("my/coupons")
    Call<MyCouponsDto> requestMyCoupons(@Header("token") String token,
                                        @Query("storeId") Integer storeId);

    @POST("my/coupons")
    Call<Void> addCoupons(@Header("token") String token, @Body AddCouponDto addCouponDto);

    @DELETE("my/coupons/{couponId}")
    Call<Void> deleteCoupons(@Header("token") String token, @Path("couponId") long couponId);

    @GET("info")
    Call<MyInfoModel> requestMyInfo(@Header("token") String token);

    @PUT("info")
    Call<Void> requestEditMyInfo(@Header("token") String token, @Body MyInfoEditDto dto);

    @GET("my/orders")
    Call<OrderListDto> requestMyOrders(@Header("token") String token, @Query("statuses") String status);

    @GET("my/orders/page")
    Call<PageableOrderListDto> requestMyOrdersPage(@Header("token") String token, @Query("statuses") String status, @Query("offset") Integer offset);

    @GET("my/orders/{orderId}")
    Call<OrderDetailDto> requestMyOrdersDetail(@Header("token") String token, @Path("orderId") long orderId);

    @POST("orders/{orderId}")
    Call<OrderDetailDto> requestMyOrdersCancel(@Header("token") String token, @Path("orderId") long orderId, @Body OrderCancelDto action);

    //탈퇴
    @DELETE("quit")
    Call<Void> requestQuit(@Header("token") String token);
}
