package com.sososhopping.customer.purchase.dto;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto implements Parcelable {
    int itemId;
    String itemName;
    String description;
    String imgUrl;
    int price;
    Boolean saleStatus;
    int num;
    boolean purchase;

    protected CartItemDto(Parcel in) {
        itemId = in.readInt();
        itemName = in.readString();
        description = in.readString();
        imgUrl = in.readString();
        price = in.readInt();
        byte tmpSaleStatus = in.readByte();
        saleStatus = tmpSaleStatus == 0 ? null : tmpSaleStatus == 1;
        num = in.readInt();
        purchase = in.readByte() != 0;
    }

    public static final Creator<CartItemDto> CREATOR = new Creator<CartItemDto>() {
        @Override
        public CartItemDto createFromParcel(Parcel in) {
            return new CartItemDto(in);
        }

        @Override
        public CartItemDto[] newArray(int size) {
            return new CartItemDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(itemId);
        parcel.writeString(itemName);
        parcel.writeString(description);
        parcel.writeString(imgUrl);
        parcel.writeInt(price);
        parcel.writeByte((byte) (saleStatus == null ? 0 : saleStatus ? 1 : 2));
        parcel.writeInt(num);
        parcel.writeByte((byte) (purchase ? 1 : 0));
    }
}
