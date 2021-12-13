package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.mysoso.model.PointDetailModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointDetailDto {
    @SerializedName("storeName")
    String storeName;

    @SerializedName("phone")
    String phone;

    @SerializedName("lat")
    double lat;

    @SerializedName("lng")
    double lng;

    @SerializedName("logs")
    ArrayList<PointDetailModel> logs = new ArrayList<>();

}
