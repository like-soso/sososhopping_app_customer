package com.sososhopping.customer.mysoso.viemodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sososhopping.customer.mysoso.dto.AddCouponDto;
import com.sososhopping.customer.mysoso.dto.MyCouponsDto;
import com.sososhopping.customer.mysoso.repository.MysosoCouponRepository;
import com.sososhopping.customer.mysoso.view.adapter.ExpandableCouponData;
import com.sososhopping.customer.mysoso.view.adapter.MysosoCouponAdapter;
import com.sososhopping.customer.shop.model.CouponModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class MyCouponViewModel extends ViewModel {
    private final MysosoCouponRepository mysosoCouponRepository = MysosoCouponRepository.getInstance();
    MutableLiveData<ArrayList<CouponModel>> myCoupons = new MutableLiveData<>();


    public void setMyCoupons(ArrayList<CouponModel> coupons){
        this.myCoupons.setValue(coupons);
    }

    public ArrayList<ExpandableCouponData> parser(){

        LinkedHashMap<String, ArrayList<CouponModel>> map = new LinkedHashMap<>();
        ArrayList<ExpandableCouponData> parse = new ArrayList<>();

        //1. parse with storeName
        for (CouponModel c : myCoupons.getValue()){
            if(!map.containsKey(c.getStoreName())){
                map.put(c.getStoreName(), new ArrayList<>());
            }
            map.get(c.getStoreName()).add(c);
        }

        //2. fill new ArrayList
        for(String storeName : map.keySet()){
            parse.add(new ExpandableCouponData(MysosoCouponAdapter.HEADER,storeName));

            for(CouponModel c : map.get(storeName)){
                parse.add(new ExpandableCouponData(MysosoCouponAdapter.CHILD, c));
            }
        }
        return parse;
    }

    public void requestCoupons(String token,
                               Integer storeId,
                               Consumer<MyCouponsDto> onSuccess,
                               Runnable onFailedLogIn,
                               Runnable onFailed,
                               Runnable onError){
        mysosoCouponRepository.requestCoupon(token,storeId, onSuccess, onFailedLogIn, onFailed, onError);
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
        mysosoCouponRepository.addShopCoupon(token, setAddCouponDto(code), msgs, onResult, onFailedLogIn, onError);
    }

    public void deleteCoupon(String token, long couponId,
                             int[] msgs,
                             Consumer<Integer> onResult,
                             Runnable onFailedLogIn,
                             Runnable onError) {
        mysosoCouponRepository.deleteCoupon(token, couponId, msgs, onResult, onFailedLogIn, onError);
    }
}
