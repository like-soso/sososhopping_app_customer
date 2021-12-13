package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class PasswordDupWatcher implements TextWatcher {

    private final TextInputLayout passwordDupTextLayout;
    private final TextInputLayout passwordTextLayout;
    private final String errorMsg;

    public PasswordDupWatcher(TextInputLayout passwordTextLayout, TextInputLayout passwordDupTextLayout, String errorMsg) {
        this.passwordTextLayout = passwordTextLayout;
        this.passwordDupTextLayout = passwordDupTextLayout;
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
        String password = passwordTextLayout.getEditText().getText().toString();

        if(!s.toString().equals(password)){
            passwordDupTextLayout.setError(errorMsg);
            passwordDupTextLayout.setErrorEnabled(true);
        }
        else{
            passwordDupTextLayout.setError(null);
            passwordDupTextLayout.setErrorEnabled(false);
        }
    }
}
