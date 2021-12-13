package com.sososhopping.customer.search.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.common.types.enumType.AskType;
import com.sososhopping.customer.common.types.enumType.CategoryType;
import com.sososhopping.customer.common.types.enumType.SearchType;
import com.sososhopping.customer.databinding.SearchShopDialogBinding;
import com.sososhopping.customer.search.HomeViewModel;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.dto.ShopListDto;

public class SearchDialogFragment extends DialogFragment {
    private NavController navController;
    private SearchShopDialogBinding binding;
    private HomeViewModel homeViewModel;

    Integer fromHome;
    public static SearchDialogFragment newInstance(){return newInstance();}

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        binding = SearchShopDialogBinding.inflate(inflater,container,false);
        fromHome = SearchDialogFragmentArgs.fromBundle(getArguments()).getNavigateFrom();
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);


        //검색 반경 설정
        binding.slider.setValue(homeViewModel.getRadius().getValue());
        binding.textViewRadius.setText(homeViewModel.getRadius().getValue()+"km");
        binding.slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return value+"km";
            }
        });
        binding.slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                binding.textViewRadius.setText(value+"km");
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        navController = Navigation.findNavController(getParentFragment().getView());
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        //초기 조건 설정
        boolean isChecked = homeViewModel.getSearchType().getValue().equals(SearchType.ITEM);
        binding.switchShopOrItem.setChecked(isChecked);

        createChips();

        binding.switchShopOrItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchSetting(isChecked);
            }
        });

        binding.textFieldSearch.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //카테고리 검색 해제
                homeViewModel.getAskType().setValue(AskType.Search);

                //검색모드
                homeViewModel.setSearchContent(binding.editTextSearch.getText().toString());

                //offset 초기화
                homeViewModel.resetPage();

                homeViewModel.search(
                        ((HomeActivity)getActivity()).getLoginToken(),
                        homeViewModel.getLocation(getContext()),
                        (int)binding.slider.getValue(),
                        0,
                        fromHome,
                        SearchDialogFragment.this::onSearchSuccessedPage,
                        SearchDialogFragment.this::onNetworkError
                );
            }

        });
    }

    public void switchSetting(boolean isChecked){
        if(isChecked){
            //검색조건 : 상품
            binding.textViewShop.setTextColor(getResources().getColor(R.color.text_0));
            binding.textViewItem.setTextColor(getResources().getColor(R.color.text_400));
        }
        else{
            //검색조건 : 상점
            binding.textViewItem.setTextColor(getResources().getColor(R.color.text_0));
            binding.textViewShop.setTextColor(getResources().getColor(R.color.text_400));
        }
        homeViewModel.setSearchType(isChecked);
    }

    public void createChips(){

        for(CategoryType t : CategoryType.values()){
            //지도는 제외외
           if(t.equals(CategoryType.MAP)) continue;

            Chip a = new Chip(getContext());
            a.setText(t.getValue());
            a.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.main_400)));
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeViewModel.getAskType().setValue(AskType.Category);
                    homeViewModel.getCategory().setValue(t.toString());

                    homeViewModel.search(
                            ((HomeActivity)getActivity()).getLoginToken(),
                            homeViewModel.getLocation(getContext()),
                            (int)binding.slider.getValue(),
                            0,
                            fromHome,
                            SearchDialogFragment.this::onSearchSuccessedPage,
                            SearchDialogFragment.this::onNetworkError
                    );
                }
            });
            binding.chipGroupCategory.addView(a);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }

    private void onSearchSuccessedPage(PageableShopListDto success, Integer navigate){
        Log.e("성공여부", success.getNumberOfElements()+"");
        homeViewModel.getShopList().setValue(success.getContent());
        homeViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());

        //리스트로 복귀
        ((HomeActivity)getActivity()).setTopAppBarTitle(homeViewModel.getSearchContent().getValue());
        if(navigate != null){
            if(navigate == R.id.home2){
                navController.navigate(SearchDialogFragmentDirections.actionSearchDialogFragmentToShopListFragment());
            }
        }
        dismiss();
    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }

}
