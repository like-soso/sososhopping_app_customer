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
import com.sososhopping.customer.account.dto.FindInfoDto;
import com.sososhopping.customer.account.viewmodel.FindPasswordViewModel;
import com.sososhopping.customer.common.textValidate.EmailWatcher;
import com.sososhopping.customer.common.textValidate.NameWatcher;
import com.sososhopping.customer.common.textValidate.PasswordDupWatcher;
import com.sososhopping.customer.common.textValidate.PasswordWatcher;
import com.sososhopping.customer.common.textValidate.PhoneWatcher;
import com.sososhopping.customer.databinding.AccountFindPasswordBinding;


public class FindPasswordFragment extends Fragment {
    private NavController navController;
    private AccountFindPasswordBinding binding;
    private Boolean phoneChecked = false;
    private final FindPasswordViewModel findPasswordViewModel = new FindPasswordViewModel();
    public static FindPasswordFragment newInstance() {
        return new FindPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.account_find_password, container, false);

        binding.editTextFindPassEmail
                .addTextChangedListener(new EmailWatcher(binding.textFieldFindPassEmail, getResources().getString(R.string.signup_error_email)));
        binding.editTextFindPassName
                .addTextChangedListener(new NameWatcher(binding.textFieldFindPassName, getResources().getString(R.string.signup_error_name)));
        binding.textFieldFindPassPhone.setCounterMaxLength(11);
        binding.editTextFindPassPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        binding.editTextFindPassPhone
                .addTextChangedListener(new PhoneWatcher(binding.textFieldFindPassPhone,getResources().getString(R.string.signup_error_phone)));

        binding.textFieldFindPassPassword.getEditText()
                .addTextChangedListener(new PasswordWatcher(binding.textFieldFindPassPassword, getResources().getString(R.string.signup_error_password)));
        binding.textFieldFindPassPasswordDup.getEditText()
                .addTextChangedListener(new PasswordDupWatcher(binding.textFieldFindPassPassword, binding.textFieldFindPassPasswordDup, getResources().getString(R.string.signup_error_passwordDup)));

        binding.textViewPhoneCheck.setVisibility(View.INVISIBLE);
        binding.textViewInfoCheck.setVisibility(View.INVISIBLE);
        binding.buttonPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.textFieldFindPassPhone.getError() != null) {
                    return;
                }
                //번호인증하기
                phoneChecked = true;

                //번호인증실패시
                if(!phoneChecked){
                    binding.textViewPhoneCheck.setText("인증실패");
                }
                else{
                    binding.editTextFindPassPhone.setEnabled(false);
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
                        binding.textFieldFindPassEmail.getError() != null ||
                        binding.textFieldFindPassName.getError() != null ||
                        binding.textFieldFindPassPhone.getError() != null){
                    Toast.makeText(getContext(),getResources().getString(R.string.signup_error_process), Toast.LENGTH_SHORT).show();
                    return;
                }

                //이메일 + 성함 + 휴대전화번호 -> 가입여부 (비밀번호 재설정)
                try {
                    findPasswordViewModel.requestPassword(
                            new FindInfoDto(
                                    binding.editTextFindPassName.getText().toString(),
                                    binding.editTextFindPassPhone.getText().toString(),
                                    binding.editTextFindPassEmail.getText().toString()
                            ),
                            FindPasswordFragment.this::onSuccessFound,
                            FindPasswordFragment.this::onNotFound,
                            FindPasswordFragment.this::onNetworkError
                    );
                    binding.textViewInfoCheck.setVisibility(View.VISIBLE);
                }catch (Exception e){
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
                checkLayoutPassEmpty();

                if(binding.textFieldFindPassPassword.getError() != null ||
                binding.textFieldFindPassPasswordDup.getError() != null){
                    return;
                }

                //API로 변경된 이메일 + 비밀번호 전송 -> 변경되는지 확인
                try {
                    findPasswordViewModel.changePassword(
                            findPasswordViewModel.getChangePasswordDto(binding.editTextFindPassPassword.getText().toString()),
                            FindPasswordFragment.this::onSuccessChange,
                            FindPasswordFragment.this::onFailedChange,
                            FindPasswordFragment.this::onNetworkError);

                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(binding.getRoot(), getResources().getString(R.string.input_error), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void checkLayoutEmpty(){
        if(TextUtils.isEmpty(binding.editTextFindPassEmail.getText().toString())){
            binding.textFieldFindPassEmail.setError(getResources().getString(R.string.signup_error_email));
        }
        if(TextUtils.isEmpty(binding.editTextFindPassName.getText().toString())){
            binding.textFieldFindPassName.setError(getResources().getString(R.string.signup_error_name));
        }
        if(TextUtils.isEmpty(binding.editTextFindPassPhone.getText().toString())){
            binding.textFieldFindPassPhone.setError(getResources().getString(R.string.signup_error_phone));
        }
    }

    public void checkLayoutPassEmpty(){
        if(TextUtils.isEmpty(binding.editTextFindPassPassword.getText().toString())){
            binding.textFieldFindPassPassword.setError(getResources().getString(R.string.signup_error_phone));
        }
        if(TextUtils.isEmpty(binding.editTextFindPassPasswordDup.getText().toString())){
            binding.textFieldFindPassPasswordDup.setError(getResources().getString(R.string.signup_error_nickname));
        }
    }

    public void onSuccessFound(){
        if(binding != null){
            binding.editTextFindPassName.setEnabled(false);
            binding.editTextFindPassEmail.setEnabled(false);
            binding.buttonInfoCheck.setEnabled(false);

            binding.textFieldFindPassPassword.setEnabled(true);
            binding.textFieldFindPassPasswordDup.setEnabled(true);
            binding.buttonToMain.setEnabled(true);
            binding.textViewInfoCheck.setText(getResources().getString(R.string.find_infoOk));
        }
    }

    public void onNotFound(){
        if(binding != null){
            binding.textViewInfoCheck.setText(getResources().getString(R.string.find_error));
        }
    }

    public void onSuccessChange(){
        if(binding != null){
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.find_change_success), Snackbar.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
    }


    public void onFailedChange(){
        if(binding != null){
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.find_change_failed), Snackbar.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
    }

    private void onNetworkError() {
        getActivity().onBackPressed();
        navController.navigate(R.id.action_global_networkErrorDialog);
    }
}