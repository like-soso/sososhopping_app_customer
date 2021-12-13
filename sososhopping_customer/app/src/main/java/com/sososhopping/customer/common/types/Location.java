package com.sososhopping.customer.common.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    double lat = 0.0;
    double lng = 0.0;
}