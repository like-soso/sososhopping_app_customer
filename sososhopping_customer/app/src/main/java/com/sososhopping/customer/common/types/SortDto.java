package com.sososhopping.customer.common.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortDto {
    boolean empty;
    boolean unsorted;
    boolean sorted;
}
