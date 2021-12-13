package com.sososhopping.customer.purchase.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.purchase.dto.CartDto;
import com.sososhopping.customer.purchase.dto.CartUpdateListDto;
import com.sososhopping.customer.purchase.service.CartService;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private static CartRepository instance;
    private final CartService cartService;

    private CartRepository(){
        this.cartService = ApiServiceFactory.createService(CartService.class);
    }

    public static synchronized CartRepository getInstance(){
        if(instance == null){
            instance = new CartRepository();
        }
        return instance;
    }

    public void requestCartItem(String token,
                                Consumer<CartDto> onSuccess,
                                Runnable onFailedLogIn,
                                Runnable onError){

        cartService.requestMyCart(token).enqueue(new Callback<CartDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CartDto> call, Response<CartDto> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.accept(response.body());
                        break;
                    }

                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }

                    default:{
                        onError.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void deleteItem(String token,
                           int itemId,
                           int storePos,
                           int itemPos,
                           BiConsumer<Integer, Integer> onSuccess,
                           Runnable onFailed,
                           Runnable onError){
        cartService.requestDeleteItem(token, itemId).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.accept(storePos, itemPos);
                        break;
                    }
                    default:{
                        onFailed.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void updateItem(String token,
               CartUpdateListDto dto){
        cartService.requestUpdateItem(token, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                Log.e("error",response.raw().toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
