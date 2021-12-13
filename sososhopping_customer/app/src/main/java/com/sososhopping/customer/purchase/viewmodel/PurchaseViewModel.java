package com.sososhopping.customer.purchase.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.purchase.dto.CartItemDto;
import com.sososhopping.customer.purchase.dto.CartStoreDto;
import com.sososhopping.customer.purchase.dto.CartUpdateDto;
import com.sososhopping.customer.purchase.model.OrderRequestModel;
import com.sososhopping.customer.purchase.repository.PurchaseRepository;
import com.sososhopping.customer.common.types.enumType.CouponType;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.common.types.enumType.PaymentType;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.mysoso.repository.MysosoMyInfoRepository;
import com.sososhopping.customer.shop.model.CouponModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.repository.ShopRepository;

import java.util.ArrayList;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class PurchaseViewModel extends ViewModel {

    //repository
    private final ShopRepository shopRepository = ShopRepository.getInstance();
    private final MysosoMyInfoRepository myInfoRepository = MysosoMyInfoRepository.getInstance();
    private final PurchaseRepository purchaseRepository = PurchaseRepository.getInstance();

    MutableLiveData<ShopIntroduceModel> shopInfo = new MutableLiveData<>();
    MutableLiveData<CartStoreDto> purchaseList = new MutableLiveData<>();
    MutableLiveData<MyInfoModel> myInfo = new MutableLiveData<>();
    MutableLiveData<CouponModel> useCoupon = new MutableLiveData<>();

    //사용 가능포인트
    MutableLiveData<Integer> maxPoint = new MutableLiveData<>();

    //최종 금액 창
    MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    MutableLiveData<Integer> couponDiscount = new MutableLiveData<>();
    MutableLiveData<Integer> deliveryPrice = new MutableLiveData<>();
    MutableLiveData<Integer> usePoint = new MutableLiveData<>();
    MutableLiveData<Integer> finalPrice = new MutableLiveData<>();

    OrderType orderType;
    PaymentType paymentType;

    public PurchaseViewModel(){
        this.totalPrice.setValue(0);
        this.finalPrice.setValue(0);

        //포인트 관련
        this.maxPoint.setValue(0);

      initPointAndCoupon();
    }

    public void initPointAndCoupon(){
        this.deliveryPrice.setValue(0);
        this.usePoint.setValue(0);
        this.couponDiscount.setValue(0);

        orderType = OrderType.ONSITE;
        paymentType = PaymentType.CASH;
    }

    public OrderRequestModel orderRequestDto(String name, String phone, String date, String time, String road, String detail){
        ArrayList<CartUpdateDto> purchaseItems = new ArrayList<>();

        for(CartItemDto d : purchaseList.getValue().getCartItems()){
            purchaseItems.add(new CartUpdateDto(d.getItemId(), d.getNum()));
        }
        String visitDate = null;
        if(date != null){
            visitDate = date.replaceAll("/","-")+" "+time+":00";
        }

        Long couponCode = null;

        if(useCoupon.getValue() != null){
            couponCode = useCoupon.getValue().getCouponId();
        }

        OrderRequestModel dto = OrderRequestModel.builder()
                .storeId(shopInfo.getValue().getStoreId())
                .orderItems(purchaseItems)
                .orderType(orderType.toString())
                .paymentType(paymentType)
                .usedPoint(usePoint.getValue())
                .couponId(couponCode)
                .finalPrice(finalPrice.getValue())
                .ordererName(name)
                .ordererPhone(phone)
                .visitDate(visitDate)
                .deliveryStreetAddress(road)
                .deliveryDetailedAddress(detail)
                .build();

        return dto;
    }



    public void calFinalPrice(){
        finalPrice.setValue(totalPrice.getValue() - couponDiscount.getValue() - usePoint.getValue() + deliveryPrice.getValue());
    }


    public void setPurchaseList(int storeId, CartItemDto[] items){
        int totalPrice = 0;
        ArrayList<CartItemDto> itemLists = new ArrayList<>();

        for(CartItemDto d : items){
            totalPrice += d.getPrice() * d.getNum();
            itemLists.add(d);
        }
        this.totalPrice.setValue(totalPrice);
        purchaseList.setValue(
                new CartStoreDto(storeId, null, itemLists, totalPrice)
        );
    }

    public void calTotalItemPriceDelete(int pos){
        CartItemDto d = purchaseList.getValue().getCartItems().get(pos);
        int newTotal = totalPrice.getValue() - d.getNum()*d.getPrice();
        this.totalPrice.setValue(newTotal);
    }

    public void calPointMax(){
        if(getShopInfo().getValue() == null){
            getMaxPoint().setValue(0);
        }
        else{
            getMaxPoint().setValue(
                    Math.min(getShopInfo().getValue().getMyPoint(),
                    getTotalPrice().getValue() - getCouponDiscount().getValue()));
        }
    }

    public void calCouponPrice(){
        CouponModel cp = getUseCoupon().getValue();
        if(cp == null){
            couponDiscount.setValue(0);
        }
        else{
            if(cp.getCouponType().equals(CouponType.FIX)){
                //전체금액보다 사용 쿠폰금액이 더 크면
                if(cp.getFixAmount() > totalPrice.getValue()){
                    couponDiscount.setValue(totalPrice.getValue());
                }
                else{
                    couponDiscount.setValue(cp.getFixAmount());
                }
            }
            else{
                couponDiscount.setValue((int)(totalPrice.getValue()*cp.getRateAmount() / 100));
            }
        }
    }

    public void setOrderType(OrderType o){
        this.orderType = o;
    }
    public void setPaymentType(PaymentType p){
        this.paymentType = p;
    }


    public void requestShopIntroduce(String token,
                                     int storeId,
                                     Consumer<ShopIntroduceModel> onSuccess,
                                     Runnable onFailed,
                                     Runnable onError){
        shopRepository.requestShopIntroduce(token, storeId, onSuccess, onFailed, onError );
    }

    public void requestMyInfo(String token,
                              Consumer<MyInfoModel> onSuccess,
                              Runnable onFailed){
       myInfoRepository.requestMyInfo(token, onSuccess, onFailed,  onFailed, onFailed);
    }

    public void requestOrder(String token,
                             OrderRequestModel dto,
                             Runnable onSuccess,
                             Runnable onFailed,
                             Runnable onError){
        purchaseRepository.requestOrders(token,dto, onSuccess, onFailed, onError);
    }
}
