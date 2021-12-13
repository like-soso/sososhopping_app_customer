package com.sososhopping.customer.shop.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.sososhopping.customer.shop.dto.AddCartDto;
import com.sososhopping.customer.shop.dto.ItemListDto;
import com.sososhopping.customer.shop.model.ShopItemModel;
import com.sososhopping.customer.shop.repository.ShopItemRepository;
import com.sososhopping.customer.shop.repository.ShopRepository;
import com.sososhopping.customer.shop.repository.ShopReviewRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopItemViewModel extends ViewModel {
    private final ShopItemRepository shopRepository = ShopItemRepository.getInstance();

    MutableLiveData<ArrayList<ShopItemModel>> shopItem = new MutableLiveData<>();

    public void requestShopItem(int storeId,
                                Consumer<ItemListDto> onSuccess,
                                Runnable onFailed,
                                Runnable onError){
        shopRepository.requestShopItem(storeId,onSuccess,onFailed, onError);
    }

    public void addCart(String token, int itemId, int quantity,
                        Runnable onSuccess,
                        Runnable onDup,
                        Runnable onFailed,
                        Runnable onError){

        shopRepository.addCart(token, new AddCartDto(itemId, quantity), onSuccess, onDup, onFailed, onError);
    }

}
