package com.sososhopping.customer.purchase.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartUpdateDto {
    @SerializedName("itemId")
    Integer itemId;
    @SerializedName("quantity")
    int quantity;

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        CartUpdateDto a = (CartUpdateDto) obj;
        return a.itemId == this.itemId;
    }

    @Override
    public int hashCode() {
        return itemId.hashCode();
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return itemId + " " + quantity;
    }

}
