package com.sososhopping.customer.shop.service;

import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.shop.dto.AddCartDto;
import com.sososhopping.customer.shop.dto.CouponListDto;
import com.sososhopping.customer.shop.dto.EventItemListDto;
import com.sososhopping.customer.shop.dto.ItemListDto;
import com.sososhopping.customer.shop.dto.PageableReviewListDto;
import com.sososhopping.customer.shop.dto.PageableWritingListDto;
import com.sososhopping.customer.shop.dto.ReportInputDto;
import com.sososhopping.customer.shop.dto.ReviewInputDto;
import com.sososhopping.customer.shop.dto.ReviewListDto;
import com.sososhopping.customer.shop.dto.StoreIdDto;
import com.sososhopping.customer.shop.model.EventDetailModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShopService {

    @GET("stores/{storeId}")
    Call<ShopIntroduceModel> requestShopIntroduce(@Header("token") String token, @Path("storeId") int storeId);

    @GET("stores/{storeId}/items")
    Call<ItemListDto> requestShopItem(@Path("storeId") int storeId);

    @GET("stores/{storeId}/coupons")
    Call<CouponListDto> requestCoupons(@Path("storeId") int storeId);

    @GET("stores/{storeId}/writings")
    Call<EventItemListDto> requestWritings(@Path("storeId") int storeId);

    @GET("stores/{storeId}/writings/page")
    Call<PageableWritingListDto> requestWritingsPage(@Path("storeId") int storeId, @Query("offset") Integer offset);

    @GET("stores/{storeId}/writings/{writingId}")
    Call<EventDetailModel> requestWritingsDetail(@Path("storeId") int storeId, @Path("writingId") int writingId);

    @GET("stores/{storeId}/reviews/page")
    Call<PageableReviewListDto> requestReviewsPage(@Path("storeId") int storeId,
                                                   @Query(value = "offset") Integer offset);

    @GET("stores/{storeId}/reviews")
    Call<ReviewListDto> requestReviews(@Path("storeId") int storeId);

    @POST("stores/{storeId}/reviews")
    Call<Void> inputReviews(@Header("token") String token, @Path("storeId") int storeId, @Body ReviewInputDto reviewInputDto);

    @DELETE("stores/{storeId}/reviews")
    Call<Void> deleteMyReview(@Header("token") String token, @Path("storeId") int storeId);

    @GET("stores/{storeId}/reviews/check")
    Call<Void> checkReviews(@Header("token") String token, @Path("storeId") int storeId);

    @POST("my/interest_store")
    Call<Void> changeInterest(@Header("token") String token, @Body StoreIdDto storeIdDto);

    @POST("stores/{storeId}/reports")
    Call<Void> inputReports(@Header("token") String token, @Path("storeId") int storeId, @Body ReportInputDto reportInputDto);

    @POST("my/coupons")
    Call<Void> addCoupons(@Header("token") String token, @Body AddCouponDto addCouponDto);

    @POST("my/cart")
    Call<Void> addCart(@Header("token")String token, @Body AddCartDto dto);
}
