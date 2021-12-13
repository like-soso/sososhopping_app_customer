package com.sososhopping.customer.mysoso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailItemDto {
    String itemName;
    String description;
    int quantity;
    int totalPrice;
}