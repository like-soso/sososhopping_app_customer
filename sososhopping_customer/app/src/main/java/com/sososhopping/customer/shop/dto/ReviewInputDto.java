package com.sososhopping.customer.shop.dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewInputDto {
    @SerializedName("score")
    int score;

    @SerializedName("content")
    String reviewContent;
}
