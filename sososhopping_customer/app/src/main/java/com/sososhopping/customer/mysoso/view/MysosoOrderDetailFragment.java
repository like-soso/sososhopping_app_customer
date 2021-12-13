package com.sososhopping.customer.mysoso.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.common.types.enumType.OrderType;
import com.sososhopping.customer.databinding.MysosoOrderDetailBinding;
import com.sososhopping.customer.mysoso.dto.OrderDetailDto;
import com.sososhopping.customer.mysoso.viemodel.OrderDetailViewModel;
import com.sososhopping.customer.mysoso.view.adapter.MysosoOrderDetailAdapter;

import org.jetbrains.annotations.Nullable;

public class MysosoOrderDetailFragment extends Fragment {

    private MysosoOrderDetailAdapter adapter;
    private MysosoOrderDetailBinding binding;
    private NavController navController;
    private OrderDetailViewModel orderDetailViewModel;

    public static MysosoOrderDetailFragment newInstance() {
        return new MysosoOrderDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //binding 설정
        binding = MysosoOrderDetailBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MysosoOrderDetailAdapter();
        binding.recyclerView.setAdapter(adapter);

        //정보 받아오기
        long orderId = MysosoOrderDetailFragmentArgs.fromBundle(getArguments()).getOrderId();

        //viewmodel 설정
        orderDetailViewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        orderDetailViewModel.requestMyOrderDetails(
                ((HomeActivity) getActivity()).getLoginToken(),
                orderId,
                this::onSuccess,
                this::onFailed,
                this::onNetworkError
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Controller 설정
        navController = Navigation.findNavController(view);

        binding.buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(MysosoOrderDetailFragmentDirections.actionMysosoOrderDetailFragmentToShopGraph(
                        orderDetailViewModel.getOrderDetailDto().getValue().getStoreId()
                ));
            }
        });

        binding.buttonShopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = orderDetailViewModel.getOrderDetailDto().getValue().getStorePhone();
                if (phone != null) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                }
            }
        });

        //TODO : 채팅방 생성 (주문상세조회) -> 고객 닉네임 필요
        binding.buttonShopChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((HomeActivity) getActivity()).isFirebaseSetted()) {
                    Snackbar.make(binding.getRoot(), "채팅 서버 인증 중입니다. 잠시만 기다려 주세요", Snackbar.LENGTH_SHORT).show();
                } else {
                    long storeId = orderDetailViewModel.getOrderDetailDto().getValue().getStoreId();
                    long ownerId = orderDetailViewModel.getOrderDetailDto().getValue().getOwnerId();
                    String storeName = orderDetailViewModel.getOrderDetailDto().getValue().getStoreName();
                    String customerName = ((HomeActivity) getActivity()).getNickname();

                    String chatroomId = ((HomeActivity) getActivity()).makeChatroom(Long.toString(storeId), Long.toString(ownerId), storeName, customerName);

                    navController
                            .navigate(NavGraphDirections.actionGlobalConversationFragment(storeName)
                                    .setChatroomId(chatroomId));
                }
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

    public void onSuccess(OrderDetailDto orderDetailDto) {
        if (binding != null) {
            //set
            try{
                orderDetailViewModel.getOrderDetailDto().setValue(orderDetailDto);

                //제목
                binding.textViewShopname.setText(orderDetailDto.getStoreName());
                adapter.setItems(orderDetailDto.getOrderItems());
                adapter.notifyDataSetChanged();
                binding.textViewTotalItemPrice.setText(orderDetailDto.getOrderPrice() + "원");

                binding.textViewVisitName.setText(orderDetailDto.getOrdererName());
                binding.textViewVisitPhone.setText(orderDetailDto.getOrdererPhone());


                if (orderDetailDto.getOrderType().equals(OrderType.ONSITE)) {
                    binding.textViewVisitOrDeliveryInfo.setText(getResources().getString(R.string.visit_info));
                    binding.textViewVisitDate.setText(DateFormatMethod.dateFormatToKorean(orderDetailDto.getVisitDate()));
                    binding.layoutAddress.setVisibility(View.GONE);
                    binding.layoutVisit.setVisibility(View.VISIBLE);
                } else {
                    binding.textViewVisitOrDeliveryInfo.setText(getResources().getString(R.string.delivery_info));
                    binding.textViewDeliveryAddress.setText(
                            orderDetailDto.getDeliveryStreetAddress() + " " + orderDetailDto.getDeliveryDetailedAddress()
                    );

                    binding.layoutAddress.setVisibility(View.VISIBLE);
                    binding.layoutVisit.setVisibility(View.GONE);
                }

                binding.textViewPurchaseType.setText(orderDetailDto.getPaymentType().getValue());
                binding.textViewPurchaseDate.setText(DateFormatMethod.dateFormatToKorean(orderDetailDto.getCreatedAt()));

                //금액
                binding.textViewTotalPayTotalPrice.setText(orderDetailDto.getOrderPrice() + "원");
                binding.textViewTotalPayPoint.setText("- "+orderDetailDto.getUsedPoint() + "원");
                binding.textViewTotalPayCoupon.setText("- "+orderDetailDto.getCouponDiscountPrice() + "원");
                binding.textViewTotalPayDelivery.setText(orderDetailDto.getDeliveryCharge() + "원");
                binding.textviewTotalPrice.setText(orderDetailDto.getFinalPrice() + "원");

                //최종상태
                binding.buttonFinalPurchase.setText(orderDetailDto.getOrderStatus().getValue());
                //최종확인
                if(orderDetailDto.getOrderStatus() == OrderStatus.READY){

                    if(orderDetailDto.getOrderType() == OrderType.DELIVERY){
                        binding.buttonFinalPurchase.setText("배송완료");
                    }
                    else if(orderDetailDto.getOrderType() == OrderType.ONSITE){
                        binding.buttonFinalPurchase.setText("픽업완료");
                    }

                    binding.buttonFinalPurchase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDetailViewModel.requestMyOrderDone(
                                    ((HomeActivity) getActivity()).getLoginToken(),
                                    orderDetailDto.getOrderId(),
                                    MysosoOrderDetailFragment.this::onSuccessDone,
                                    MysosoOrderDetailFragment.this::onFailedCancel,
                                    MysosoOrderDetailFragment.this::onNetworkError
                            );
                        }
                    });
                }else{
                    binding.buttonFinalPurchase.setText(orderDetailDto.getOrderStatus().getValue());
                    binding.buttonFinalPurchase.setEnabled(false);
                }

                //주문취소
                if (orderDetailDto.getOrderStatus() != OrderStatus.PENDING) {
                    binding.buttonFinalPurchaseCancel.setVisibility(View.GONE);
                } else {
                    binding.buttonFinalPurchaseCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            orderDetailViewModel.requestMyOrderCancel(
                                    ((HomeActivity) getActivity()).getLoginToken(),
                                    orderDetailDto.getOrderId(),
                                    MysosoOrderDetailFragment.this::onSuccessCancel,
                                    MysosoOrderDetailFragment.this::onFailedCancel,
                                    MysosoOrderDetailFragment.this::onNetworkError
                            );
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void onSuccessDone() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), "물품 수령을 완료하였습니다", Snackbar.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void onSuccessCancel() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.order_cancel_success), Snackbar.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    private void onFailedCancel() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.order_cancel_failed), Snackbar.LENGTH_SHORT).show();
        navController.navigate(MysosoOrderDetailFragmentDirections.actionMysosoOrderDetailFragmentSelf(
                orderDetailViewModel.getOrderDetailDto().getValue().getOrderId()
        ));
    }

    private void onFailed() {
        if (binding != null) {
            Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
            getActivity().onBackPressed();

        }
    }

    private void onNetworkError() {
        if (binding != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
            getActivity().onBackPressed();

        }
    }
}
