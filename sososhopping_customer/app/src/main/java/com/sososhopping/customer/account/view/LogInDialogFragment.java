package com.sososhopping.customer.account.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.account.dto.LogInRequestDto;
import com.sososhopping.customer.account.dto.LogInResponseDto;
import com.sososhopping.customer.account.viewmodel.LogInViewModel;
import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.sharedpreferences.SharedPreferenceManager;
import com.sososhopping.customer.databinding.AccountLogInDialogBinding;

public class LogInDialogFragment extends DialogFragment {

    private NavController navController;
    private AccountLogInDialogBinding binding;
    private LogInViewModel logInViewModel;

    private String email;
    private String password;

    public static LogInDialogFragment newInstance() {
        return new LogInDialogFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = AccountLogInDialogBinding.inflate(inflater, container, false);
        logInViewModel = new LogInViewModel();

        binding.editTextLogInEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                binding.textFieldLogInEmail.setError(null);
                binding.textFieldLogInEmail.setErrorEnabled(false);
            }
        });

        binding.editTextLogInPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                binding.textFieldLogInPassword.setError(null);
                binding.textFieldLogInPassword.setErrorEnabled(false);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        navController = Navigation.findNavController(getParentFragment().getView());

        //아이디찾기
        binding.buttonFindEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_logInDialogFragment_to_findEmailFragment);
            }
        });

        //비밀번호찾기
        binding.buttonFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_logInDialogFragment_to_findPasswordFragment);
            }
        });

        //로그인
        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLayoutEmpty();

                if(binding.textFieldLogInEmail.getError() != null ||
                        binding.textFieldLogInEmail.getError() != null){
                    Snackbar.make(binding.getRoot(),getResources().getString(R.string.signup_error_process), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //로그인 프로세스
                email = binding.editTextLogInEmail.getText().toString().trim();
                password = binding.editTextLogInPassword.getText().toString().trim();

                logInViewModel.requestLogin(email, password,
                        LogInDialogFragment.this::onLoggedIn,
                        LogInDialogFragment.this::onLoginFailed,
                        LogInDialogFragment.this::onNetworkError);

                //for test
                //onLoggedIn(new LogInRequestDto(email, password), new LogInResponseDto(email));
            }
        });
    }

    public void checkLayoutEmpty(){
        if(TextUtils.isEmpty(binding.editTextLogInEmail.getText().toString())){
            binding.textFieldLogInEmail.setError(getResources().getString(R.string.login_email));
        }
        if(TextUtils.isEmpty(binding.editTextLogInPassword.getText().toString())){
            binding.textFieldLogInPassword.setError(getResources().getString(R.string.login_password));
        }
    }

    private void onLoginFailed() {
        if(binding != null){
            Snackbar.make(binding.getRoot(),getResources().getString(R.string.login_failed),Snackbar.LENGTH_SHORT).show();
        }

    }

    private void onLoggedIn(LogInRequestDto requestDto, LogInResponseDto responseDto) {
        String id = requestDto.getEmail();
        String password = requestDto.getPassword();
        String token = responseDto.getToken();

        SharedPreferenceManager.setString(getContext(), Constant.SHARED_PREFERENCE_KEY_ID, id);
        SharedPreferenceManager.setString(getContext(), Constant.SHARED_PREFERENCE_KEY_PASSWORD, password);
        ((HomeActivity) getActivity()).setLoginToken(token);
        ((HomeActivity) getActivity()).afterLoginSuccessFirebaseInit(responseDto.getFirebaseToken());

        //로그인 처리 후 홈화면 이동
        ((HomeActivity) getActivity()).setIsLogIn(true);
        ((HomeActivity) getActivity()).initLoginButton();
        navController.navigate(R.id.action_global_home2);
    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }
}