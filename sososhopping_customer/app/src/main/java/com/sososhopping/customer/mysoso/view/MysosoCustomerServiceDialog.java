package com.sososhopping.customer.mysoso.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.MysosoCustomerserviceDialogBinding;

import org.jetbrains.annotations.Nullable;

public class MysosoCustomerServiceDialog extends DialogFragment {

    MysosoCustomerserviceDialogBinding binding;
    public static MysosoCustomerServiceDialog newInstance() {return new MysosoCustomerServiceDialog();}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = MysosoCustomerserviceDialogBinding.inflate(inflater, container, false);

        //viewmodel 설정

        //Controller 설정

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_round_background));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}
