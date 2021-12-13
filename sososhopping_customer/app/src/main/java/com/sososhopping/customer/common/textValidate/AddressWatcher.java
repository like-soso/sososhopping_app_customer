package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class AddressWatcher  implements TextWatcher {

    private final TextInputLayout emailTextLayout;
    private TextView dupCheckTextView;
    private final String errorMsg;


    public AddressWatcher(TextInputLayout textInputLayout, String errorMsg){
        this.emailTextLayout = textInputLayout;
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
        emailTextLayout.setError(null);
        emailTextLayout.setErrorEnabled(false);
    }
}

