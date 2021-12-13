package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.mysoso.model.PointInfoModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointListDto {
    @SerializedName("interestStorePoints")
    ArrayList<PointInfoModel> pointListFavorite = new ArrayList<>();

    @SerializedName("storePoints")
    ArrayList<PointInfoModel> pointListNotFavorite = new ArrayList<>();
}
