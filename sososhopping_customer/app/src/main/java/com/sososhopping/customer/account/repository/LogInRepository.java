package com.sososhopping.customer.account.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.account.dto.LogInRequestDto;
import com.sososhopping.customer.account.dto.LogInResponseDto;
import com.sososhopping.customer.account.service.LogInService;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInRepository {
    private static LogInRepository instance;
    private final LogInService loginService;

    private LogInRepository() {
        this.loginService = ApiServiceFactory.createService(LogInService.class);
    }

    public static synchronized LogInRepository getInstance() {
        if(instance == null) {
            instance = new LogInRepository();
        }

        return instance;
    }

    public void requestLogin(LogInRequestDto dto,
                             BiConsumer<LogInRequestDto, LogInResponseDto> onSuccess,
                             Runnable onFailed,
                             Runnable onError) {
        loginService.requestLogin(dto).enqueue(new Callback<LogInResponseDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<LogInResponseDto> call, Response<LogInResponseDto> response) {
                if (response.code() == 200) {
                    onSuccess.accept(dto,response.body());
                } else {
                    onFailed.run();
                }
            }

            @Override
            public void onFailure(Call<LogInResponseDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void autoLogin(LogInRequestDto dto,
                          Consumer<LogInResponseDto> onSuccess,
                          Runnable onFailed){
        loginService.requestLogin(dto).enqueue(new Callback<LogInResponseDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<LogInResponseDto> call, Response<LogInResponseDto> response) {
                if (response.code() == 200) {
                    onSuccess.accept(response.body());
                } else {
                    onFailed.run();
                }
            }

            @Override
            public void onFailure(Call<LogInResponseDto> call, Throwable t) {
                onFailed.run();
            }
        });
    }
}