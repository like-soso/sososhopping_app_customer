package com.sososhopping.customer.shop.dto;

import com.sososhopping.customer.common.types.PageableDto;
import com.sososhopping.customer.common.types.SortDto;
import com.sososhopping.customer.shop.model.ReviewModel;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableReviewListDto {

    ArrayList<ReviewModel> content = new ArrayList<>();
    PageableDto pageable;
    SortDto sort;

    int numberOfElements;
}
