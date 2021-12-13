package com.sososhopping.customer.common.types.enumType;

public enum PaymentType {
    CASH("현금"), CARD("신용/체크카드"), LOCAL("지역 화폐"), KAKAO("카카오페이"), NAVER("네이버페이"), PHONE("핸드폰결제"), TOSS("토스");

    private final String value;
    PaymentType(String value){
        this.value = value;
    }
    public String getValue(){ return value; }
}
