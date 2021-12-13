package com.sososhopping.customer.mysoso.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.MysosoMainBinding;

import org.jetbrains.annotations.Nullable;

public class
MysosoMainFragment extends Fragment {

    MysosoMainBinding binding;
    NavController navConroller;
    public static MysosoMainFragment newInstance() {return new MysosoMainFragment();}

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
        binding = MysosoMainBinding.inflate(inflater,container,false);

        //viewmodel 설정

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Controller 설정
        navConroller = Navigation.findNavController(view);

        //내정보
        binding.cardViewMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(R.id.action_mysosoMainFragment_to_mysosoMyInfoFragment);
            }
        });


        //관심가게
        binding.cardViewInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //하단바에 관심가게 클릭한 효과
                ((HomeActivity) getActivity()).bottomItemClicked(R.id.menu_interest);
            }
        });

        //채팅
        binding.cardViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).bottomItemClicked(R.id.menu_chat);
            }
        });

        //고객센터
        binding.cardViewServiceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(MysosoMainFragmentDirections.actionGlobalMysosoCustomerServiceDialog());
            }
        });

        //설정
        binding.cardViewSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(MysosoMainFragmentDirections.actionMysosoMainFragmentToMysosoSettingFragment());
            }
        });

        /*6개 메뉴*/
        binding.cardViewMyPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(R.id.action_mysosoMainFragment_to_mysosoPointFragment);
            }
        });

        binding.cardViewMyRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(R.id.action_mysosoMainFragment_to_mysosoReviewFragment);
            }
        });

        //쿠폰
        binding.cardViewMyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(R.id.action_mysosoMainFragment_to_mysosoCouponFragment);
            }
        });

        //장바구니
        binding.cardViewShoppingBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //하단바 클릭한 효과
                ((HomeActivity) getActivity()).bottomItemClicked(R.id.menu_cart);
            }
        });
        //결제내역
        binding.cardViewPayHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(MysosoMainFragmentDirections.actionMysosoMainFragmentToMysosoOrderListFragment());
            }
        });

        //지도
        binding.cardViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(MysosoMainFragmentDirections.actionMysosoMainFragmentToShopMapFragment(R.id.mysosoMainFragment));
            }
        });



        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.mysoso));
        ((HomeActivity)getActivity()).getBinding().topAppBar.setOnClickListener(null);
        ((HomeActivity)getActivity()).getBinding().topAppBar.setTitleCentered(true);
        ((HomeActivity)getActivity()).setTopAppBarNotHome(false);

        //하단바
        ((HomeActivity)getActivity()).showBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
