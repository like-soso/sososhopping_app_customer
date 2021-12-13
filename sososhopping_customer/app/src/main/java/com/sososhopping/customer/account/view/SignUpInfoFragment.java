package com.sososhopping.customer.account.view;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.account.viewmodel.SignUpViewModel;
import com.sososhopping.customer.common.NetworkStatus;
import com.sososhopping.customer.common.textValidate.AddressWatcher;
import com.sososhopping.customer.common.textValidate.NameWatcher;
import com.sososhopping.customer.common.textValidate.NickNameWatcher;
import com.sososhopping.customer.common.textValidate.PhoneWatcher;
import com.sososhopping.customer.databinding.AccountSignUpInfoBinding;


public class SignUpInfoFragment extends Fragment {

    private NavController navController;
    private SignUpViewModel signUpViewModel;
    private AccountSignUpInfoBinding binding;

    private Boolean dupChecked = false;
    private Boolean phoneChecked = false;

    public static SignUpInfoFragment newInstance() {
        return new SignUpInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.account_sign_up_info, container, false);
        binding.textViewDupChecked.setVisibility(View.INVISIBLE);
        binding.textViewPhoneCheck.setVisibility(View.INVISIBLE);

        //도로명 검색 API와 연결해야 함
        binding.textFieldSignUpName.getEditText().addTextChangedListener(new NameWatcher(binding.textFieldSignUpName, getResources().getString(R.string.signup_error_name)));

        binding.textFieldSignUpPhone.setCounterMaxLength(11);
        binding.textFieldSignUpPhone.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        /*binding.textFieldSignUpPhone.getEditText()
                .addTextChangedListener(new PhoneNumberFormattingTextWatcher());*/
        binding.textFieldSignUpPhone.getEditText()
                .addTextChangedListener(new PhoneWatcher(binding.textFieldSignUpPhone,getResources().getString(R.string.signup_error_phone)));

        binding.textFieldSignUpNickname.setCounterMaxLength(8);
        binding.textFieldSignUpNickname.getEditText()
                .addTextChangedListener(new NickNameWatcher(binding.textFieldSignUpNickname,
                        binding.textViewDupChecked, getResources().getString(R.string.signup_error_nickname)));

        //주소 검증
        binding.textFieldSignUpRoadAddress.getEditText().addTextChangedListener(new AddressWatcher(binding.textFieldSignUpRoadAddress,
                getResources().getString(R.string.signup_error_roadAddress)));
        binding.textFieldSignUpDetailAddress.getEditText().addTextChangedListener(new AddressWatcher(binding.textFieldSignUpDetailAddress,
                getResources().getString(R.string.signup_error_detailAddress)));

        binding.textViewDupChecked.setVisibility(View.INVISIBLE);
        binding.textViewPhoneCheck.setVisibility(View.INVISIBLE);

        binding.buttonPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(binding.editTextSignUpPhone.getText().toString())){
                    binding.textFieldSignUpPhone.setError(getResources().getString(R.string.signup_error_phone));
                }

                if(binding.editTextSignUpPhone.getError() != null) return;

                //번호중복여부
                signUpViewModel.requestPhoneDupCheck(
                        binding.editTextSignUpPhone.getText().toString(),
                        SignUpInfoFragment.this::onPhoneNotDuplicated,
                        SignUpInfoFragment.this::onPhoneDuplicated,
                        SignUpInfoFragment.this::onNetworkError
                );
            }
        });

        binding.buttonDupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(binding.editTextSignUpNickname.getText().toString())){
                    binding.textFieldSignUpNickname.setError(getResources().getString(R.string.signup_error_nickname));
                }

                if(binding.editTextSignUpNickname.getError() != null) return;

                //중복확인 요청 API
                signUpViewModel.requestNicknameDupCheck(
                        binding.editTextSignUpNickname.getText().toString(),
                        SignUpInfoFragment.this::onNicknameNotDuplicated,
                        SignUpInfoFragment.this::onNicknameDuplicated,
                        SignUpInfoFragment.this::onNetworkError);

                //for test
                //onNicknameNotDuplicated();
            }
        });

        binding.editTextSignUpRoadAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = NetworkStatus.getConnectivityStatus(getContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    navController.navigate(NavGraphDirections.actionGlobalRoadAddressSearchDialog());
                }else {
                    Snackbar.make(binding.getRoot(), "인터넷 연결을 확인해주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("roadAddress")
                .observe(this, new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        binding.editTextSignUpRoadAddress.setText((CharSequence) o);
                    }
                });

        signUpViewModel = new ViewModelProvider(requireParentFragment()).get(SignUpViewModel.class);

        //다음화면으로
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLayoutEmpty();

                //변경되었었으면 false로
                if(binding.textViewDupChecked.getVisibility() == View.INVISIBLE){
                    dupChecked = false;
                }

                if(!phoneChecked || !dupChecked ||
                        binding.textFieldSignUpName.getError() != null ||
                        binding.textFieldSignUpPhone.getError() != null ||
                        binding.textFieldSignUpNickname.getError() != null ||
                        binding.textFieldSignUpRoadAddress.getError() != null ||
                        binding.textFieldSignUpDetailAddress.getError() != null) {

                    Snackbar.make(binding.getRoot(),getResources().getString(R.string.signup_error_process), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                signUpViewModel.setName(binding.editTextSignUpName.getText().toString());
                signUpViewModel.setPhone(binding.editTextSignUpPhone.getText().toString());
                signUpViewModel.setNickName(binding.editTextSignUpNickname.getText().toString());
                signUpViewModel.setRoadAddress(binding.editTextSignUpRoadAddress.getText().toString());
                signUpViewModel.setDetailAddress(binding.editTextSignUpDetailAddress.getText().toString());

                //회원가입 절차 진행
                Log.d("회원가입 요청 : ", signUpViewModel.printVal());
                signUpViewModel.requestSignup(
                        SignUpInfoFragment.this::onSignupSuccess,
                        SignUpInfoFragment.this::onNetworkError
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void checkLayoutEmpty(){
        if(TextUtils.isEmpty(binding.editTextSignUpName.getText().toString())){
            binding.textFieldSignUpName.setError(getResources().getString(R.string.signup_error_name));
        }
        if(TextUtils.isEmpty(binding.editTextSignUpPhone.getText().toString())){
            binding.textFieldSignUpPhone.setError(getResources().getString(R.string.signup_error_phone));
        }
        if(TextUtils.isEmpty(binding.editTextSignUpNickname.getText().toString())){
            binding.textFieldSignUpNickname.setError(getResources().getString(R.string.signup_error_nickname));
        }
        if(TextUtils.isEmpty(binding.editTextSignUpRoadAddress.getText().toString())){
            binding.textFieldSignUpRoadAddress.setError(getResources().getString(R.string.signup_error_roadAddress));
        }
        if(TextUtils.isEmpty(binding.editTextSignUpDetailAddress.getText().toString())){
            binding.textFieldSignUpDetailAddress.setError(getResources().getString(R.string.signup_error_detailAddress));
        }
    }

    private void onNicknameDuplicated() {
        dupChecked = false;
        binding.textViewDupChecked.setText("사용이 불가능합니다.");
        binding.textViewDupChecked.setVisibility(View.VISIBLE);
    }

    private void onNicknameNotDuplicated(){
        dupChecked = true;
        binding.textViewDupChecked.setText("사용 가능");
        binding.textViewDupChecked.setVisibility(View.VISIBLE);
    }

    private void onPhoneDuplicated(){
        phoneChecked = false;
        binding.textViewPhoneCheck.setText("이미 가입한 번호입니다");
        binding.textViewPhoneCheck.setVisibility(View.VISIBLE);
    }
    private void onPhoneNotDuplicated(){
        phoneChecked = true;
        binding.editTextSignUpPhone.setEnabled(false);
        binding.textFieldSignUpPhone.setEnabled(false);
        binding.textViewPhoneCheck.setText("인증완료");
        binding.textViewPhoneCheck.setVisibility(View.VISIBLE);
    }

    private void onSignupSuccess() {
        Toast.makeText(getContext(),getResources().getString(R.string.signup_success),Toast.LENGTH_LONG).show();
        navController.navigate(R.id.action_signUpInfoFragment_to_signUpStartFragment);

    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }

}