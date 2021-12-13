package com.sososhopping.customer.mysoso.viemodel;

import com.sososhopping.customer.mysoso.dto.PointListDto;
import com.sososhopping.customer.mysoso.repository.MysosoPointRepository;

import java.util.function.Consumer;

public class PointInfoViewModel {
    private final MysosoPointRepository mysosoPointRepository = MysosoPointRepository.getInstance();

    public void requestPointList(String token,
                                 Consumer<PointListDto>onSuccess,
                                 Runnable onFailedLogIn,
                                 Runnable onFailed,
                                 Runnable onError){
        mysosoPointRepository.requestPointList(token, onSuccess, onFailedLogIn, onFailed, onError);
    }
}
