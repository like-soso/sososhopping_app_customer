package com.sososhopping.customer.shop.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ShopReviewAddDialogBinding;
import com.sososhopping.customer.shop.viewmodel.ShopReviewViewModel;

public class ShopReviewAddDialogFragment extends DialogFragment {
    private ShopReviewAddDialogBinding binding;
    private final ShopReviewViewModel reviewViewModel = new ShopReviewViewModel();
    int storeId = -1;

    public static ShopReviewAddDialogFragment newInstance(){return new ShopReviewAddDialogFragment();}

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = ShopReviewAddDialogBinding.inflate(inflater,container,false);
        storeId = ShopReviewAddDialogFragmentArgs.fromBundle(getArguments()).getStoreId();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        binding.buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //제출 시나리오
                String content = binding.editTextReviewContent.getText().toString();
                int score = (int) binding.ratingBarReview.getRating();

                reviewViewModel.inputShopReviews(
                        ((HomeActivity)getActivity()).getLoginToken(),
                        storeId,
                        reviewViewModel.getReviewInputDto(score, content),
                        ShopReviewAddDialogFragment.this::onSuccess,
                        ShopReviewAddDialogFragment.this::onFailedLogIn,
                        ShopReviewAddDialogFragment.this::onFailed,
                        ShopReviewAddDialogFragment.this::onNetworkError);
            }
        });
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    public void onSuccess(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.review_input), Snackbar.LENGTH_SHORT).show();

        if(binding != null){
            NavHostFragment.findNavController(getParentFragment())
                    .navigate(ShopReviewAddDialogFragmentDirections.actionShopReviewAddDialogFragmentToShopReviewFragment());
        }

        //종료
        dismiss();
    }

    private void onFailedLogIn(){
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.login_error_token), Snackbar.LENGTH_SHORT).show();
        ((HomeActivity)getActivity()).setLoginToken(null);
        //종료
        dismiss();
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkError() {
        //종료
        dismiss();
    }
}
