package com.sososhopping.customer.mysoso.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoModel {
    String email;
    String password;
    String name;
    String phone;
    String nickname;
    String streetAddress;
    String detailedAddress;
}
