package com.sososhopping.customer.search.dto;

import com.sososhopping.customer.common.types.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoDto {
    private int storeId;
    private String storeType;
    private String name;
    private String imgUrl;

    private boolean businessStatus;
    private boolean localCurrencyStatus;
    private boolean pickupStatus;
    private boolean deliveryStatus;

    private Location location = new Location();

    private double score;
    private boolean isInterestStore;
}
