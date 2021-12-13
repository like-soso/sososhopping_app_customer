package com.sososhopping.customer.shop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.sososhopping.customer.common.types.enumType.CouponType;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponModel implements Parcelable {
    long couponId;
    String storeName;
    String couponName;
    String couponCode;
    int minimumOrderPrice;
    String startDate;
    String endDate;
    String expiryDate;
    CouponType couponType;
    int fixAmount;
    Double rateAmount;

    protected CouponModel(Parcel in) {
        couponId = in.readLong();
        storeName = in.readString();
        couponName = in.readString();
        couponCode = in.readString();
        minimumOrderPrice = in.readInt();
        startDate = in.readString();
        endDate = in.readString();
        expiryDate = in.readString();
        fixAmount = in.readInt();
        if (in.readByte() == 0) {
            rateAmount = null;
        } else {
            rateAmount = in.readDouble();
        }
    }

    public static final Creator<CouponModel> CREATOR = new Creator<CouponModel>() {
        @Override
        public CouponModel createFromParcel(Parcel in) {
            return new CouponModel(in);
        }

        @Override
        public CouponModel[] newArray(int size) {
            return new CouponModel[size];
        }
    };

    public String amount(){
        if(couponType == CouponType.FIX){
            return fixAmount+"원";
        }
        else if(couponType == CouponType.RATE){
            return rateAmount+"%";
        }
        return null;
    }

    public CouponModel(String storeName){
        this.storeName = storeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(couponId);
        parcel.writeString(storeName);
        parcel.writeString(couponName);
        parcel.writeString(couponCode);
        parcel.writeInt(minimumOrderPrice);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeString(expiryDate);
        parcel.writeInt(fixAmount);
        if (rateAmount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(rateAmount);
        }
    }
}
