package com.sososhopping.customer.mysoso.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.R;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.mysoso.dto.PointDetailDto;
import com.sososhopping.customer.mysoso.dto.PointListDto;
import com.sososhopping.customer.mysoso.service.MysosoService;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MysosoPointRepository {
    private static MysosoPointRepository instance;
    private final MysosoService mysosoService;

    private MysosoPointRepository(){
        this.mysosoService = ApiServiceFactory.createService(MysosoService.class);
    }

    public static synchronized MysosoPointRepository getInstance(){
        if(instance == null){
            instance = new MysosoPointRepository();
        }
        return instance;
    }

    public void requestPointList(String token,
                                 Consumer<PointListDto> onSuccess,
                                 Runnable onFailedLogIn,
                                 Runnable onFailed,
                                 Runnable onError){
        mysosoService.requestPointList(token).enqueue(new Callback<PointListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PointListDto> call, Response<PointListDto> response) {
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
                    case 404:{
                        break;
                    }
                    default:{
                        onFailed.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PointListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestPointDetail(String token,
                                   int storeId,
                                   String at,
                                   int index,
                                   BiConsumer<PointDetailDto, Integer> onSuccess,
                                   Runnable onFailed,
                                   Runnable onError){
        mysosoService.requestPointDetailList(token, storeId, at).enqueue(new Callback<PointDetailDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PointDetailDto> call, Response<PointDetailDto> response) {
                Log.d("log", response.raw().toString());
                switch (response.code()){
                    case 200:
                        onSuccess.accept(response.body(), index);
                        break;

                    //검색 없음
                    case 404:{
                        Log.d("log", response.raw().toString());
                        onFailed.run();
                        break;
                    }
                    default:{
                        onError.run();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PointDetailDto> call, Throwable t) {
                onError.run();
            }
        });
    }

}
