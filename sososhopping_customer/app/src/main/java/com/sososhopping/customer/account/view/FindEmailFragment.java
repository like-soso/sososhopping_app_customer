package com.sososhopping.customer.account.view;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.R;
import com.sososhopping.customer.account.dto.FindEmailDto;
import com.sososhopping.customer.account.dto.FindInfoDto;
import com.sososhopping.customer.account.viewmodel.FindEmailViewModel;
import com.sososhopping.customer.common.textValidate.NameWatcher;
import com.sososhopping.customer.common.textValidate.PhoneWatcher;
import com.sososhopping.customer.databinding.AccountFindEmailBinding;


public class FindEmailFragment extends Fragment {

    private NavController navController;
    private AccountFindEmailBinding binding;
    private Boolean phoneChecked = false;
    private final FindEmailViewModel findEmailViewModel = new FindEmailViewModel();

    public static FindEmailFragment newInstance() {
        return new FindEmailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.account_find_email, container, false);

        binding.editTextFindEmailName
                .addTextChangedListener(new NameWatcher(binding.textFieldFindEmailName, getResources().getString(R.string.signup_name)));
        binding.textFieldFindEmailPhone.setCounterMaxLength(11);
        binding.editTextFindEmailPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        binding.editTextFindEmailPhone
                .addTextChangedListener(new PhoneWatcher(binding.textFieldFindEmailPhone,getResources().getString(R.string.signup_error_phone)));


        binding.textViewPhoneCheck.setVisibility(View.INVISIBLE);

        binding.buttonPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.textFieldFindEmailPhone.getError() != null) {
                    return;
                }

                //번호인증하기
                phoneChecked = true;

                //번호인증실패시
                if(!phoneChecked){
                    binding.textViewPhoneCheck.setText("인증실패");
                }
                else{
                    binding.editTextFindEmailPhone.setEnabled(false);
                    binding.textViewPhoneCheck.setText("인증완료");
                }
                binding.textViewPhoneCheck.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonInfoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLayoutEmpty();

                //입력 검증
                if(!phoneChecked ||
                binding.textFieldFindEmailName.getError() != null ||
                binding.textFieldFindEmailPhone.getError() != null){
                    Snackbar.make(binding.getRoot(),getResources().getString(R.string.signup_error_process), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                try {
                    //성함 + 휴대전화번호 -> 이메일 API
                    findEmailViewModel.requestEmail(
                            new FindEmailDto((binding.editTextFindEmailName.getText().toString()),
                                    binding.editTextFindEmailPhone.getText().toString()),
                            FindEmailFragment.this::onSuccess,
                            FindEmailFragment.this::onNotFound,
                            FindEmailFragment.this::onNetworkError
                    );
                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(binding.getRoot(), getResources().getString(R.string.input_error), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.buttonToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_findEmailFragment_to_signUpStartFragment);
            }
        });
    }

    public void checkLayoutEmpty(){
        if(TextUtils.isEmpty(binding.editTextFindEmailName.getText().toString())){
            binding.textFieldFindEmailName.setError(getResources().getString(R.string.signup_error_name));
            binding.textFieldFindEmailName.setErrorEnabled(true);
        }
        if(TextUtils.isEmpty(binding.editTextFindEmailPhone.getText().toString())){
            binding.textFieldFindEmailPhone.setError(getResources().getString(R.string.signup_error_phone));
            binding.textFieldFindEmailPhone.setErrorEnabled(true);
        }
    }

    public void onSuccess(String email){
        if(binding != null){
            binding.editTextFindEmail.setText(email);
            binding.textFieldFindEmailName.setEnabled(false);
            binding.buttonInfoCheck.setClickable(false);
            binding.editTextFindEmail.setFocusable(false);
        }
    }

    public void onNotFound(){
        if(binding != null){
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.find_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        getActivity().onBackPressed();
        navController.navigate(R.id.action_global_networkErrorDialog);
    }

}