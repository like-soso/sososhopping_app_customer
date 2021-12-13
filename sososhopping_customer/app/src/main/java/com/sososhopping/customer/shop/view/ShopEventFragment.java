package com.sososhopping.customer.shop.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ShopEventBinding;
import com.sososhopping.customer.shop.dto.CouponListDto;
import com.sososhopping.customer.shop.dto.EventItemListDto;
import com.sososhopping.customer.shop.dto.PageableWritingListDto;
import com.sososhopping.customer.shop.model.CouponModel;
import com.sososhopping.customer.shop.view.adapter.ShopEventBoardAdapter;
import com.sososhopping.customer.shop.view.adapter.ShopEventCouponAdapter;
import com.sososhopping.customer.shop.viewmodel.ShopEventViewModel;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;


public class ShopEventFragment extends Fragment {
    private final ShopEventBoardAdapter shopEventBoardAdapter = new ShopEventBoardAdapter();
    private final ShopEventCouponAdapter shopEventCouponAdapter = new ShopEventCouponAdapter();
    ShopEventBinding binding;

    private ShopInfoViewModel shopInfoViewModel;
    private ShopEventViewModel shopEventViewModel;

    private final int[] msgCode = {R.string.event_coupon_addSucc, R.string.event_coupon_addFail, R.string.event_coupon_addDup};

    public static ShopEventFragment newInstance() {
        return new ShopEventFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ShopEventBinding.inflate(inflater, container, false);


        shopInfoViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(ShopInfoViewModel.class);
        shopEventViewModel = new ShopEventViewModel();
        int storeId = shopInfoViewModel.getShopId().getValue();

        LinearLayoutManager layoutManager_coupon = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        LinearLayoutManager layoutManager_event = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewBoard.setLayoutManager(layoutManager_event);
        binding.recyclerViewCoupon.setLayoutManager(layoutManager_coupon);

        binding.recyclerViewCoupon.setAdapter(shopEventCouponAdapter);
        binding.recyclerViewBoard.setAdapter(shopEventBoardAdapter);

        shopEventViewModel.requestShopCoupon(storeId,
                ShopEventFragment.this::onSuccessCoupon,
                ShopEventFragment.this::onFailed,
                ShopEventFragment.this::onNetworkError);

        //페이징
        shopEventViewModel.requestShopEvent(storeId,
                null,
                ShopEventFragment.this::onSuccessEvent,
                ShopEventFragment.this::onFailed,
                ShopEventFragment.this::onNetworkError);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int storeId = shopInfoViewModel.getShopId().getValue();

        shopEventBoardAdapter.setOnItemClickListener(new ShopEventBoardAdapter.OnItemClickListenerBoard() {
            @Override
            public void onItemClick(int writingId) {

                //게시판 상세정보로 이동
                NavHostFragment.findNavController(getParentFragment().getParentFragment())
                        .navigate(ShopMainFragmentDirections.actionShopMainFragmentToShopEventDetailFragment(
                                storeId,
                                writingId, shopInfoViewModel.getShopName().getValue()));
            }
        });

        binding.recyclerViewBoard.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    binding.progressCircular.setVisibility(View.VISIBLE);
                } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (shopEventViewModel.getNumberOfElement() > 0) {
                        binding.progressCircular.setVisibility(View.VISIBLE);
                        shopEventViewModel.requestShopEvent(
                                storeId,
                                null,
                                ShopEventFragment.this::onSuccessEvent,
                                ShopEventFragment.this::onFailed,
                                ShopEventFragment.this::onNetworkError
                        );
                    }
                } else {
                    binding.progressCircular.setVisibility(View.GONE);
                }
            }
        });

        shopEventCouponAdapter.setOnItemClickListener(new ShopEventCouponAdapter.OnItemClickListenerCoupon() {
            @Override
            public void onItemClick(CouponModel couponModel) {
                String token = ((HomeActivity) getActivity()).getLoginToken();
                if (token != null) {

                    int[] msgCode = new int[3];
                    msgCode[0] = R.string.event_coupon_addSucc;
                    msgCode[1] = R.string.event_coupon_addFail;
                    msgCode[2] = R.string.event_coupon_addDup;

                    shopEventViewModel.addShopCoupon(token, couponModel.getCouponCode(),
                            msgCode,
                            ShopEventFragment.this::onResult,
                            ShopEventFragment.this::onFailedLogInCoupon,
                            ShopEventFragment.this::onNetworkError
                    );
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.requireLogIn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        setButtonArrows();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccessCoupon(CouponListDto couponModels) {
        if (binding != null) {
            shopEventCouponAdapter.storeName = shopInfoViewModel.getShopName().getValue();
            if (couponModels.getResults() != null) {
                shopEventViewModel.setCouponModels(couponModels.getResults());
            }
            shopEventCouponAdapter.setCouponModels(shopEventViewModel.getCouponModels());
            shopEventCouponAdapter.notifyDataSetChanged();
        }
    }

    private void onSuccessEvent(PageableWritingListDto success) {
        if (binding != null) {
            binding.progressCircular.setVisibility(View.GONE);

            if (success.getNumberOfElements() > 0) {
                shopEventViewModel.getEventItemModels().addAll(success.getContent());
                shopEventBoardAdapter.setShopBoardItemModels(shopEventViewModel.getEventItemModels());
                shopEventBoardAdapter.notifyItemRangeInserted(shopEventViewModel.getOffset(), success.getNumberOfElements());
            }
            shopEventViewModel.setNumberOfElement(success.getNumberOfElements());
            shopEventViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());
        }
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onResult(int msgCode) {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                getResources().getString(msgCode), Snackbar.LENGTH_SHORT).show();
    }

    private void onFailedLogInCoupon() {
        if (binding != null) {
            NavHostFragment.findNavController(getParentFragment().getParentFragment())
                    .navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));

        }
    }

    private void onNetworkError() {
        NavHostFragment.findNavController(getParentFragment().getParentFragment()).navigate(R.id.action_global_networkErrorDialog);
        getActivity().onBackPressed();
    }

    private void setButtonArrows() {
        binding.imageButtonBoardArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내리기
                binding.imageButtonBoardArrowDown.setVisibility(View.GONE);
                binding.imageButtonBoardArrowUp.setVisibility(View.VISIBLE);
                binding.recyclerViewBoard.setVisibility(View.GONE);
            }
        });

        binding.imageButtonBoardArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //올리기
                binding.imageButtonBoardArrowDown.setVisibility(View.VISIBLE);
                binding.imageButtonBoardArrowUp.setVisibility(View.GONE);
                binding.recyclerViewBoard.setVisibility(View.VISIBLE);
            }
        });

        binding.imageButtonCouponArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내리기
                binding.imageButtonCouponArrowDown.setVisibility(View.GONE);
                binding.imageButtonCouponArrowUp.setVisibility(View.VISIBLE);
                binding.recyclerViewCoupon.setVisibility(View.GONE);
            }
        });

        binding.imageButtonCouponArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //올리기
                binding.imageButtonCouponArrowDown.setVisibility(View.VISIBLE);
                binding.imageButtonCouponArrowUp.setVisibility(View.GONE);
                binding.recyclerViewCoupon.setVisibility(View.VISIBLE);
            }
        });
    }
}