package com.sososhopping.customer.mysoso.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.mysoso.dto.MyCouponsDto;
import com.sososhopping.customer.mysoso.service.MysosoService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MysosoCouponRepository {
    private static MysosoCouponRepository instance;
    private final MysosoService mysosoService;

    private MysosoCouponRepository(){
        this.mysosoService = ApiServiceFactory.createService(MysosoService.class);
    }

    public static synchronized MysosoCouponRepository getInstance(){
        if(instance == null){
            instance = new MysosoCouponRepository();
        }
        return instance;
    }

    public void requestCoupon(String token,
                              Integer storeId,
                              Consumer<MyCouponsDto> onSuccess,
                              Runnable onFailedLogIn,
                              Runnable onFailed,
                              Runnable onError){
        mysosoService.requestMyCoupons(token,storeId).enqueue(new Callback<MyCouponsDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<MyCouponsDto> call, Response<MyCouponsDto> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.accept(response.body());
                        break;
                    }
                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }
                    //검색 없음
                    case 404:
                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<MyCouponsDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void addShopCoupon(String token, AddCouponDto addCouponDto, int[] msgCodes,
                              Consumer<Integer> onResult,
                              Runnable onFailedLogIn,
                              Runnable onError){
        mysosoService.addCoupons(token, addCouponDto).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
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
                        Log.d("coupon", response.raw().toString());
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

    public void deleteCoupon(String token, long couponId, int[] msgCodes,
                             Consumer<Integer> onResult,
                             Runnable onFailedLogIn,
                             Runnable onError){
        mysosoService.deleteCoupons(token, couponId).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case 200:{
                        onResult.accept(msgCodes[0]);
                        break;
                    }
                    //토큰 실패
                    case 403:{
                        onFailedLogIn.run();
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

}
