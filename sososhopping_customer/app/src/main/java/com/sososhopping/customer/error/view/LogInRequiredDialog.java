package com.sososhopping.customer.error.view;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ErrorAskLogInDialogBinding;

public class LogInRequiredDialog extends DialogFragment {
    ErrorAskLogInDialogBinding binding;


    public LogInRequiredDialog() {

    }

    public static LogInRequiredDialog newInstance() {
        return new LogInRequiredDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.error_ask_log_in_dialog, container, false);
        //다른 메세지 있으면 그걸로
        int errorDescription = LogInRequiredDialogArgs.fromBundle(getArguments()).getErrorMsgId();
        if (errorDescription != -1) {
            binding.description.setText(getResources().getString(errorDescription));
        }
        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //한번에 이동하는법?
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.action_global_navigation_login);
                closeDialog();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void closeDialog() {
        this.dismiss();
    }
}
