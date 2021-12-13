package com.sososhopping.customer.purchase.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.purchase.dto.CartDto;
import com.sososhopping.customer.purchase.dto.CartItemDto;
import com.sososhopping.customer.purchase.dto.CartStoreDto;
import com.sososhopping.customer.purchase.dto.CartUpdateListDto;
import com.sososhopping.customer.purchase.dto.CartUpdateDto;
import com.sososhopping.customer.purchase.repository.CartRepository;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartViewModel extends ViewModel {

    MutableLiveData<ArrayList<CartStoreDto>> stores = new MutableLiveData<>();
    MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    CartRepository cartRepository = CartRepository.getInstance();

    public int calTotalStore( ){
        return stores.getValue().size();
    }

    public int calTotalPrice(){
        int totalPrice = 0;
        for (CartStoreDto c : stores.getValue()){
            totalPrice += c.getTotalPrice();
        }
        this.totalPrice.setValue(totalPrice);
        return totalPrice;
    }

    public int calTotalStorePrice(int position){
        int price = 0;

        for(CartItemDto d : stores.getValue().get(position).getCartItems()){
            price += d.getPrice() * d.getNum();
        }

        stores.getValue().get(position).setTotalPrice(price);
        return price;
    }


    //전체 목록
    public ArrayList<CartUpdateDto> getCartList(){
        ArrayList<CartUpdateDto> totalList = null;
        if(stores.getValue() != null){
         totalList = new ArrayList<>();
            for(CartStoreDto c : stores.getValue()){
                for(CartItemDto d : c.getCartItems()){
                    totalList.add(totalList.size(), new CartUpdateDto(d.getItemId(), d.getNum()));
                }
            }
        }

        return totalList;
    }

    public CartItemDto[] getPurchaseList(CartStoreDto dto){
        ArrayList<CartItemDto> purchaseList = new ArrayList<>();
        for(CartItemDto d : dto.getCartItems()){
            if(d.isPurchase()){
                purchaseList.add(d);
            }
        }
        return purchaseList.toArray(new CartItemDto[purchaseList.size()]);
    }

    public void requestMyCart(String token,
                              Consumer<CartDto> onSuccess,
                              Runnable onFailedLogIn,
                              Runnable onError){
        cartRepository.requestCartItem(token, onSuccess, onFailedLogIn, onError);
    }

    public void requestDelete(String token, int itemId, int storePos, int itemPos,
                              BiConsumer<Integer,Integer> onSuccess,
                              Runnable onFailed,
                              Runnable onError){
        cartRepository.deleteItem(token,itemId, storePos, itemPos, onSuccess,onFailed,onError);
    }

    public void updateItem(String token, ArrayList<CartUpdateDto> dtos){
        if(dtos != null){
            cartRepository.updateItem(token,new CartUpdateListDto(dtos));
        }
    }
}
