package com.sososhopping.customer.mysoso.dto;

import com.google.gson.annotations.SerializedName;
import com.sososhopping.customer.mysoso.model.MyreviewModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import retrofit2.http.GET;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyReviewsDto {
    @SerializedName("results")
    ArrayList<MyreviewModel> myreviews= new ArrayList<>();
}
