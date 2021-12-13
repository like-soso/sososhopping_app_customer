package com.sososhopping.customer.purchase.view;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.NetworkStatus;
import com.sososhopping.customer.purchase.model.OrderRequestModel;
import com.sososhopping.customer.purchase.viewmodel.PurchaseViewModel;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.databinding.PurchaseMainBinding;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.shop.model.CouponModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;

import org.jetbrains.annotations.Nullable;

public class PurchaseFragment extends Fragment {

    private PurchaseMainBinding binding;
    private NavController navController;

    PurchaseViewModel purchaseViewModel;

    boolean itemClosed = false;
    boolean pointClosed = false;
    boolean visitClosed = false;
    boolean purchClosed = false;

    PurchaseFragment_Item purchaseFragment_item;
    PurchaseFragment_Payments purchaseFragment_payments;
    PurchaseFragment_Visit purchaseFragment_visit;
    PurchaseFragment_Point purchaseFragment_point;

    public static PurchaseFragment newInstance() {return new PurchaseFragment();}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //메뉴 변경 확인
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_none, menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = PurchaseMainBinding.inflate(inflater,container,false);

        //viewmodel 설정
        purchaseViewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);
        purchaseViewModel.setPurchaseList(
                PurchaseFragmentArgs.fromBundle(getArguments()).getStoreId(),
                PurchaseFragmentArgs.fromBundle(getArguments()).getPurchaseList()
        );

        observing();

        //상점정보 -> 에러만 뜨게
        purchaseViewModel.requestShopIntroduce(
                ((HomeActivity)getActivity()).getLoginToken(),
                purchaseViewModel.getPurchaseList().getValue().getStoreId(),
                this::onSuccess,
                this::onNetworkError,
                this::onNetworkError
        );

        //통신 필요 X

        //ItemLayout
        purchaseFragment_item = new PurchaseFragment_Item(
                binding,
                purchaseViewModel,
                getContext()
        );
        purchaseFragment_item.setItemLayout();

        //purchaseLayout
        purchaseFragment_payments = new PurchaseFragment_Payments(
                binding,
                purchaseViewModel,
                getContext()
        );
        purchaseFragment_payments.setPaymentsLayout(getResources());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Controller 설정
        navController = Navigation.findNavController(view);

        buttonCloseOpen();
        observing();
        //내정보 받아오기
        purchaseViewModel.requestMyInfo(
                ((HomeActivity)getActivity()).getLoginToken(),
                this::onSuccessMyInfo,
                this::onFailedMyInfo
        );

        //통신 필요
        //VisitLayout
        purchaseFragment_visit= new PurchaseFragment_Visit(
                binding,
                purchaseViewModel,
                getContext()
        );

        purchaseFragment_visit.setVisitLayout(getParentFragmentManager(), getResources());
        purchaseFragment_visit.setDeliveryLayout(getResources());



        binding.includeLayoutVisit.editTextRoadAddress.setOnClickListener(new View.OnClickListener() {
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

        //pointLayout
       purchaseFragment_point = new PurchaseFragment_Point(
                binding,
                purchaseViewModel,
                getContext()
        );
        purchaseFragment_point.setPointLayout(getResources(), navController);

        observingDialog();

        binding.includeLayoutTotal.buttonTotalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindow pop = new PopupWindow(getContext());
                LinearLayout layout = new LinearLayout(getContext());

                TextView textView = new TextView(getContext());
                textView.setText(getResources().getString(R.string.order_info));
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setGravity(Gravity.CENTER);

                ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.addView(textView, params);
                layout.setPadding(20,10,10,20);

                layout.setBackground(getResources().getDrawable(R.drawable.drawable_background_toast));
                pop.setContentView(layout);


                // Closes the popup window when touch outside.
                pop.setOutsideTouchable(true);
                pop.setFocusable(true);
                pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pop.showAsDropDown(binding.includeLayoutTotal.buttonTotalInfo,
                            (int)0.2*binding.includeLayoutPoint.textFieldUsePoint.getWidth(),
                            -2*binding.includeLayoutPoint.textFieldUsePoint.getHeight(),Gravity.CENTER);
                }
                else{
                    pop.showAsDropDown(binding.includeLayoutTotal.buttonTotalInfo,
                            (int)0.2*binding.includeLayoutPoint.textFieldUsePoint.getWidth(),
                            -2*binding.includeLayoutPoint.textFieldUsePoint.getHeight());
                }
            }
        });

        binding.includeLayoutTotal.buttonFinalPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1. 입력 검증 (Visited / Delivery)
                if(!checkInput()){
                    return;
                }

                // 2. Dto 생성
                OrderRequestModel dto;
                if(purchaseViewModel.getOrderType() == OrderType.ONSITE){

                    dto = purchaseViewModel.orderRequestDto(
                            binding.includeLayoutVisit.editTextName.getText().toString(),
                            binding.includeLayoutVisit.editTextPhone.getText().toString(),
                            binding.includeLayoutVisit.editTextDate.getText().toString(),
                            binding.includeLayoutVisit.editTextTime.getText().toString(),
                            null,
                            null
                    );
                }
                else{
                    dto = purchaseViewModel.orderRequestDto(
                            binding.includeLayoutVisit.editTextDeliveryName.getText().toString(),
                            binding.includeLayoutVisit.editTextDeliveryPhone.getText().toString(),
                            null,
                            null,
                            binding.includeLayoutVisit.editTextRoadAddress.getText().toString(),
                            binding.includeLayoutVisit.editTextDetailAddress.getText().toString()
                    );
                }

                // 3. 구매 요청
                purchaseViewModel.requestOrder(
                        ((HomeActivity)getActivity()).getLoginToken(),
                        dto,
                        PurchaseFragment.this::onSuccessOrder,
                        PurchaseFragment.this::onFailedOrder,
                        PurchaseFragment.this::onNetworkError
                );
            }
        });


    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).setTopAppBarTitle("구매하기");

        //하단바
        ((HomeActivity)getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccess(ShopIntroduceModel shopIntroduceModel){
        if(binding != null){
            purchaseViewModel.getShopInfo().setValue(shopIntroduceModel);
            purchaseViewModel.calPointMax();

            //매장이름설정
            binding.includeLayoutItem.textViewStoreName.setText(shopIntroduceModel.getName());

            //배송불가설정
            if(!shopIntroduceModel.isDeliveryStatus()){
                binding.includeLayoutVisit.buttonDelivery.setEnabled(false);
            }
            //영업하지 않는 매장
            if(!shopIntroduceModel.isBusinessStatus()){
                Toast.makeText(getContext(), "현재 영업하지 않는 매장입니다", Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
            }

            //지역화폐 불가
            if(!shopIntroduceModel.isLocalCurrencyStatus()){
                binding.includeLayoutPurchase.radioLocalPay.setEnabled(false);
            }
        }
    }

    private void onNetworkError() {
        if(binding != null){
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
        }
    }

    //내 정보 가져오기 실패 -> 실패해도 실행되게
    private void onFailedMyInfo(){
    }
    private void onSuccessMyInfo(MyInfoModel myInfo){
        if(binding != null){
            purchaseViewModel.getMyInfo().setValue(myInfo);
        }
    }

    private void onSuccessOrder(){
        navController.navigate(PurchaseFragmentDirections.actionPurchaseFragmentToPurchaseSuccessFragment(
                purchaseViewModel.getShopInfo().getValue().getStoreId()
        ));
    }
    private void onFailedOrder(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(), getResources().getString(R.string.order_error), Snackbar.LENGTH_SHORT).show();
    }

    public void observingDialog(){
        //쿠폰 옵저빙
        navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("couponParcel")
                .observe(getViewLifecycleOwner(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        purchaseViewModel.getUseCoupon().setValue((CouponModel) o);
                    }
                });

        //옵저버
        navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("roadAddress")
                .observe(getViewLifecycleOwner(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        binding.includeLayoutVisit.editTextRoadAddress.setText((CharSequence) o);
                    }
                });

    }
    public void observing(){


        purchaseViewModel.getUseCoupon().observe(getViewLifecycleOwner(), new Observer<CouponModel>() {
            @Override
            public void onChanged(CouponModel couponModel) {
                if(couponModel == null){
                    binding.includeLayoutPoint.linearLayoutCouponInfo.setVisibility(View.GONE);
                }
                else{
                    binding.includeLayoutPoint.textViewCouponName.setText(
                            couponModel.getCouponName()
                    );
                    binding.includeLayoutPoint.linearLayoutCouponInfo.setVisibility(View.VISIBLE);
                }

                //금액계산도 필요
                purchaseViewModel.calCouponPrice();
            }
        });

        //금액 옵저빙
        purchaseViewModel.getTotalPrice().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String totalPrice = integer+"원";
                binding.includeLayoutItem.textViewTotalStorePrice.setText(totalPrice);
                binding.includeLayoutTotal.textViewTotalPayTotalPrice.setText(totalPrice);

                //최종, 최대포인트 계산
                purchaseViewModel.calFinalPrice();
                purchaseViewModel.calPointMax();
            }
        });

        purchaseViewModel.getCouponDiscount().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String couponDiscount = integer+"원";
                binding.includeLayoutTotal.textViewTotalPayCoupon.setText("- "+couponDiscount);

                purchaseViewModel.calFinalPrice();
                purchaseViewModel.calPointMax();
            }
        });

        purchaseViewModel.getDeliveryPrice().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String delivery = integer+"원";
                binding.includeLayoutTotal.textViewTotalPayDelivery.setText(delivery);
                purchaseViewModel.calFinalPrice();
            }
        });

        purchaseViewModel.getUsePoint().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String pointDiscount = integer+"원";
                binding.includeLayoutTotal.textViewTotalPayPoint.setText("- "+pointDiscount);
                purchaseViewModel.calFinalPrice();
            }
        });

        purchaseViewModel.getFinalPrice().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String finalPrice = integer+"원";
                binding.includeLayoutTotal.textviewTotalPrice.setText(finalPrice);
            }
        });

        purchaseViewModel.getMaxPoint().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.includeLayoutPoint.textViewAvailablePoint.setText(Integer.toString(purchaseViewModel.getMaxPoint().getValue()));
                binding.includeLayoutPoint.editTextUsePoint.setText(null);
                purchaseViewModel.getUsePoint().setValue(0);
            }
        });
    }
    public void buttonCloseOpen(){
        //물품 접었다 펴기
        binding.includeLayoutItem.buttonItemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClosed){
                    binding.includeLayoutItem.layoutItemInfo.setVisibility(View.VISIBLE);
                    binding.includeLayoutItem.buttonItemInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                }else{
                    binding.includeLayoutItem.layoutItemInfo.setVisibility(View.GONE);
                    binding.includeLayoutItem.buttonItemInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                }
                itemClosed = !itemClosed;
            }
        });

        //방문 접었다 펴기
        binding.includeLayoutVisit.buttonVisitInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visitClosed){
                    binding.includeLayoutVisit.layoutVisitAll.setVisibility(View.VISIBLE);
                    binding.includeLayoutVisit.buttonVisitInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                }else{
                    binding.includeLayoutVisit.layoutVisitAll.setVisibility(View.GONE);
                    binding.includeLayoutVisit.buttonVisitInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                }
                visitClosed = !visitClosed;
            }
        });

        //포인트 접었다 펴기
        binding.includeLayoutPoint.buttonPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pointClosed){
                    binding.includeLayoutPoint.layoutPoint.setVisibility(View.VISIBLE);
                    binding.includeLayoutPoint.buttonPoint.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                }else{
                    binding.includeLayoutPoint.layoutPoint.setVisibility(View.GONE);
                    binding.includeLayoutPoint.buttonPoint.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                }
                pointClosed = !pointClosed;
            }
        });

        //결제수단 접었다 펴기
        binding.includeLayoutPurchase.buttonPaymentsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(purchClosed){
                    binding.includeLayoutPurchase.layoutPaymentsInfo.setVisibility(View.VISIBLE);
                    binding.includeLayoutPurchase.buttonPaymentsInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                }else{
                    binding.includeLayoutPurchase.layoutPaymentsInfo.setVisibility(View.GONE);
                    binding.includeLayoutPurchase.buttonPaymentsInfo.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                }
                purchClosed = !purchClosed;
            }
        });
    }

    public boolean checkInput(){
        //결제수단 검증
        String checkPur = purchaseFragment_payments.checkPayments(getResources());
        //배달 검증
        String check = purchaseFragment_visit.checkInput(getResources());
        if(checkPur != null){
            Toast.makeText(getContext(), checkPur, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(check != null){
            Toast.makeText(getContext(), check, Toast.LENGTH_SHORT).show();
            return false;
        }

        //현장결재인데, 배달이면 안됨
        if(binding.includeLayoutPurchase.radioGroup.getCheckedRadioButtonId() == R.id.radio_cash
        && binding.includeLayoutVisit.toggleButton.getCheckedButtonId() == R.id.button_delivery){
            Toast.makeText(getContext(), "현장 결제 시 배송 서비스는 불가합니다", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
