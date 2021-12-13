package com.sososhopping.customer.shop.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.shop.dto.PageableReviewListDto;
import com.sososhopping.customer.shop.dto.ReviewInputDto;
import com.sososhopping.customer.shop.dto.ReviewListDto;
import com.sososhopping.customer.shop.model.ReviewModel;
import com.sososhopping.customer.shop.repository.ShopReviewRepository;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopReviewViewModel extends ViewModel {
    private final ShopReviewRepository shopRepository = ShopReviewRepository.getInstance();

    MutableLiveData<ArrayList<ReviewModel>> reviewModels = new MutableLiveData<>();

    int offset;
    int numberOfElement;

    public void init(){
        this.offset = 0;
        this.numberOfElement = Constant.LIMIT_PAGE;
        reviewModels.setValue(new ArrayList<>());
    }

    public void requestShopReviewsPage(int storeId,
                                   Integer offset,
                                   Consumer<PageableReviewListDto> onSuccess,
                                   Runnable onFailed,
                                   Runnable onError){

        if(offset == null){
            offset = this.offset;
        }
        shopRepository.requestShopReviewsPage(storeId, offset, onSuccess, onFailed, onError);
    }

    public ReviewInputDto getReviewInputDto(int score, String content){
        return new ReviewInputDto(score, content);
    }

    public void inputShopReviews(String token, int storeId, ReviewInputDto reviewInputDto,
                                 Runnable onSuccess,
                                 Runnable onFailedLogIn,
                                 Runnable onFailed,
                                 Runnable onError){
        shopRepository.inputReview(token, storeId, reviewInputDto, onSuccess, onFailedLogIn, onFailed, onError);
    }

    public void checkShopReview(String token, int storeId,
                                Runnable onDup,
                                Runnable onFailed,
                                Runnable onError){
        shopRepository.checkReviews(token, storeId, onDup, onFailed, onError);
    }

    public void deleteShopReview(String token,
                                 int shopId,
                                 Runnable onSuccess,
                                 Runnable onFailedDelete,
                                 Runnable onError){
        shopRepository.deleteMyReview(token, shopId, onSuccess, onFailedDelete, onError);
    }


}
