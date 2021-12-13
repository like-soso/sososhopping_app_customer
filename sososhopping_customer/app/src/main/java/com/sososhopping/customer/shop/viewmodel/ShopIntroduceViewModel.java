package com.sososhopping.customer.shop.viewmodel;

import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.repository.ShopRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class ShopIntroduceViewModel {

    private final ShopRepository shopRepository = ShopRepository.getInstance();

    private int storeId;

    public String getBusinessDay(ShopIntroduceModel shopIntroduceModel){
        //월~x 로 계산
        int businessDaySize = shopIntroduceModel.getBusinessDays().size();
        int[] group = new int[businessDaySize];
        Arrays.fill(group, -1);

        for(int i=0; i<businessDaySize; i++){
            if(!shopIntroduceModel.getBusinessDays().get(i).getIsOpen()){
                group[i] = -2;
            }
        }
        HashMap<Integer, String> weekDay = new HashMap<>();
        for(int i=0; i<businessDaySize; i++){
            if(group[i] != -1) continue;
            group[i] = i;
            weekDay.put(i, shopIntroduceModel.getBusinessDays().get(i).getDay().getValue());

            for(int j=i+1; j<businessDaySize; j++){
                if(group[j] != -1) continue;

                if(shopIntroduceModel.getBusinessDays().get(i).getOpenTime().equals(shopIntroduceModel.getBusinessDays().get(j).getOpenTime()) &&
                        shopIntroduceModel.getBusinessDays().get(i).getCloseTime().equals(shopIntroduceModel.getBusinessDays().get(j).getCloseTime())){
                    group[j] = i;
                    weekDay.put(i, weekDay.get(i) +", "+shopIntroduceModel.getBusinessDays().get(j).getDay().getValue());
                }
            }
        }

        //-2 = 영업안함, 나머지는 알아서 묶임
        StringBuilder businessDay = new StringBuilder();

        for (Integer key : weekDay.keySet()){
            businessDay.append(weekDay.get(key));
            businessDay.append(" : ");
            businessDay.append(shopIntroduceModel.getBusinessDays().get(key).getOpenTime().substring(0,2));
            businessDay.append(":");
            businessDay.append(shopIntroduceModel.getBusinessDays().get(key).getOpenTime().substring(2,4));
            businessDay.append(" ~ ");
            businessDay.append(shopIntroduceModel.getBusinessDays().get(key).getCloseTime().substring(0,2));
            businessDay.append(":");
            businessDay.append(shopIntroduceModel.getBusinessDays().get(key).getCloseTime().substring(2,4));
            businessDay.append("\n");
        }
        if(businessDay.length() > 0){
            businessDay.delete(businessDay.lastIndexOf("\n"), businessDay.length());
        }

        return businessDay.toString();
    }

    public String getAddress(ShopIntroduceModel shopIntroduceModel){
        String addressText = "";

        if(shopIntroduceModel.getStreetAddress() != null){
            addressText = addressText.concat(shopIntroduceModel.getStreetAddress());
        }

        if(shopIntroduceModel.getDetailedAddress() != null){
            addressText = addressText.concat(" "+shopIntroduceModel.getDetailedAddress());
        }
        return addressText;
    }

    public String getMinimum(ShopIntroduceModel shopIntroduceModel){
        return "온라인으로는 " + shopIntroduceModel.getMinimumOrderPrice() + " 원 이상부터 구매가 가능합니다.";
    }

    public String getSaveRate(ShopIntroduceModel shopIntroduceModel){
        return "구매 금액의 " + shopIntroduceModel.getSaveRate()+"% 적립";
    }

    public void requestShopIntroduce(String token,
                                     Consumer<ShopIntroduceModel> onSuccess,
                                     Runnable onFailed,
                                     Runnable onError){
        shopRepository.requestShopIntroduce(token, storeId, onSuccess, onFailed, onError );
    }

    public void requestShopFavoriteChange(String token,
                                          Runnable onSuccess,
                                          Runnable onFailedLogIn,
                                          Runnable onFailed,
                                          Runnable onError){
        shopRepository.changeInterest(token,storeId, onSuccess, onFailedLogIn, onFailed, onError);
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public ShopInfoShortModel toShort(ShopIntroduceModel s, float distance){
        return new ShopInfoShortModel(
                s.getStoreId(),
                s.getOwnerId(),
                s.getStoreType(),
                s.getName(),
                s.getDescription(),
                s.getPhone(),
                s.getImgUrl(),
                s.isBusinessStatus(),
                s.isLocalCurrencyStatus(),
                s.isPickupStatus(),
                s.isDeliveryStatus(),
                s.getLocation(),
                distance,
                s.getScore(),
                s.isInterestStore()
        );
    }
}
