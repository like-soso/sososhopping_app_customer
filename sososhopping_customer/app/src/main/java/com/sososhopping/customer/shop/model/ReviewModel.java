package com.sososhopping.customer.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModel {
    int storeId;
    int userId;
    String nickname;
    String content;
    //이미지는 일단 생략
    //String imgUrl;
    float score;
    String createdAt;
}
