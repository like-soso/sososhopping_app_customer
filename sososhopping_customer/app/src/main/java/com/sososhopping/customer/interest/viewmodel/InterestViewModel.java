package com.sososhopping.customer.interest.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.gps.CalculateDistance;
import com.sososhopping.customer.common.gps.GPSTracker;
import com.sososhopping.customer.interest.repository.InterestRepository;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ShopInfoShortModel>> favoriteList = new MutableLiveData<>();
    private InterestRepository interestRepository = InterestRepository.getInstance();

    public void requestInterest(String token,
                                Consumer<ShopListDto> onSuccess,
                                Runnable onFailedLogIn,
                                Runnable onError){
            interestRepository.requestInterest(token,onSuccess,onFailedLogIn,onError);
    }

    public void changeInterest(String token, int storeId,
                               Runnable onSuccess,
                               Runnable onFailedLogIn,
                               Runnable onFailed,
                               Runnable onError){
        interestRepository.changeInterest(token, storeId, onSuccess, onFailedLogIn, onFailed, onError);
    }
}
