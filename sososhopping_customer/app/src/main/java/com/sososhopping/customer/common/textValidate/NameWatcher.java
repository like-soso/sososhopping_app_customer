package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class NameWatcher implements TextWatcher {

    private final TextInputLayout nameTextLayout;
    private final String errorMsg;

    public NameWatcher(TextInputLayout nameTextLayout, String errorMsg) {
        this.nameTextLayout = nameTextLayout;
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
        if(validateEmail(s.toString()) && s.length() >= 2){
            nameTextLayout.setError(null);
            nameTextLayout.setErrorEnabled(false);
        }
        else{
            nameTextLayout.setError(errorMsg);
            nameTextLayout.setErrorEnabled(true);
        }
    }

    public boolean validateEmail(String s){
        String regex = "^[가-힣]*$";
        return s.matches(regex);
    }
}
