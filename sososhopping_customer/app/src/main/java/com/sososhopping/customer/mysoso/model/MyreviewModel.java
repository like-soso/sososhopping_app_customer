package com.sososhopping.customer.mysoso.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyreviewModel {
    int storeId;
    int userId;
    String storeName;
    String content;
    //이미지는 일단 생략
    //String imgUrl;
    float score;
    String createdAt;
}

