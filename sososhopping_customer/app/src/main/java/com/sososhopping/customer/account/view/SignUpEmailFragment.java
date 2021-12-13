package com.sososhopping.customer.account.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.R;
import com.sososhopping.customer.account.viewmodel.SignUpViewModel;
import com.sososhopping.customer.common.textValidate.EmailWatcher;
import com.sososhopping.customer.common.textValidate.PasswordDupWatcher;
import com.sososhopping.customer.common.textValidate.PasswordWatcher;
import com.sososhopping.customer.databinding.AccountSignUpEmailBinding;

public class SignUpEmailFragment extends Fragment {

    private NavController navController;
    private SignUpViewModel signUpViewModel;

    private AccountSignUpEmailBinding binding;
    private Boolean dupChecked = false;

    public static SignUpEmailFragment newInstance() {
        return new SignUpEmailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.account_sign_up_email, container, false);
        binding.textViewDupChecked.setVisibility(View.INVISIBLE);

        binding.textFieldSignUpEmail.getEditText()
                .addTextChangedListener(new EmailWatcher(binding.textFieldSignUpEmail,
                        binding.textViewDupChecked, getResources().getString(R.string.signup_error_email)));
        binding.textFieldSignUpPassword.getEditText()
                .addTextChangedListener(new PasswordWatcher(binding.textFieldSignUpPassword, getResources().getString(R.string.signup_error_password)));
        binding.textFieldSignUpPasswordDup.getEditText()
                .addTextChangedListener(new PasswordDupWatcher(binding.textFieldSignUpPassword, binding.textFieldSignUpPasswordDup, getResources().getString(R.string.signup_error_passwordDup)));

        binding.buttonDupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(binding.textFieldSignUpEmail.getEditText().getText().toString())){
                    binding.textFieldSignUpEmail.setError(getResources().getString(R.string.signup_error_email));
                }

                //에러시 종료
                if(binding.textFieldSignUpEmail.getError() != null) return;

                //중복확인 요청
                signUpViewModel.requestEmailDupCheck(
                        binding.textFieldSignUpEmail.getEditText().getText().toString(),
                        SignUpEmailFragment.this::onEmailNotDuplicated,
                        SignUpEmailFragment.this::onEmailDuplicated,
                        SignUpEmailFragment.this::onNetworkError);

                //for test
                //onEmailNotDuplicated();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signUpViewModel = new ViewModelProvider(requireParentFragment()).get(SignUpViewModel.class);
        navController = Navigation.findNavController(view);

        //다음 화면으로
        binding.buttonNextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLayoutEmpty();
                //변경되었었으면 false로
                if(binding.textViewDupChecked.getVisibility() == View.INVISIBLE){
                    dupChecked = false;
                }

                if(!dupChecked ||
                        binding.textFieldSignUpEmail.getError() != null ||
                        binding.textFieldSignUpPassword.getError() != null ||
                        binding.textFieldSignUpPasswordDup.getError() != null){
                    Snackbar.make(binding.getRoot(),getResources().getString(R.string.signup_error_process),Snackbar.LENGTH_SHORT).show();
                    return;
                }

                signUpViewModel.setEmail(binding.textFieldSignUpEmail.getEditText().getText().toString());
                signUpViewModel.setPassword(binding.textFieldSignUpPassword.getEditText().getText().toString());

                //다음 fragment로 이동
                navController.navigate(R.id.action_signUpIdFragment_to_signUpInfoFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void checkLayoutEmpty(){
        if(TextUtils.isEmpty(binding.textFieldSignUpEmail.getEditText().getText().toString())){
            binding.textFieldSignUpEmail.setError(getResources().getString(R.string.signup_error_name));
        }
        if(TextUtils.isEmpty(binding.textFieldSignUpPassword.getEditText().getText().toString())){
            binding.textFieldSignUpPassword.setError(getResources().getString(R.string.signup_error_phone));
        }
        if(TextUtils.isEmpty(binding.textFieldSignUpPasswordDup.getEditText().getText().toString())){
            binding.textFieldSignUpPasswordDup.setError(getResources().getString(R.string.signup_error_passwordDup));
        }
    }

    private void onEmailDuplicated() {
        if(binding != null){
            dupChecked = false;
            signUpViewModel.getEmailDupChecked().setValue(dupChecked);
            binding.textViewDupChecked.setText("사용이 불가능합니다.");
            binding.textViewDupChecked.setVisibility(View.VISIBLE);
        }
    }

    private void onEmailNotDuplicated(){
        if(binding != null){
            dupChecked = true;
            signUpViewModel.getEmailDupChecked().setValue(dupChecked);
            binding.textViewDupChecked.setText("사용 가능");
            binding.textViewDupChecked.setVisibility(View.VISIBLE);
        }
    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }
}