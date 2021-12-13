package com.sososhopping.customer.shop.model;

import com.sososhopping.customer.common.types.BusinessDays;
import com.sososhopping.customer.common.types.Location;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShopIntroduceModel {
    int storeId;
    int ownerId;
    String storeType;
    String name;
    //대표이미지
    String imgUrl;
    String description;
    String extraBusinessDay;
    String phone;
    boolean businessStatus;
    boolean localCurrencyStatus;
    boolean pickupStatus;
    boolean deliveryStatus;
    int deliveryCharge;
    Integer minimumOrderPrice;
    Double saveRate;
    String streetAddress;
    String detailedAddress;
    ArrayList<BusinessDays> businessDays = new ArrayList<>();
    ArrayList<String> storeImages = new ArrayList<>();
    Location location;
    double score;
    boolean isInterestStore;
    int myPoint;
}



