package com.sososhopping.customer.common.types;

import com.sososhopping.customer.common.types.enumType.WeekType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDays{
    WeekType day;
    Boolean isOpen;
    String openTime;
    String closeTime;
}

