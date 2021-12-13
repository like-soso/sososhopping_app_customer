package com.sososhopping.customer.search.dto;

import com.sososhopping.customer.common.types.PageableDto;
import com.sososhopping.customer.common.types.SortDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableShopListDto{

    ArrayList<ShopInfoShortModel> content = new ArrayList<>();
    PageableDto pageable;
    SortDto sort;

    int numberOfElements;
    int size;
    boolean empty;
}
