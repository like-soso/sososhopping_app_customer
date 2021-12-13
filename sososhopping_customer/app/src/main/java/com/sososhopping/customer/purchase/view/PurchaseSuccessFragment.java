package com.sososhopping.customer.purchase.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.PurchaseSuccessBinding;

public class PurchaseSuccessFragment extends Fragment {
    PurchaseSuccessBinding binding;
    NavController navController;

    int storeId;

    public static PurchaseSuccessFragment newInstance() {return new PurchaseSuccessFragment();}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        //binding 설정
        binding = PurchaseSuccessBinding.inflate(inflater,container,false);

        //id 저장
        storeId = PurchaseSuccessFragmentArgs.fromBundle(getArguments()).getStoreId();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Controller 설정
        navController = Navigation.findNavController(view);

        binding.buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //home을 뒤에 넣어놔야함
                navController.navigate(PurchaseSuccessFragmentDirections.actionPurchaseSuccessFragmentToShopGraph(storeId));
            }
        });

        binding.buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.cartMainFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
            }
        });

        binding.buttonMyPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(PurchaseSuccessFragmentDirections.actionPurchaseSuccessFragmentToMysosoOrderListFragment());
            }
        });

        binding.buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)getActivity()).getBinding().bottomNavigation.setSelectedItemId(R.id.menu_home);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).hideTopAppBar();

        //하단바
        ((HomeActivity)getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
