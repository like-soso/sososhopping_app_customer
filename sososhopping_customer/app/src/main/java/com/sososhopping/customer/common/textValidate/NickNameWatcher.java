package com.sososhopping.customer.common.textValidate;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class NickNameWatcher implements TextWatcher {

    private final TextInputLayout nickNameTextLayout;
    private final TextView dupCheckTextView;
    private final String errorMsg;

    public NickNameWatcher(TextInputLayout nickNameTextLayout,  TextView textView, String errorMsg) {
        this.nickNameTextLayout = nickNameTextLayout;
        this.dupCheckTextView = textView;
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
        if(validateNickname(s.toString())){
            nickNameTextLayout.setError(null);
            nickNameTextLayout.setErrorEnabled(false);
        }
        else{
            nickNameTextLayout.setError(errorMsg);
            nickNameTextLayout.setErrorEnabled(true);
        }
        dupCheckTextView.setVisibility(View.INVISIBLE);
    }

    public boolean validateNickname(String s){
        String regex = "^[0-9a-zA-Z가-힣]*$";
        return s.matches(regex);
    }
}
