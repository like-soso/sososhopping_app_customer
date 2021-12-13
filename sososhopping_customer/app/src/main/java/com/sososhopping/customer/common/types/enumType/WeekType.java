package com.sososhopping.customer.common.types.enumType;

import com.google.gson.annotations.SerializedName;

public enum WeekType {

    @SerializedName("월")
    Monday("월", 2),
    @SerializedName("화")
    Tuesday("화", 3),
    @SerializedName("수")
    Wednesday("수", 4),
    @SerializedName("목")
    Thursday("목", 5),
    @SerializedName("금")
    Friday("금", 6),
    @SerializedName("토")
    Saturday("토", 7),
    @SerializedName("일")
    Sunday("일", 1);

    private final String value;
    private final int intValue;

    WeekType(String value, int intValue){
        this.value = value;
        this.intValue = intValue;
    }

    public String getValue(){ return value; }

    public int getIntValue(){return intValue;}
}
