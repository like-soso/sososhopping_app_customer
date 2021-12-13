package com.sososhopping.customer.account.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.account.dto.ChangePasswordDto;
import com.sososhopping.customer.account.dto.FindInfoDto;
import com.sososhopping.customer.account.repository.FindRepository;

import lombok.Getter;

@Getter
public class FindPasswordViewModel extends ViewModel {
    private final FindRepository findRepository = FindRepository.getInstance();

    MutableLiveData<FindInfoDto> dto = new MutableLiveData<>();

    public ChangePasswordDto getChangePasswordDto(String password){
        return new ChangePasswordDto(
                dto.getValue().getName(),
                dto.getValue().getPhone(),
                dto.getValue().getEmail(),
                password
        );
    }

    public void requestPassword(FindInfoDto dto,
                                Runnable onSuccess,
                                Runnable onNotFound,
                                Runnable onFailed){
        this.dto.setValue(dto);
        findRepository.requestPassword(dto,onSuccess,onNotFound,onFailed);
    }

    public void changePassword(ChangePasswordDto dto,
                               Runnable onSuccess,
                               Runnable onNotFound,
                               Runnable onFailed){
        findRepository.changePassword(dto,onSuccess,onNotFound,onFailed);
    }
}
