package com.sososhopping.customer.mysoso.view;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.NetworkStatus;
import com.sososhopping.customer.common.sharedpreferences.SharedPreferenceManager;
import com.sososhopping.customer.common.textValidate.NameWatcher;
import com.sososhopping.customer.common.textValidate.NickNameWatcher;
import com.sososhopping.customer.common.textValidate.PasswordDupWatcher;
import com.sososhopping.customer.common.textValidate.PasswordWatcher;
import com.sososhopping.customer.common.textValidate.PhoneWatcher;
import com.sososhopping.customer.databinding.MysosoMyinfoBinding;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.mysoso.viemodel.MyInfoViewModel;

import org.jetbrains.annotations.Nullable;

public class MysosoMyInfoFragment extends Fragment {

    MysosoMyinfoBinding binding;
    MyInfoViewModel myInfoViewModel;
    NavController navController;

    //정보 수정이지 아닌지
    boolean state = false;
    private Boolean dupChecked = false;
    private Boolean phoneChecked = false;

    public static MysosoMyInfoFragment newInstance() {
        return new MysosoMyInfoFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //binding 설정
        binding = MysosoMyinfoBinding.inflate(inflater, container, false);

        //viewmodel 설정
        myInfoViewModel = new MyInfoViewModel();
        myInfoViewModel.requestMyInfo(((HomeActivity) getActivity()).getLoginToken(),
                this::onSuccess,
                this::onFailedLogIn,
                this::onFailed,
                this::onNetworkError);

        //이메일은 변경 불가
        initialSetting();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Controller 설정
        navController = Navigation.findNavController(view);
        navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("roadAddress")
                .observe(this, new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        binding.editTextRoadAddress.setText((CharSequence) o);
                    }
                });

        binding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //정보수정 요청
                if (!state) {
                    state = true;
                    setFocusable(true);
                    binding.buttonEdit.setText("수정완료");
                    ((HomeActivity) getActivity()).getBinding().topAppBar.setTitle("내 정보 수정");
                }

                //수정완료
                else {
                    checkLayoutEmpty();

                    //변경되었었으면 false로
                    if (binding.textViewDupChecked.getVisibility() == View.INVISIBLE) {
                        dupChecked = false;
                    }

                    if (phoneChecked == false) {
                        phoneChecked = myInfoViewModel.checkPastPhone(binding.editTextPhone.getText().toString());
                    }
                    if (dupChecked == false) {
                        dupChecked = myInfoViewModel.checkPastNickname(binding.editTextNickname.getText().toString());
                    }

                    if (!phoneChecked || !dupChecked ||
                            binding.textFieldPassword.getError() != null ||
                            binding.textFieldPasswordDup.getError() != null ||
                            binding.textFieldName.getError() != null ||
                            binding.textFieldPhone.getError() != null ||
                            binding.textFieldNickname.getError() != null ||
                            binding.textFieldRoadAddress.getError() != null ||
                            binding.textFieldDetailAddress.getError() != null) {
                        Toast.makeText(getContext(), getResources().getString(R.string.signup_error_process), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(binding.editTextPassword.getText().toString())) {
                        myInfoViewModel.setPassword(null);
                    } else {
                        myInfoViewModel.setPassword(binding.editTextPassword.getText().toString());
                    }

                    myInfoViewModel.setName(binding.editTextName.getText().toString());
                    myInfoViewModel.setPhone(binding.editTextPhone.getText().toString());
                    myInfoViewModel.setNickName(binding.editTextNickname.getText().toString());
                    myInfoViewModel.setRoadAddress(binding.editTextRoadAddress.getText().toString());
                    myInfoViewModel.setDetailAddress(binding.editTextDetailAddress.getText().toString());


                    Log.e("edit", myInfoViewModel.toMyInfoEditDto().toString());
                    //정보 수정 절차 진행
                    myInfoViewModel.requestEditInfo(
                            ((HomeActivity) getActivity()).getLoginToken(),
                            MysosoMyInfoFragment.this::onEditSuccess,
                            MysosoMyInfoFragment.this::onNetworkError);
                }
            }
        });

        binding.buttonDupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.editTextNickname.getText().toString())) {
                    binding.textFieldNickname.setError(getResources().getString(R.string.signup_error_nickname));
                }

                if (binding.editTextNickname.getError() != null) return;

                //현재 닉네임과 동일
                if (myInfoViewModel.checkPastNickname(binding.editTextNickname.getText().toString())) {
                    onNicknameNotDuplicated();
                    return;
                }

                //중복확인 요청 API
                myInfoViewModel.requestNicknameDupCheck(
                        binding.editTextNickname.getText().toString(),
                        MysosoMyInfoFragment.this::onNicknameNotDuplicated,
                        MysosoMyInfoFragment.this::onNicknameDuplicated,
                        MysosoMyInfoFragment.this::onNetworkError);
            }
        });

        binding.buttonPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.editTextPhone.getText().toString())) {
                    binding.textFieldPhone.setError(getResources().getString(R.string.signup_error_phone));
                }

                if (binding.editTextPhone.getError() != null) return;

                //현재 번호와 동일
                if (myInfoViewModel.checkPastPhone(binding.editTextPhone.getText().toString())) {
                    onPhoneNotDuplicated();
                    return;
                }

                //번호중복여부
                myInfoViewModel.requestPhoneDupCheck(
                        binding.editTextPhone.getText().toString(),
                        MysosoMyInfoFragment.this::onPhoneNotDuplicated,
                        MysosoMyInfoFragment.this::onPhoneDuplicated,
                        MysosoMyInfoFragment.this::onNetworkError
                );
            }
        });
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity) getActivity()).showTopAppBar();
        //하단바
        ((HomeActivity) getActivity()).hideBottomNavigation();
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccess(MyInfoModel myInfoModel) {
        if (binding != null) {
            myInfoViewModel.setMyInfo(myInfoModel);

            binding.editTextEmail.setText(myInfoModel.getEmail());
            binding.editTextName.setText(myInfoModel.getName());
            binding.editTextPhone.setText(myInfoModel.getPhone());
            binding.editTextNickname.setText(myInfoModel.getNickname());
            binding.editTextRoadAddress.setText(myInfoModel.getStreetAddress());
            binding.editTextDetailAddress.setText(myInfoModel.getDetailedAddress());

        }
    }

    private void onEditSuccess() {
        Snackbar.make(
                ((HomeActivity)getActivity()).getMainView(), getResources().getString(R.string.mysoso_myInfo_editSuccess), Snackbar.LENGTH_SHORT).show();
        logOut();
    }

    private void onFailedLogIn() {
        if (binding != null) {
            NavHostFragment.findNavController(this)
                    .navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));
        }
    }

    private void onFailed() {
        if (binding != null) {
            Toast.makeText(getContext(), getResources().getString(R.string.shop_error), Toast.LENGTH_LONG).show();
        }
    }

    private void onNetworkError() {
        if (binding != null) {
            getActivity().onBackPressed();
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);

        }
    }

    private void onNicknameDuplicated() {
        dupChecked = false;
        binding.textViewDupChecked.setText("사용이 불가능합니다.");
        binding.textViewDupChecked.setVisibility(View.VISIBLE);
    }

    private void onNicknameNotDuplicated() {
        dupChecked = true;
        binding.textViewDupChecked.setText("사용 가능");
        binding.textViewDupChecked.setVisibility(View.VISIBLE);
    }

    private void onPhoneDuplicated() {
        phoneChecked = false;
        binding.textViewPhoneCheck.setText("이미 가입한 번호입니다");
        binding.textViewPhoneCheck.setVisibility(View.VISIBLE);
    }

    private void onPhoneNotDuplicated() {
        phoneChecked = true;
        binding.buttonPhoneCheck.setEnabled(false);
        binding.textFieldPhone.setEnabled(false);
        binding.textViewPhoneCheck.setText("인증완료");
        binding.textViewPhoneCheck.setVisibility(View.VISIBLE);
    }

    private void initialSetting() {

        binding.textFieldPassword.getEditText()
                .addTextChangedListener(new PasswordWatcher(binding.textFieldPassword, getResources().getString(R.string.signup_error_password)));
        binding.textFieldPasswordDup.getEditText()
                .addTextChangedListener(new PasswordDupWatcher(binding.textFieldPassword, binding.textFieldPasswordDup, getResources().getString(R.string.signup_error_passwordDup)));

        binding.textFieldName.getEditText().addTextChangedListener(new NameWatcher(binding.textFieldName, getResources().getString(R.string.signup_error_name)));

        binding.textFieldPhone.setCounterMaxLength(11);
        binding.textFieldPhone.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        /*binding.textFieldSignUpPhone.getEditText()
                .addTextChangedListener(new PhoneNumberFormattingTextWatcher());*/
        binding.textFieldPhone.getEditText()
                .addTextChangedListener(new PhoneWatcher(binding.textFieldPhone, getResources().getString(R.string.signup_error_phone)));

        binding.textFieldNickname.setCounterMaxLength(8);
        binding.textFieldNickname.getEditText()
                .addTextChangedListener(new NickNameWatcher(binding.textFieldNickname,
                        binding.textViewDupChecked, getResources().getString(R.string.signup_error_nickname)));

        binding.textViewDupChecked.setVisibility(View.INVISIBLE);
        binding.textViewPhoneCheck.setVisibility(View.INVISIBLE);
    }

    private void setFocusable(boolean state) {
        binding.textFieldName.setEnabled(state);
        binding.textFieldPhone.setEnabled(state);
        binding.textFieldNickname.setEnabled(state);

        binding.textFieldRoadAddress.setEnabled(state);
        binding.editTextRoadAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = NetworkStatus.getConnectivityStatus(getContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    navController.navigate(NavGraphDirections.actionGlobalRoadAddressSearchDialog());
                } else {
                    Snackbar.make(binding.getRoot(), "인터넷 연결을 확인해주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        binding.textFieldDetailAddress.setEnabled(state);
        binding.textFieldPassword.setEnabled(state);
        binding.textFieldPasswordDup.setEnabled(state);

        binding.buttonPhoneCheck.setEnabled(state);
        binding.buttonDupCheck.setEnabled(state);
    }

    public void checkLayoutEmpty() {
        if (TextUtils.isEmpty(binding.editTextName.getText().toString())) {
            binding.textFieldName.setError(getResources().getString(R.string.signup_error_name));
        }
        if (TextUtils.isEmpty(binding.editTextPhone.getText().toString())) {
            binding.textFieldPhone.setError(getResources().getString(R.string.signup_error_phone));
        }
        if (TextUtils.isEmpty(binding.editTextNickname.getText().toString())) {
            binding.textFieldNickname.setError(getResources().getString(R.string.signup_error_nickname));
        }
        if (TextUtils.isEmpty(binding.editTextRoadAddress.getText().toString())) {
            binding.textFieldRoadAddress.setError(getResources().getString(R.string.signup_error_roadAddress));
        }
        if (TextUtils.isEmpty(binding.editTextDetailAddress.getText().toString())) {
            binding.textFieldDetailAddress.setError(getResources().getString(R.string.signup_error_detailAddress));
        }
    }

    public void logOut(){
        SharedPreferenceManager.deleteString(getContext(), Constant.SHARED_PREFERENCE_KEY_ID);
        SharedPreferenceManager.deleteString(getContext(), Constant.SHARED_PREFERENCE_KEY_PASSWORD);
        ((HomeActivity)getActivity()).setLoginToken(null);
        ((HomeActivity)getActivity()).setIsLogIn(false);

        ((HomeActivity) getActivity()).bottomItemClicked(R.id.menu_home);
    }
}
