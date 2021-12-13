package com.sososhopping.customer.mysoso.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.sharedpreferences.SharedPreferenceManager;
import com.sososhopping.customer.databinding.MysosoSettingBinding;
import com.sososhopping.customer.mysoso.repository.MysosoMyInfoRepository;

import org.jetbrains.annotations.Nullable;

public class MysosoSettingFragment extends Fragment {

    public static MysosoSettingFragment newInstance() {return new MysosoSettingFragment();}
    MysosoSettingBinding binding;
    NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = MysosoSettingBinding.inflate(inflater,container,false);

        //viewmodel 설정


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        //TODO : 이벤트 알림 설정
        binding.switchEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.switchEvent.setEnabled(false);

        //TODO : 채팅 알림 설정
        binding.switchChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        binding.switchChat.setEnabled(false);

        //로그아웃
        binding.constraintLayoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 정보 삭제
                try {
                    Snackbar.make(binding.getRoot(), getResources().getString(R.string.setting_logout_ask), Snackbar.LENGTH_SHORT)
                            .setAction(R.string.setting_logout, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    logOut();
                                    Snackbar.make(binding.getRoot(), getResources().getString(R.string.setting_logout_success), Snackbar.LENGTH_SHORT).show();
                                }
                            }).show();
                }catch (Exception e){
                    //실패 메시지
                    Snackbar.make(binding.getRoot(), getResources().getString(R.string.setting_logout_failed), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //데이터 삭제


        //회원탈퇴
        binding.constraintLayoutQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("회원탈퇴")
                        .setMessage("회원탈퇴 시 관련된 정보가 모두 삭제됩니다.\n회원탈퇴는 1일 정도 소요됩니다.\n정말 탈퇴하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MysosoMyInfoRepository.getInstance().requestQuit(
                                        ((HomeActivity)getActivity()).getLoginToken(),
                                        MysosoSettingFragment.this::onSuccess,
                                        MysosoSettingFragment.this::onFailed
                                );
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).setTopAppBarTitle(getResources().getString(R.string.setting));

        //하단바
        ((HomeActivity)getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void logOut(){
        SharedPreferenceManager.deleteString(getContext(), Constant.SHARED_PREFERENCE_KEY_ID);
        SharedPreferenceManager.deleteString(getContext(), Constant.SHARED_PREFERENCE_KEY_PASSWORD);
        ((HomeActivity)getActivity()).setLoginToken(null);
        ((HomeActivity)getActivity()).setIsLogIn(false);

        ((HomeActivity) getActivity()).bottomItemClicked(R.id.menu_home);
    }

    public void onSuccess(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),"회원탈퇴에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
        logOut();
    }

    public void onFailed(){
        Snackbar snackbar = Snackbar.make(((HomeActivity)getActivity()).getMainView(),"회원탈퇴에 실패하였습니다" +
                "\n회원탈퇴는 대기/진행중인 주문이 모두 종료되어야 가능합니다. " +
                "혹시 주문이 있는지 확인해주세요.", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("주문확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navController.navigate(MysosoSettingFragmentDirections.actionMysosoSettingFragmentToMysosoOrderListFragment());
                        snackbar.dismiss();
                    }
                });
        View snackbarView = snackbar.getView();
        TextView snackTextView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackTextView.setMaxLines(4);
        snackbar.show();
    }
}
