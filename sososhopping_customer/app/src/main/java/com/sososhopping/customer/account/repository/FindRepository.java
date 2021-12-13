package com.sososhopping.customer.account.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.account.dto.ChangePasswordDto;
import com.sososhopping.customer.account.dto.FindEmailDto;
import com.sososhopping.customer.account.dto.FindInfoDto;
import com.sososhopping.customer.account.service.LogInService;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;

import java.util.function.Consumer;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRepository {
    private static FindRepository instance;
    private final LogInService loginService;

    private FindRepository() {
        this.loginService = ApiServiceFactory.createService(LogInService.class);
    }

    public static synchronized FindRepository getInstance() {
        if (instance == null) {
            instance = new FindRepository();
        }

        return instance;
    }

    public void requestEmail(FindEmailDto dto,
                             Consumer<String> onSuccess,
                             Runnable onNotFound,
                             Runnable onError) {
        loginService.requestEmail(dto).enqueue(new Callback<ResponseBody>() {
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                switch (response.code()) {
                    case 200:
                        onSuccess.accept(response.body().string());
                        break;
                    case 404:
                    default:
                        onNotFound.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }

    public void requestPassword(FindInfoDto dto,
                                Runnable onSuccess,
                                Runnable onNotFound,
                                Runnable onError) {
        loginService.requestPassword(dto).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        onSuccess.run();
                        break;
                    case 404:
                    default:
                        onNotFound.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void changePassword(ChangePasswordDto dto,
                               Runnable onSuccess,
                               Runnable onFailed,
                               Runnable onError) {
        loginService.changePassword(dto).enqueue(new Callback<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("d", response.raw().toString());
                switch (response.code()) {
                    case 200:
                        onSuccess.run();
                        break;
                    case 404:
                    default:
                        onFailed.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onError.run();
            }
        });
    }

}
