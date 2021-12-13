package com.sososhopping.customer.shop.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ShopReportDialogBinding;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;

import org.jetbrains.annotations.Nullable;

public class ShopReportDialogFragment extends DialogFragment {

    private ShopReportDialogBinding binding;
    private ShopInfoViewModel shopInfoViewModel;

    int storeId;
    public static ShopReportDialogFragment newInstance() {return new ShopReportDialogFragment();}

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = ShopReportDialogBinding.inflate(inflater,container,false);

        //viewmodel 설정
        shopInfoViewModel = new ShopInfoViewModel();
        storeId = ShopReportDialogFragmentArgs.fromBundle(getArguments()).getStoreId();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //신고 전송
        binding.buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.editTextReportContent.getText().length() < 29){
                    Toast.makeText(getContext(),getResources().getString(R.string.report_warning), Toast.LENGTH_SHORT).show();
                    return;
                }

                shopInfoViewModel.inputReport(
                        ((HomeActivity)getActivity()).getLoginToken(),
                        storeId,
                        binding.editTextReportContent.getText().toString(),
                        ShopReportDialogFragment.this::onSuccess,
                        ShopReportDialogFragment.this::onFailedLogIn,
                        ShopReportDialogFragment.this::onFailed,
                        ShopReportDialogFragment.this::onNetworkError
                );
            }
        });
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.report_title));

        //하단바
        ((HomeActivity)getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onSuccess(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.report_input), Snackbar.LENGTH_SHORT).show();
        //종료
        dismiss();
    }

    private void onFailedLogIn(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
            getResources().getString(R.string.login_error_token), Snackbar.LENGTH_SHORT).show();
        ((HomeActivity)getActivity()).setLoginToken(null);
        //종료
        dismiss();
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
            getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkError() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        //종료
        dismiss();
    }

}
