package com.sososhopping.customer.shop.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.carousel.CarouselMethod;
import com.sososhopping.customer.common.DateFormatMethod;
import com.sososhopping.customer.databinding.ShopEventDetailBinding;
import com.sososhopping.customer.shop.model.EventDetailModel;
import com.sososhopping.customer.shop.viewmodel.ShopEventDetailViewModel;

public class ShopEventDetailFragment extends DialogFragment {
    ShopEventDetailBinding binding;
    ShopEventDetailViewModel shopEventDetailViewModel = new ShopEventDetailViewModel();

    int writingId, storeId;
    String storeName;

    public static ShopEventDetailFragment newInstance(){return new ShopEventDetailFragment();}

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        binding = ShopEventDetailBinding.inflate(inflater,container,false);

        //내용 받아오기
        writingId = ShopEventDetailFragmentArgs.fromBundle(getArguments()).getWriteId();
        storeId = ShopEventDetailFragmentArgs.fromBundle(getArguments()).getShopId();
        storeName = ShopEventDetailFragmentArgs.fromBundle(getArguments()).getStoreName();

        shopEventDetailViewModel.requestShopEventDetail(storeId, writingId,
                this::onSuccess, this::onFailed, this::onNetworkError);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    public void setShopInfo(EventDetailModel eventDetailModel){
        binding.textViewTitle.setText(eventDetailModel.getTitle());
        binding.textViewContent.setText(eventDetailModel.getContent());
        binding.textViewWriteDate.setText(DateFormatMethod.dateFormatMin(eventDetailModel.getCreatedAt()));
        binding.textViewType.setText(eventDetailModel.getWritingType().getValue());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccess(EventDetailModel eventDetailModel){
        if(binding != null){
            //setting Info
            setShopInfo(eventDetailModel);

            //이미지 세팅
            if(eventDetailModel.getImgUrl() == null){
                binding.viewpagerEventDetail.setVisibility(View.GONE);
                binding.layoutIndicators.setVisibility(View.GONE);
            }
            else if(eventDetailModel.getImgUrl().size() <= 0){
                binding.viewpagerEventDetail.setVisibility(View.GONE);
                binding.layoutIndicators.setVisibility(View.GONE);
            }else{
                CarouselMethod carouselMethod = new CarouselMethod(binding.layoutIndicators, binding.viewpagerEventDetail, getContext());
                carouselMethod.setCarousel(eventDetailModel.getImgUrl());
            }
        }
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkError() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        dismiss();
    }

}
