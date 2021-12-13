package com.sososhopping.customer.mysoso.viemodel;

import com.sososhopping.customer.mysoso.dto.MyReviewsDto;
import com.sososhopping.customer.mysoso.repository.MysosoMyReviewsRepository;

import java.util.function.Consumer;

public class MyReviewViewModel {
    private final MysosoMyReviewsRepository myReviewsRepository = MysosoMyReviewsRepository.getInstance();

    public void requestMyReview(String token,
                                Consumer<MyReviewsDto> onSuccess,
                                Runnable onFailedLogin,
                                Runnable onFailed,
                                Runnable onError){
        myReviewsRepository.requestMyReviews(token,onSuccess,onFailedLogin,onFailed,onError);
    }

    public void deleteMyReview(String token,
                               int storeId,
                               int position,
                               Consumer<Integer> onSuccess,
                               Runnable onFailed,
                               Runnable onError){
        myReviewsRepository.deleteMyReview(token, storeId, position, onSuccess, onFailed, onError);
    }
}
