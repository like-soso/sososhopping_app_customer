package com.sososhopping.customer.shop.model;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.common.types.enumType.WritingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventItemModel {
    @SerializedName("writingId")
    int writingId;

    @SerializedName("title")
    String title;

    @SerializedName("content")
    String description;

    @SerializedName("createdAt")
    String createdAt;

    @SerializedName("writingType")
    WritingType writingType;

    @SerializedName("imgUrl")
    String imgUrl;

}
