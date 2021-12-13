package com.sososhopping.customer.shop.viewmodel;

import com.sososhopping.customer.shop.model.EventDetailModel;
import com.sososhopping.customer.shop.repository.ShopEventRepository;
import com.sososhopping.customer.shop.repository.ShopRepository;

import java.util.function.Consumer;

public class ShopEventDetailViewModel {
    private final ShopEventRepository shopRepository = ShopEventRepository.getInstance();
    private EventDetailModel eventDetailModel;

    public void requestShopEventDetail(int storeId, int writingId,
                                       Consumer<EventDetailModel> eventDetailModel,
                                       Runnable onFailed,
                                       Runnable onError){
        shopRepository.requestShopEventDetail(storeId, writingId, eventDetailModel, onFailed, onError);
    }
}
