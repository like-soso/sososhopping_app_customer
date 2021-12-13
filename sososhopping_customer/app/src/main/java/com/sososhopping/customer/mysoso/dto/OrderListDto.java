package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.mysoso.model.OrderRecordShortModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDto {
    @SerializedName("results")
    ArrayList<OrderRecordShortModel> results = new ArrayList<>();
}
