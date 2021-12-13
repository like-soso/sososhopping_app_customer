package com.sososhopping.customer.mysoso.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.MysosoCouponsBinding;
import com.sososhopping.customer.mysoso.dto.MyCouponsDto;
import com.sososhopping.customer.mysoso.viemodel.MyCouponViewModel;
import com.sososhopping.customer.mysoso.view.adapter.ExpandableCouponData;
import com.sososhopping.customer.mysoso.view.adapter.MysosoCouponAdapter;
import com.sososhopping.customer.shop.model.CouponModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


public class MysosoCouponFragment extends Fragment {

    MysosoCouponsBinding binding;
    NavController navController;
    MyCouponViewModel myCouponViewModel;
    MysosoCouponAdapter mysosoCouponAdapter;

    public static MysosoCouponFragment newInstance() {
        return new MysosoCouponFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //binding 설정
        binding = MysosoCouponsBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager_coupon = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewCoupon.setLayoutManager(layoutManager_coupon);

        mysosoCouponAdapter = new MysosoCouponAdapter(new ArrayList<ExpandableCouponData>());
        binding.recyclerViewCoupon.setAdapter(mysosoCouponAdapter);

        //viewmodel 설정
        myCouponViewModel = new MyCouponViewModel();
        myCouponViewModel.requestCoupons(((HomeActivity) getActivity()).getLoginToken(),
                null,
                this::onSuccess,
                this::onFailedLogIn,
                this::onFailed,
                this::onNetworkError);

        //등록
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = binding.editTextAddCode.getText().toString();

                if (code.length() != 10) {
                    Toast.makeText(getContext(), getResources().getString(R.string.event_coupon_shortLength), Toast.LENGTH_SHORT).show();
                    return;
                }

                int[] msgCode = new int[3];
                msgCode[0] = R.string.event_coupon_addSucc;
                msgCode[1] = R.string.event_coupon_addFail;
                msgCode[2] = R.string.event_coupon_addDup;

                //쿠폰 저장
                myCouponViewModel.addShopCoupon(((HomeActivity) getActivity()).getLoginToken(),
                        binding.editTextAddCode.getText().toString(),
                        msgCode,
                        MysosoCouponFragment.this::onResult,
                        MysosoCouponFragment.this::onFailedLogIn,
                        MysosoCouponFragment.this::onNetworkError
                );
            }
        });

        mysosoCouponAdapter.setOnItemClickListener(new MysosoCouponAdapter.OnItemClickListenerChild() {
            @Override
            public void onItemClick(CouponModel couponModel) {
                //쿠폰 삭제 / 매장으로 이동
            }

            @Override
            public void onItemLongClick(CouponModel couponModel) {

                //안전을 위해서 다이얼로그 추가
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("쿠폰을 삭제하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int[] msgCode = new int[2];
                                msgCode[0] = R.string.event_coupon_deleteSucc;
                                msgCode[1] = R.string.event_coupon_deleteFail;

                                myCouponViewModel.deleteCoupon(((HomeActivity) getActivity()).getLoginToken(),
                                        couponModel.getCouponId(),
                                        msgCode,
                                        MysosoCouponFragment.this::onResult,
                                        MysosoCouponFragment.this::onFailedLogIn,
                                        MysosoCouponFragment.this::onNetworkError);
                            }
                        })
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
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
        ((HomeActivity) getActivity()).showTopAppBar();
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.mysoso_coupon));
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitleCentered(true);
        //하단바
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onSuccess(MyCouponsDto dto) {
        if (binding != null) {
            if (dto != null) {
                myCouponViewModel.setMyCoupons(dto.getCoupons());
                mysosoCouponAdapter.setItems(myCouponViewModel.parser());
                mysosoCouponAdapter.notifyDataSetChanged();
            }
        }

    }

    private void onResult(int msgCode) {
        if (binding != null) {
            Snackbar.make(binding.getRoot(), getResources().getString(msgCode), Snackbar.LENGTH_SHORT).show();

            if (msgCode == R.string.event_coupon_addSucc || msgCode == R.string.event_coupon_deleteSucc) {
                //쿠폰 다시 받아오기
                myCouponViewModel.requestCoupons(((HomeActivity) getActivity()).getLoginToken(),
                        null,
                        this::onSuccess,
                        this::onFailedLogIn,
                        this::onFailed,
                        this::onNetworkError);
            }
        }
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
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
        }
    }

}
