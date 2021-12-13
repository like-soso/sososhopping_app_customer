package com.sososhopping.customer.shop.viewmodel;

import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.shop.dto.CouponListDto;
import com.sososhopping.customer.shop.dto.EventItemListDto;
import com.sososhopping.customer.shop.dto.PageableWritingListDto;
import com.sososhopping.customer.shop.model.CouponModel;
import com.sososhopping.customer.shop.model.EventItemModel;
import com.sososhopping.customer.shop.repository.ShopEventRepository;
import com.sososhopping.customer.shop.repository.ShopRepository;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ShopEventViewModel {

    private final ShopEventRepository shopRepository = ShopEventRepository.getInstance();

    ArrayList<CouponModel> couponModels = new ArrayList<>();
    ArrayList<EventItemModel> eventItemModels = new ArrayList<>();

    int offset;
    int numberOfElement;

    public void init(){
        this.offset = 0;
        this.numberOfElement = Constant.LIMIT_PAGE;
    }

    public void requestShopCoupon(int storeId, Consumer<CouponListDto> couponModel,
                                  Runnable onFailed,
                                  Runnable onError){
        shopRepository.requestShopCoupon(storeId, couponModel, onFailed, onError);
    }

    public void requestShopEvent(int storeId,
                                 Integer offset,
                                 Consumer<PageableWritingListDto> eventItemModel,
                                 Runnable onFailed,
                                 Runnable onError){


        if(offset == null){
            offset = this.offset;
        }

        shopRepository.requestShopWritingPage(storeId, offset, eventItemModel,onFailed,onError);
    }

    public AddCouponDto setAddCouponDto(String code){
        AddCouponDto dto = new AddCouponDto();
        dto.setCouponCode(code);
        return dto;
    }

    public void addShopCoupon(String token, String code,
                              int[] msgs,
                              Consumer<Integer> onResult,
                              Runnable onFailedLogIn,
                              Runnable onError){
        shopRepository.addShopCoupon(token, setAddCouponDto(code), msgs, onResult, onFailedLogIn, onError);
    }
}
