package com.sososhopping.customer.common.types.enumType;

public enum WritingType {
    EVENT("이벤트"), PROMOTION("소식");

    private final String value;

    WritingType(String value){
        this.value = value;
    }
    public String getValue(){ return value; }
}
