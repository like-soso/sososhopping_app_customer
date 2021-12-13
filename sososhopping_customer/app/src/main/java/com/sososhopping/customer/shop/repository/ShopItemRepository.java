package com.sososhopping.customer.shop.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.shop.dto.AddCartDto;
import com.sososhopping.customer.shop.dto.ItemListDto;
import com.sososhopping.customer.shop.service.ShopService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopItemRepository {
    private static ShopItemRepository instance;
    private final ShopService shopService;

    private ShopItemRepository(){
        this.shopService = ApiServiceFactory.createService(ShopService.class);
    }

    public static synchronized ShopItemRepository getInstance() {
        if(instance == null){
            instance = new ShopItemRepository();
        }
        return instance;
    }

    public void requestShopItem(int storeId, Consumer<ItemListDto> item,
                                Runnable onFailed,
                                Runnable onError){
        shopService.requestShopItem(storeId).enqueue(new Callback<ItemListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ItemListDto> call, Response<ItemListDto> response) {
                switch (response.code()){
                    case 200:{
                        item.accept(response.body());
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
            public void onFailure(Call<ItemListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void addCart(String token, AddCartDto addCartDto,
                        Runnable onSuccess,
                        Runnable onDup,
                        Runnable onFailed,
                        Runnable onError){
        shopService.addCart(token, addCartDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case 201:{
                        onSuccess.run();
                        break;
                    }
                    //검색 없음
                    case 404:{
                        onFailed.run();
                        break;
                    }
                    //중복
                    case 409:{
                        onDup.run();
                        break;
                    }
                    default:{
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
