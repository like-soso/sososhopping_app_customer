package com.sososhopping.customer.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopItemModel {
    int itemId;
    String name;
    String description;
    String purchaseUnit;
    String imgUrl;
    int price;
    Boolean saleStatus;
}
