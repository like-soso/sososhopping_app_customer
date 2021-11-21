package com.sososhopping.customer.shop.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.shop.dto.ReviewInputDto;
import com.sososhopping.customer.shop.dto.ReviewListDto;
import com.sososhopping.customer.shop.service.ShopService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopReviewRepository {
    private static ShopReviewRepository instance;
    private final ShopService shopService;

    private ShopReviewRepository(){
        this.shopService = ApiServiceFactory.createService(ShopService.class);
    }

    public static synchronized ShopReviewRepository getInstance() {
        if(instance == null){
            instance = new ShopReviewRepository();
        }
        return instance;
    }


    public void requestShopReviews(int storeId, Consumer<ReviewListDto> reviewModel,
                                   Runnable onFailed,
                                   Runnable onError) {
        shopService.requestReviews(storeId).enqueue(new Callback<ReviewListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ReviewListDto> call, Response<ReviewListDto> response) {
                switch (response.code()) {
                    case 200: {
                        reviewModel.accept(response.body());
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
            public void onFailure(Call<ReviewListDto> call, Throwable t) {
                onError.run();
            }
        });
    }


    public void inputReview(String token,
                            int storeId, ReviewInputDto reviewInputDto,
                            Runnable onSuccess,
                            Runnable onFailedLogIn,
                            Runnable onFailed,
                            Runnable onError){

        shopService.inputReviews(token, storeId, reviewInputDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        //작성 성공
                    case 201:{
                        onSuccess.run();
                        break;
                    }

                    //내용 오류
                    case 400:

                        //토큰 검증 실패
                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }

                    //점포 없음
                    case 404:

                        //리뷰 중복 작성
                    case 409: {
                        onFailed.run();
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