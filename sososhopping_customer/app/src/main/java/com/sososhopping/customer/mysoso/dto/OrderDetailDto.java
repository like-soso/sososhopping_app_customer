package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.common.types.enumType.PaymentType;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {
    long orderId;
    long storeId;
    long userId;
    long ownerId;

    ArrayList<OrderDetailItemDto> orderItems = new ArrayList<>();
    String ordererName;
    String ordererPhone;
    OrderType orderType;
    String visitDate;
    String storeName;
    String storePhone;

    int deliveryCharge;
    String deliveryStreetAddress;
    String deliveryDetailedAddress;

    int orderPrice;
    int usedPoint;
    int couponDiscountPrice;
    int finalPrice;

    OrderStatus orderStatus;
    String createdAt;
    PaymentType paymentType;

}
