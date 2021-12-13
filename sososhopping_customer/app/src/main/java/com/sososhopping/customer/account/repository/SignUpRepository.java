package com.sososhopping.customer.account.repository;

import com.sososhopping.customer.account.dto.EmailDupCheckRequestDto;
import com.sososhopping.customer.account.dto.NicknameDupCheckRequestDto;
import com.sososhopping.customer.account.dto.PhoneDupCheckRequestDto;
import com.sososhopping.customer.account.dto.SignUpRequestDto;
import com.sososhopping.customer.account.service.SignUpService;
import com.sososhopping.customer.common.retrofit.ApiServiceFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpRepository {
    private static SignUpRepository instance;
    private final SignUpService signupService;

    private SignUpRepository() {
        this.signupService = ApiServiceFactory.createService(SignUpService.class);
    }

    public static synchronized SignUpRepository getInstance() {
        if(instance == null) {
            instance = new SignUpRepository();
        }

        return instance;
    }

    public void requestEmailDuplicationCheck(EmailDupCheckRequestDto dto,
                                             Runnable onNotDuplicated,
                                             Runnable onDuplicated,
                                             Runnable onFailed) {
        signupService.requestEmailDuplicationCheck(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        onNotDuplicated.run();
                        break;
                    case 409:
                        onDuplicated.run();
                        break;
                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                onFailed.run();
            }
        });
    }


    public void requestNicknameDuplicationCheck(NicknameDupCheckRequestDto dto,
                                             Runnable onNotDuplicated,
                                             Runnable onDuplicated,
                                             Runnable onFailed) {
        signupService.requestNicknameDuplicationCheck(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        onNotDuplicated.run();
                        break;
                    case 409:
                        onDuplicated.run();
                        break;
                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                onFailed.run();
            }
        });
    }

    public void requestPhoneDuplicationCheck(PhoneDupCheckRequestDto dto,
                                             Runnable onNotDuplicated,
                                             Runnable onDuplicated,
                                             Runnable onFailed){
        signupService.requestPhoneDuplicationCheck(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        onNotDuplicated.run();
                        break;
                    case 409:
                        onDuplicated.run();
                        break;
                    default:
                        onFailed.run();
                        break;
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                onFailed.run();
            }
        });
    }

    public void requestSignup(SignUpRequestDto dto,
                              Runnable onSuccess,
                              Runnable onFailed){
        signupService.requestSignUp(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 201) onSuccess.run();
                else {
                    response.raw();
                    onFailed.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                onFailed.run();
            }
        });
    }
}
