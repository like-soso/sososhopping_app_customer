package com.sososhopping.customer.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.types.enumType.AskType;
import com.sososhopping.customer.common.types.enumType.CategoryType;
import com.sososhopping.customer.databinding.HomeBinding;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.view.adapter.CategoryAdapter;
import com.sososhopping.customer.search.HomeViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private NavController navController;
    private final CategoryAdapter categoryAdapter = new CategoryAdapter();
    private HomeViewModel homeViewModel;
    HomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        ((HomeActivity) getActivity()).hideTopAppBar();
        ((HomeActivity) getActivity()).showBottomNavigation();
        ((HomeActivity) getActivity()).initLoginButton();
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5);
        binding.recyclerViewCategory.setLayoutManager(gridLayoutManager);
        binding.recyclerViewCategory.setAdapter(categoryAdapter);


        //처음 searchType
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        //homeViewModel.initHome();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String category = categoryAdapter.getCategoryName(pos);

                Integer navigate;
                homeViewModel.resetPage();

                if (category.equals(CategoryType.MAP.toString())) {
                    //전체검색으로 넘어가게
                    homeViewModel.getAskType().setValue(AskType.Search);
                    homeViewModel.setSearchContent(binding.editTextSearch.getText().toString());
                    navigate = R.id.shopMapFragment;
                } else {
                    //ViewModel 설정 후 이동
                    homeViewModel.getAskType().setValue(AskType.Category);
                    homeViewModel.setCategory(category);
                    homeViewModel.setSearchContent(null);
                    navigate = R.id.shopListFragment;
                }
                //검색
                homeViewModel.search(
                        ((HomeActivity) getActivity()).getLoginToken(),
                        homeViewModel.getLocation(getContext()),
                        null,
                        0,
                        navigate,
                        HomeFragment.this::onSearchSuccess,
                        HomeFragment.this::onNetworkError);
            }
        });

        binding.editTextSearch.setFocusable(false);
        binding.editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(HomeFragmentDirections.actionHome2ToSearchDialogFragment2(R.id.home2));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSearchSuccess(PageableShopListDto success, Integer navigate) {
        //offset 설정까지
        homeViewModel.getShopList().setValue(success.getContent());
        homeViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());

        if (navigate != null) {
            if (navigate == R.id.shopMapFragment) {
                navController.navigate(HomeFragmentDirections.actionHome2ToShopMapFragment(R.id.home2));
            } else if (navigate == R.id.shopListFragment) {
                navController.navigate(HomeFragmentDirections.actionHome2ToShopListFragment());
            }
        }
        //default
        else {
            navController.navigate(HomeFragmentDirections.actionHome2ToShopListFragment());
        }
    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }
}