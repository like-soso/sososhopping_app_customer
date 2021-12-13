package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordWatcher implements TextWatcher {

    private final TextInputLayout passwordTextLayout;
    private final String errorMsg;

    public PasswordWatcher(TextInputLayout passwordTextLayout, String errorMsg) {
        this.passwordTextLayout = passwordTextLayout;
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
        if(s.toString().length() < 8){
            passwordTextLayout.setError(errorMsg);
            passwordTextLayout.setErrorEnabled(true);
        }else{
            passwordTextLayout.setError(null);
            passwordTextLayout.setErrorEnabled(false);
        }
    }
}
