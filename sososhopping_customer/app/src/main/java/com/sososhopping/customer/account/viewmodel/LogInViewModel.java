package com.sososhopping.customer.account.viewmodel;

import android.util.Log;

import com.sososhopping.customer.account.dto.LogInRequestDto;
import com.sososhopping.customer.account.dto.LogInResponseDto;
import com.sososhopping.customer.account.repository.LogInRepository;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LogInViewModel {

    private final LogInRepository loginRepository = LogInRepository.getInstance();

    public void requestLogin(String email, String password,
                             BiConsumer<LogInRequestDto, LogInResponseDto> onSuccess,
                             Runnable onFailed,
                             Runnable onError){
        loginRepository.requestLogin(new LogInRequestDto(email, password), onSuccess, onFailed, onError);
    }

    public void autoLogin(String id, String password,
                          Consumer<LogInResponseDto> onSuccess,
                          Runnable onFailed){
        loginRepository.autoLogin(new LogInRequestDto(id, password),onSuccess, onFailed);
    }

}
