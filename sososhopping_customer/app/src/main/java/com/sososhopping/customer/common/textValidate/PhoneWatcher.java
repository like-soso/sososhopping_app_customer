package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class PhoneWatcher implements TextWatcher {

    private final TextInputLayout phoneTextLayout;
    private final String errorMsg;

    public PhoneWatcher(TextInputLayout phoneTextLayout, String errorMsg) {
        this.phoneTextLayout = phoneTextLayout;
        this.errorMsg = errorMsg;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(validatePhone(s.toString())){
            phoneTextLayout.setError(null);
            phoneTextLayout.setErrorEnabled(false);
        }
        else{
            phoneTextLayout.setError(errorMsg);
            phoneTextLayout.setErrorEnabled(true);
        }
    }

    public boolean validatePhone(String s){
        String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        return s.matches(regex);
    }
}
