package com.sososhopping.customer.common.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableDto {

    int offset;

    SortDto sort;

    boolean paged;
    boolean unpaged;
    int pageNumber;
    int pageSize;
}
