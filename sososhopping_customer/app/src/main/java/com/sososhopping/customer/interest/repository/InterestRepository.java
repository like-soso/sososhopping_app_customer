package com.sososhopping.customer.interest.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.interest.service.InterestService;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.shop.dto.StoreIdDto;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterestRepository {

    private static InterestRepository instance;
    private final InterestService interestService;

    private InterestRepository(){
        this.interestService = ApiServiceFactory.createService(InterestService.class);
    }

    public static synchronized InterestRepository getInstance(){
        if(instance == null){
            instance = new InterestRepository();
        }
        return instance;
    }

    public void requestInterest(String token,
                                Consumer<ShopListDto> onSuccess,
                                Runnable onFailedLogIn,
                                Runnable onError){

        interestService.requestInterestList(token).enqueue(new Callback<ShopListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ShopListDto> call, Response<ShopListDto> response) {
                switch (response.code()) {
                    case 200:{
                        onSuccess.accept(response.body());
                        break;
                    }
                        //토큰 검증 실패
                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }

                    default: {
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void changeInterest(String token,
                               int storeId,
                               Runnable onSuccess,
                               Runnable onLogInFailed,
                               Runnable onFailed,
                               Runnable onError){
        interestService.changeInterest(token, new StoreIdDto(storeId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        //작성 성공
                    case 201:{
                        onSuccess.run();
                        break;
                    }

                    //토큰 검증 실패
                    case 403:{
                        onLogInFailed.run();
                        break;
                    }

                    //내용 오류
                    case 400:

                        //점포 없음
                    case 404:{
                        onFailed.run();
                        Log.d("error Log", response.raw()+"");
                        break;
                    }

                    default: {
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });
    }
}
