package com.sososhopping.customer.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePasswordDto{
    String name;
    String phone;
    String email;
    String password;
}
