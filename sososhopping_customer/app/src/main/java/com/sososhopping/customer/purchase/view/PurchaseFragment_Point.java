package com.sososhopping.customer.purchase.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.R;
import com.sososhopping.customer.purchase.viewmodel.PurchaseViewModel;
import com.sososhopping.customer.databinding.PurchaseMainBinding;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PurchaseFragment_Point {

    PurchaseMainBinding binding;
    PurchaseViewModel purchaseViewModel;
    Context context;

    protected void setPointLayout(Resources rs, NavController navController){

        binding.includeLayoutPoint.linearLayoutCouponInfo.setVisibility(View.GONE);

        binding.includeLayoutPoint.buttonCouponUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(PurchaseFragmentDirections
                        .actionPurchaseFragmentToPurchaseCouponDialogFragment(purchaseViewModel.getShopInfo().getValue().getStoreId(),
                                purchaseViewModel.getTotalPrice().getValue()));
            }
        });

        binding.includeLayoutPoint.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseViewModel.getUseCoupon().setValue(null);
            }
        });

        binding.includeLayoutPoint.buttonUseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int myPoint = purchaseViewModel.getMaxPoint().getValue();
                purchaseViewModel.getUsePoint().setValue(myPoint);
                binding.includeLayoutPoint.editTextUsePoint.setText(String.valueOf(myPoint));
            }
        });

        binding.includeLayoutPoint.editTextUsePoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.length() > 0){
                    int usePoint = Integer.parseInt(editable.toString());
                    if(usePoint > purchaseViewModel.getMaxPoint().getValue()){
                        binding.includeLayoutPoint.textFieldUsePoint.setError(rs.getString(R.string.point_use_err));
                        binding.includeLayoutPoint.textFieldUsePoint.setErrorEnabled(true);
                    }else{
                        binding.includeLayoutPoint.textFieldUsePoint.setError(null);
                        binding.includeLayoutPoint.textFieldUsePoint.setErrorEnabled(false);
                    }
                    purchaseViewModel.getUsePoint().setValue(usePoint);
                }
            }
        });

        binding.includeLayoutPoint.textFieldUsePoint.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupWindow pop = new PopupWindow(context);
                LinearLayout layout = new LinearLayout(context);

                TextView textView = new TextView(context);
                textView.setText(rs.getString(R.string.point_info));
                textView.setTextColor(rs.getColor(R.color.white));
                textView.setGravity(Gravity.LEFT);

                ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addView(textView, params);
                layout.setPadding(20,10,10,20);

                layout.setBackground(rs.getDrawable(R.drawable.drawable_background_toast));
                pop.setContentView(layout);


                // Closes the popup window when touch outside.
                pop.setOutsideTouchable(true);
                pop.setFocusable(true);
                pop.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                pop.showAsDropDown(binding.includeLayoutPoint.textFieldUsePoint,
                        (int)0.2*binding.includeLayoutPoint.textFieldUsePoint.getWidth(),
                        -2*binding.includeLayoutPoint.textFieldUsePoint.getHeight(),Gravity.CENTER);
            }
        });
    }
}
