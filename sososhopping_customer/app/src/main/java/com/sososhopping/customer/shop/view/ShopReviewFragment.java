package com.sososhopping.customer.shop.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ShopReviewBinding;
import com.sososhopping.customer.shop.dto.PageableReviewListDto;
import com.sososhopping.customer.shop.view.adapter.ShopReviewAdapter;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;
import com.sososhopping.customer.shop.viewmodel.ShopReviewViewModel;

import org.jetbrains.annotations.NotNull;


public class ShopReviewFragment extends Fragment {
    private NavController navController;
    private final ShopReviewAdapter shopReviewAdapter = new ShopReviewAdapter();
    private final ShopReviewViewModel reviewViewModel = new ShopReviewViewModel();
    private ShopReviewBinding binding;

    int storeId = -1;
    MutableLiveData<Boolean> isExist = new MutableLiveData<>();

    public static ShopReviewFragment newInstance() {
        return new ShopReviewFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = ShopReviewBinding.inflate(inflater, container, false);
        storeId = new ViewModelProvider(getParentFragment().getParentFragment()).get(ShopInfoViewModel.class).getShopId().getValue();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewReview.setLayoutManager(layoutManager);
        binding.recyclerViewReview.setAdapter(shopReviewAdapter);

        isExist.setValue(false);
        isExist.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    binding.buttonAddReview.setText("후기 삭제하기");
                }else{
                    binding.buttonAddReview.setText("후기 작성하기");
                }
            }
        });

        reviewViewModel.init();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        //초기값
        reviewViewModel.requestShopReviewsPage(storeId,
                0,
                this::onSuccess,
                this::onFailed,
                this::onNetworkError);

        String token = ((HomeActivity) getActivity()).getLoginToken();
        if (token != null) {
            reviewViewModel.checkShopReview(token, storeId,
                    this::onDup,
                    this::onFailed,
                    this::onNetworkError);
        }

        binding.buttonAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExist.getValue()){
                    reviewViewModel.deleteShopReview(
                            ((HomeActivity)getActivity()).getLoginToken(),
                            storeId,
                            ShopReviewFragment.this::onSuccessDelete,
                            ShopReviewFragment.this::onFailedDelete,
                            ShopReviewFragment.this::onNetworkError
                    );
                }
                else{
                    //리뷰 작성 Dialog
                    navController.navigate(ShopReviewFragmentDirections.actionShopReviewFragmentToShopReviewAddDialogFragment(storeId));
                }
            }
        });

        binding.recyclerViewReview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    binding.progressCircular.setVisibility(View.VISIBLE);
                } else if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (reviewViewModel.getNumberOfElement() > 0) {
                        binding.progressCircular.setVisibility(View.VISIBLE);
                        reviewViewModel.requestShopReviewsPage(
                                storeId,
                                null,
                                ShopReviewFragment.this::onSuccess,
                                ShopReviewFragment.this::onFailed,
                                ShopReviewFragment.this::onNetworkError
                        );
                    }
                } else {
                    binding.progressCircular.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccess(PageableReviewListDto success) {
        if (binding != null) {
            binding.progressCircular.setVisibility(View.GONE);
            if (success.getNumberOfElements() > 0) {
                reviewViewModel.getReviewModels().getValue().addAll(success.getContent());
                shopReviewAdapter.setReviewModels(reviewViewModel.getReviewModels().getValue());
                shopReviewAdapter.notifyItemRangeInserted(reviewViewModel.getOffset(), success.getNumberOfElements());
            }
            reviewViewModel.setNumberOfElement(success.getNumberOfElements());
            reviewViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());
        }
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkError() {
        NavHostFragment.findNavController(getParentFragment().getParentFragment()).navigate(R.id.action_global_networkErrorDialog);
    }

    private void onSuccessDelete(){
        if(binding != null){
            isExist.setValue(false);
        }
        Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                getResources().getString(R.string.mysoso_myRating_delte_success), Snackbar.LENGTH_SHORT).show();
        navController.navigate(ShopReviewFragmentDirections.actionShopReviewFragmentSelf());
    }

    private void onFailedDelete() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                getResources().getString(R.string.mysoso_myRating_delte_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onDup() {
        isExist.setValue(true);
    }
}