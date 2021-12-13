package com.sososhopping.customer.common.retrofit;

import com.sososhopping.customer.common.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceFactory {

    private final static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <T> T createService(Class<T> serviceClass){
        return retrofit.create(serviceClass);
    }
}