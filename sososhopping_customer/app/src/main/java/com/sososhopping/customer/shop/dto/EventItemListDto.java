package com.sososhopping.customer.shop.dto;

import com.sososhopping.customer.shop.model.EventItemModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventItemListDto {
    ArrayList<EventItemModel> results = new ArrayList<>();
}
