package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class EmailWatcher implements TextWatcher {

    private final TextInputLayout emailTextLayout;
    private TextView dupCheckTextView;
    private final String errorMsg;

    public EmailWatcher(TextInputLayout textInputLayout, TextView textView, String errorMsg){
        this.emailTextLayout = textInputLayout;
        this.dupCheckTextView = textView;
        this.errorMsg = errorMsg;
    }

    public EmailWatcher(TextInputLayout textInputLayout, String errorMsg){
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
        if(!validateEmail(s.toString())){
            emailTextLayout.setError(errorMsg);
            emailTextLayout.setErrorEnabled(true);
        }
        else{
            emailTextLayout.setError(null);
            emailTextLayout.setErrorEnabled(false);
        }
        if(dupCheckTextView != null){
            dupCheckTextView.setVisibility(View.INVISIBLE);
        }
    }

    public boolean validateEmail(String s){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return s.matches(emailPattern);
    }
}
