package com.sososhopping.customer.mysoso.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.mysoso.dto.MyInfoEditDto;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.mysoso.service.MysosoService;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MysosoMyInfoRepository {
    private static MysosoMyInfoRepository instance;
    private final MysosoService mysosoService;

    private MysosoMyInfoRepository(){
        this.mysosoService = ApiServiceFactory.createService(MysosoService.class);
    }

    public static synchronized MysosoMyInfoRepository getInstance(){
        if(instance == null){
            instance = new MysosoMyInfoRepository();
        }
        return instance;
    }

    public void requestMyInfo(String token,
                              Consumer<MyInfoModel> onSuccess,
                              Runnable onFailedLogIn,
                              Runnable onFailed,
                              Runnable onError){
        mysosoService.requestMyInfo(token).enqueue(new Callback<MyInfoModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<MyInfoModel> call, Response<MyInfoModel> response) {
                switch (response.code()){
                    case 200: {
                        onSuccess.accept(response.body());
                        break;
                    }
                    case 403:{
                        onFailedLogIn.run();
                        break;
                    }
                    default:{
                        onFailed.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<MyInfoModel> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestEditInfo(String token,
                                MyInfoEditDto dto,
                                Runnable onSuccess,
                                Runnable onError){
        mysosoService.requestEditMyInfo(token, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.run();
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

    public void requestQuit(String token,
                            Runnable onSuccess,
                            Runnable onFailed){
        mysosoService.requestQuit(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("log", response.raw().toString());
                switch (response.code()){
                    case 200:
                        onSuccess.run();
                        break;

                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onFailed.run();
            }
        });
    }
}
