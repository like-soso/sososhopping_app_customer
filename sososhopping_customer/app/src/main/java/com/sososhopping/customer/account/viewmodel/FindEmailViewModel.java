package com.sososhopping.customer.account.viewmodel;

import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.account.dto.FindEmailDto;
import com.sososhopping.customer.account.repository.FindRepository;

import java.util.function.Consumer;

public class FindEmailViewModel extends ViewModel {
    private final FindRepository findRepository = FindRepository.getInstance();

    public void requestEmail(FindEmailDto dto,
                             Consumer<String> onSuccess,
                             Runnable onNotFound,
                             Runnable onError){
        findRepository.requestEmail(dto,onSuccess,onNotFound,onError);
    }
}
