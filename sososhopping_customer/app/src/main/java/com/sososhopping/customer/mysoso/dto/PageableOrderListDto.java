package com.sososhopping.customer.mysoso.dto;

import com.sososhopping.customer.common.types.PageableDto;
import com.sososhopping.customer.common.types.SortDto;
import com.sososhopping.customer.mysoso.model.OrderRecordShortModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableOrderListDto {

    ArrayList<OrderRecordShortModel> content = new ArrayList<>();

    PageableDto pageable;
    SortDto sort;

    int numberOfElements;

}
