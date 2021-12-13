package com.sososhopping.customer.shop.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.R;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.shop.dto.CouponListDto;
import com.sososhopping.customer.shop.dto.EventItemListDto;
import com.sososhopping.customer.shop.dto.ItemListDto;
import com.sososhopping.customer.shop.dto.ReportInputDto;
import com.sososhopping.customer.shop.dto.ReviewInputDto;
import com.sososhopping.customer.shop.dto.ReviewListDto;
import com.sososhopping.customer.shop.dto.StoreIdDto;
import com.sososhopping.customer.shop.model.EventDetailModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.service.ShopService;

import java.util.function.Consumer;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopRepository {
    private static ShopRepository instance;
    private final ShopService shopService;

    private ShopRepository() {
        this.shopService = ApiServiceFactory.createService(ShopService.class);
    }

    public static synchronized ShopRepository getInstance() {
        if (instance == null) {
            instance = new ShopRepository();
        }
        return instance;
    }

    public void requestShopIntroduce(String token, int storeId,
                                     Consumer<ShopIntroduceModel> shopIntroduceModel,
                                     Runnable onFailed,
                                     Runnable onError) {
        shopService.requestShopIntroduce(token, storeId).enqueue(new Callback<ShopIntroduceModel>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ShopIntroduceModel> call, Response<ShopIntroduceModel> response) {
                switch (response.code()) {
                    case 200: {
                        shopIntroduceModel.accept(response.body());
                        break;
                    }
                    //검색 없음
                    case 404: {
                        onFailed.run();
                        break;
                    }
                    default: {
                        onError.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopIntroduceModel> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }

    public void changeInterest(String token,
                               int storeId,
                               Runnable onSuccess,
                               Runnable onLogInFailed,
                               Runnable onFailed,
                               Runnable onError) {
        shopService.changeInterest(token, new StoreIdDto(storeId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        //작성 성공
                    case 201: {
                        onSuccess.run();
                        break;
                    }

                    //토큰 검증 실패
                    case 403: {
                        onLogInFailed.run();
                        break;
                    }

                    //내용 오류
                    case 400:

                        //점포 없음
                    case 404: {
                        onFailed.run();
                        Log.d("error Log", response.raw() + "");
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

    public void inputReport(String token, int storeId, ReportInputDto content,
                            Runnable onSuccess,
                            Runnable onLogInFailed,
                            Runnable onFailed,
                            Runnable onError) {
        shopService.inputReports(token, storeId, content).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        //작성 성공
                    case 201: {
                        onSuccess.run();
                        break;
                    }

                    //토큰 검증 실패
                    case 403: {
                        onLogInFailed.run();
                        break;
                    }

                    //내용 오류
                    case 400:

                        //점포 없음
                    case 404: {
                        onFailed.run();
                        Log.d("error Log", response.raw() + "");
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
