package com.sososhopping.customer.common.types.enumType;

import com.sososhopping.customer.R;

public enum CategoryType {
    GROCERY("마트", R.drawable.icon_category_mart),
    MEAT("정육점", R.drawable.icon_category_meat),
    FISH("수산물", R.drawable.icon_category_seafood),
    BAKERY("베이커리", R.drawable.icon_category_bakery),
    CAFE("카페", R.drawable.icon_category_cafe),
    HAIRSHOP("헤어숍", R.drawable.icon_category_beauty_salon),
    LAUNDRY("세탁소", R.drawable.icon_category_laundry),
    BOOK("서점", R.drawable.icon_category_bookstore),
    OFFICE("사무용품", R.drawable.icon_category_office_tool),
    ACADEMY("학원", R.drawable.icon_category_academy),
    PHOTO("사진관", R.drawable.icon_category_camera),
    FLOWER("꽃집", R.drawable.icon_category_flowers),
    CLOTH("의류", R.drawable.icon_category_dress_shop),
    LOCALPAY("지역화폐", R.drawable.icon_local_pay),
    MAP("지도", R.drawable.icon_app_map);

    private final String value;
    private final int icon;

    CategoryType(String value, int icon){
        this.value = value;
        this.icon = icon;
    }

    public String getValue(){ return value; }
    public int getIconId(){return icon;}
}
