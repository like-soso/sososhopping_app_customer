package com.sososhopping.customer.shop.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.ShopGraphDirections;
import com.sososhopping.customer.common.carousel.CarouselMethod;
import com.sososhopping.customer.databinding.ShopIntroduceBinding;
import com.sososhopping.customer.search.HomeViewModel;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;
import com.sososhopping.customer.shop.viewmodel.ShopIntroduceViewModel;

import java.util.ArrayList;

public class ShopIntroduceFragment extends Fragment {
    private NavController navController;
    private ShopIntroduceBinding binding;
    private ShopIntroduceModel shopIntroduceModel;
    private final ShopIntroduceViewModel shopIntroduceViewModel = new ShopIntroduceViewModel();
    private ShopInfoViewModel shopInfoViewModel;

    public static ShopIntroduceFragment newInstance() {
        return new ShopIntroduceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ShopIntroduceBinding.inflate(inflater, container, false);
        shopInfoViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(ShopInfoViewModel.class);
        shopIntroduceViewModel.setStoreId(shopInfoViewModel.getShopId().getValue());
        shopIntroduceViewModel.requestShopIntroduce(
                ((HomeActivity) getActivity()).getLoginToken(),
                ShopIntroduceFragment.this::onSuccess,
                ShopIntroduceFragment.this::onFailed,
                ShopIntroduceFragment.this::onNetworkError);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.buttonShopMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //homeView에 아무도 없으면 추가해줘야함
                HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

                if (homeViewModel.getShopList().getValue() == null) {
                    homeViewModel.getShopList().postValue(new ArrayList<>());
                }

                int id = shopIntroduceModel.getStoreId();
                boolean isContained = false;
                for (ShopInfoShortModel s : homeViewModel.getShopList().getValue()) {
                    if (s.getStoreId() == id) {
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    homeViewModel.getShopList().getValue().add(
                            shopIntroduceViewModel.toShort(shopIntroduceModel, shopInfoViewModel.getDistance().getValue())
                    );
                }

                homeViewModel.setNumberOfElement(0);

                NavHostFragment.findNavController(getParentFragment().getParentFragment().getParentFragment())
                        .navigate(ShopGraphDirections.actionGlobalShopMapFragment(R.id.shopMainFragment)
                                .setLat((float) shopIntroduceModel.getLocation().getLat())
                                .setLng((float) shopIntroduceModel.getLocation().getLng()));
            }
        });

        binding.buttonShopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shopIntroduceModel.getPhone() != null) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + shopIntroduceModel.getPhone())));
                }
            }
        });

        //TODO : 채팅방 생성 (상점소개) -> 고객 닉네임 필요
        binding.buttonShopChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((HomeActivity) getActivity()).getLoginToken() == null) {
                    Snackbar.make(binding.getRoot(), "로그인 후에 이용해 주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                } else if (!((HomeActivity) getActivity()).isFirebaseSetted()) {
                    Snackbar.make(binding.getRoot(), "채팅 서버 인증 중입니다. 잠시만 기다려 주세요", Snackbar.LENGTH_SHORT).show();
                } else {
                    long storeId = shopIntroduceModel.getStoreId();
                    long ownerId = shopIntroduceModel.getOwnerId();
                    String storeName = shopIntroduceModel.getName();
                    String customerName = ((HomeActivity) getActivity()).getNickname();


                    String chatroomId = ((HomeActivity) getActivity()).makeChatroom(Long.toString(storeId), Long.toString(ownerId), storeName, customerName);

                    NavHostFragment.findNavController(getParentFragment().getParentFragment())
                            .navigate(NavGraphDirections.actionGlobalConversationFragment(storeName)
                                    .setChatroomId(chatroomId));
                }
            }
        });

        binding.buttonShopFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //관심가게 여부 변경 api
                shopIntroduceViewModel.requestShopFavoriteChange(
                        ((HomeActivity) getActivity()).getLoginToken(),
                        ShopIntroduceFragment.this::onSuccessFavoriteChange,
                        ShopIntroduceFragment.this::onFailedLogIn,
                        ShopIntroduceFragment.this::onFailed,
                        ShopIntroduceFragment.this::onNetworkError);
            }
        });
    }

    private void onSuccess(ShopIntroduceModel shopIntroduceModel) {
        if (shopIntroduceModel != null) {
            if (shopInfoViewModel.getShopIntroduceModel().getValue() == null) {
                ((ShopMainFragment) getParentFragment().getParentFragment()).initialSetting(shopIntroduceModel);
            }
            this.shopIntroduceModel = shopIntroduceModel;

            if (binding != null) {
                binding.textViewShopLocation.setText(shopIntroduceViewModel.getAddress(shopIntroduceModel));
                //영업일 계산
                try {
                    binding.textViewShopOpen.setText(shopIntroduceViewModel.getBusinessDay(shopIntroduceModel));
                    binding.textViewShopOpenDetail.setText(shopIntroduceModel.getExtraBusinessDay());

                    //제한
                    if (shopIntroduceModel.getMinimumOrderPrice() != null) {
                        binding.textViewShopRestrict.setText(shopIntroduceViewModel.getMinimum(shopIntroduceModel));
                        binding.textViewShopRestrict.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewShopRestrict.setVisibility(View.GONE);
                    }

                    //설명
                    binding.textViewShopIntroduce.setText(shopIntroduceModel.getDescription());

                    //포인트
                    if (shopIntroduceModel.getSaveRate() != null) {
                        binding.textViewShopPoint.setText(shopIntroduceViewModel.getSaveRate(shopIntroduceModel));
                    }

                    //배송비
                    if(shopIntroduceModel.isDeliveryStatus()){
                        binding.layoutDelivery.setVisibility(View.VISIBLE);
                        binding.textViewShopDelivery.setText(shopIntroduceModel.getDeliveryCharge()+"원");
                    }
                    else{
                        binding.layoutDelivery.setVisibility(View.GONE);
                    }

                    //번호
                    if (shopIntroduceModel.getPhone() != null) {
                        binding.textViewShopPhone.setText(shopIntroduceModel.getPhone());
                    }
                    changeFavoriteState(shopIntroduceModel.isInterestStore());
                    //이미지
                    CarouselMethod carouselMethod = new CarouselMethod(binding.layoutIndicators, binding.viewpagerIntroduce, getContext());
                    carouselMethod.setCarousel(shopIntroduceModel.getStoreImages());

                } catch (Exception e) {
                    //View에서는 혹시 에러 터져도 진행되게
                    e.printStackTrace();
                }
            }

        }
    }

    public void changeFavoriteState(boolean status) {
        if (!status) {
            binding.buttonShopFavorite.setText(getResources().getString(R.string.introduce_button_favorite));
        } else {
            binding.buttonShopFavorite.setText(getResources().getString(R.string.introduce_button_favorite_not));
        }
    }

    private void onSuccessFavoriteChange() {
        //관심여부 변경 (View단)
        boolean status = !shopIntroduceModel.isInterestStore();
        shopIntroduceModel.setInterestStore(status);
        ((ShopMainFragment) getParentFragment().getParentFragment()).changeFavoriteState(status);

        if (binding != null) {
            changeFavoriteState(status);
        }
    }

    private void onFailedLogIn() {
        if (binding != null) {
            NavHostFragment.findNavController(getParentFragment().getParentFragment()).navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));
        }
    }

    private void onFailed() {
        if (binding != null) {
            Snackbar.make(binding.getRoot(), getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        NavHostFragment.findNavController(getParentFragment().getParentFragment()).navigate(R.id.action_global_networkErrorDialog);
        getActivity().onBackPressed();
    }

}
