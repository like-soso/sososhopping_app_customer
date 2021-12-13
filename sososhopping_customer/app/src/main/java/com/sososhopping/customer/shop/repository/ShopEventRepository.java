package com.sososhopping.customer.shop.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.shop.dto.CouponListDto;
import com.sososhopping.customer.shop.dto.EventItemListDto;
import com.sososhopping.customer.shop.dto.PageableWritingListDto;
import com.sososhopping.customer.shop.model.EventDetailModel;
import com.sososhopping.customer.shop.service.ShopService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopEventRepository {
    private static ShopEventRepository instance;
    private final ShopService shopService;

    private ShopEventRepository(){
        this.shopService = ApiServiceFactory.createService(ShopService.class);
    }

    public static synchronized ShopEventRepository getInstance() {
        if(instance == null){
            instance = new ShopEventRepository();
        }
        return instance;
    }

    public void requestShopCoupon(int storeId, Consumer<CouponListDto> couponModel,
                                  Runnable onFailed,
                                  Runnable onError) {

        shopService.requestCoupons(storeId).enqueue(new Callback<CouponListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CouponListDto> call, Response<CouponListDto> response) {
                switch (response.code()){
                    case 200:{
                        couponModel.accept(response.body());
                        break;
                    }
                    //검색 없음
                    case 404:{
                        onFailed.run();
                        break;
                    }
                    default:{
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<CouponListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void addShopCoupon(String token, AddCouponDto addCouponDto, int[] msgCodes,
                              Consumer<Integer> onResult,
                              Runnable onFailedLogIn,
                              Runnable onError){
        shopService.addCoupons(token, addCouponDto).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("쿠폰", response.raw().toString());
                switch (response.code()){
                    case 201:{
                        onResult.accept(msgCodes[0]);
                        break;
                    }
                    //토큰 실패
                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }
                    //중복저장
                    case 409:{
                        onResult.accept(msgCodes[2]);
                        break;
                    }
                    //쿠폰 기간 x , 수량 소진
                    case 400:
                    //쿠폰없음
                    case 404:
                    default:
                        onResult.accept(msgCodes[1]);
                        break;

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });

    }

    public void requestShopWriting(int storeId, Consumer<EventItemListDto> eventModel,
                                   Runnable onFailed,
                                   Runnable onError) {

        shopService.requestWritings(storeId).enqueue(new Callback<EventItemListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<EventItemListDto> call, Response<EventItemListDto> response) {
                switch (response.code()) {
                    case 200: {
                        eventModel.accept(response.body());
                        break;
                    }
                    //검색 없음
                    case 404: {
                        onFailed.run();
                        break;
                    }
                    default: {
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventItemListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestShopWritingPage(int storeId,
                                       Integer offset,
                                       Consumer<PageableWritingListDto> eventModel,
                                   Runnable onFailed,
                                   Runnable onError) {

        shopService.requestWritingsPage(storeId, offset).enqueue(new Callback<PageableWritingListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PageableWritingListDto> call, Response<PageableWritingListDto> response) {
                switch (response.code()) {
                    case 200: {
                        eventModel.accept(response.body());
                        break;
                    }
                    //검색 없음
                    case 404: {
                        onFailed.run();
                        break;
                    }
                    default: {
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<PageableWritingListDto> call, Throwable t) {
                onError.run();
            }
        });
    }




    public void requestShopEventDetail(int storeId, int writingId,
                                       Consumer<EventDetailModel> eventDetailModel,
                                       Runnable onFailed,
                                       Runnable onError) {
        shopService.requestWritingsDetail(storeId, writingId).enqueue(new Callback<EventDetailModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<EventDetailModel> call, Response<EventDetailModel> response) {
                switch (response.code()) {
                    case 200: {
                        eventDetailModel.accept(response.body());
                        break;
                    }
                    //검색 없음
                    case 400:
                    case 404: {
                        onFailed.run();
                        break;
                    }

                    default: {
                        onError.run();
                    }
                }
            }
            @Override
            public void onFailure(Call<EventDetailModel> call, Throwable t) {
                onError.run();
            }
        });
    }


}
