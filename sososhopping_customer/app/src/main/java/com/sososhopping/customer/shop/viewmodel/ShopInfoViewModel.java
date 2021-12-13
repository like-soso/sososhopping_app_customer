package com.sososhopping.customer.shop.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.shop.dto.ReportInputDto;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.repository.ShopRepository;

import lombok.Getter;


@Getter
public class ShopInfoViewModel extends ViewModel {
    MutableLiveData<Integer> shopId = new MutableLiveData<>();
    MutableLiveData<Integer> ownerId = new MutableLiveData<>();
    MutableLiveData<String> shopName = new MutableLiveData<>();
    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<Location> location = new MutableLiveData<>();
    MutableLiveData<ShopIntroduceModel> shopIntroduceModel = new MutableLiveData<>();
    MutableLiveData<Float> distance = new MutableLiveData<>();

    private final ShopRepository shopRepository = ShopRepository.getInstance();

    public void setShopId(int i){
        this.shopId.setValue(i);
    }

    public void setShopName(String name){
        this.shopName.setValue(name);
    }

    public void setPhone(String phone){
        this.phone.setValue(phone);
    }

    public void setLocation(Location location){
        this.location.setValue(location);
    }

    public void setOwnerId(int i) {this.ownerId.setValue(i);}

    public void inputReport(String token, int shopId, String report,
                            Runnable onSuccess,
                            Runnable onLogInFailed,
                            Runnable onFailed,
                            Runnable onError){
        shopRepository.inputReport(token, shopId, ReportInputDto.builder().report(report).build(),
                onSuccess, onLogInFailed, onFailed, onError);
    }

    public void setDistance(float distance){
        this.distance.setValue(distance);
    }


    public void setItems(ShopIntroduceModel item){
        setShopId(item.getStoreId());
        setOwnerId(item.getOwnerId());

        setShopName(item.getName());
        setPhone(item.getPhone());
        setLocation(item.getLocation());

        this.shopIntroduceModel.setValue(item);
    }
}
