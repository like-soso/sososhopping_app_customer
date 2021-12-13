package com.sososhopping.customer.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindInfoDto {
    String name;
    String phone;
    String email;
}
