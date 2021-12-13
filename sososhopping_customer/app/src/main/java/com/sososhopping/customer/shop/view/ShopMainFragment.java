package com.sososhopping.customer.shop.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.StringFormatMethod;
import com.sososhopping.customer.common.gps.CalculateDistance;
import com.sososhopping.customer.common.gps.GPSTracker;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.databinding.ShopMainBinding;
import com.sososhopping.customer.search.view.ShopListFragment;
import com.sososhopping.customer.shop.model.ShopIntroduceModel;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;

public class ShopMainFragment extends Fragment {

    private NavController navController;
    private ShopMainBinding binding;
    private ShopInfoViewModel shopInfoViewModel;


    public static ShopListFragment newInstance(){return new ShopListFragment();}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_shop, menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        binding = DataBindingUtil.inflate(inflater, R.layout.shop_main,container,false);

        //ViewModel Setting -> Main에서 유지되게
        shopInfoViewModel = new ViewModelProvider(this).get(ShopInfoViewModel.class);
        shopInfoViewModel.setShopId((int)ShopMainFragmentArgs.fromBundle(getArguments()).getStoreId());

        if(shopInfoViewModel.getDistance().getValue() == null){
            shopInfoViewModel.getDistance().setValue(ShopMainFragmentArgs.fromBundle(getArguments()).getDistance());
        }
        if(shopInfoViewModel.getShopIntroduceModel().getValue() != null){
            initialSetting(shopInfoViewModel.getShopIntroduceModel().getValue());
        }

        return binding.getRoot();
    }

    public void initialSetting(ShopIntroduceModel shopIntroduceModel){
        binding.textViewShopName.setText(shopIntroduceModel.getName());
        binding.textViewRating.setText(StringFormatMethod.getRating(shopIntroduceModel.getScore()));

        //안 받아왔으면
        if(shopInfoViewModel.getDistance().getValue() == -1){
            if(shopIntroduceModel.getLocation() != null){
                GPSTracker gpsTracker = GPSTracker.getInstance(getContext());
                Location me = new Location(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                shopInfoViewModel.getDistance().setValue(CalculateDistance.distance(me, shopIntroduceModel.getLocation()));
            }
        }

        if(shopInfoViewModel.getDistance().getValue() >= 1){
            binding.textViewDistance.setText(Math.round(shopInfoViewModel.getDistance().getValue()*10)/10.0+"km");
        }else{
            binding.textViewDistance.setText(Math.round(shopInfoViewModel.getDistance().getValue()*1000)+"m");
        }

        //지역화폐, 배달여부
        if(!shopIntroduceModel.isLocalCurrencyStatus()){
            binding.layoutLocalPay.setVisibility(View.GONE);
        }
        if(!shopIntroduceModel.isDeliveryStatus()){
            binding.layoutDelivery.setVisibility(View.GONE);
        }
        if(!shopIntroduceModel.isInterestStore()){
            binding.layoutFavorite.setVisibility(View.GONE);
        }

        //이미지
        Glide.with(binding.getRoot())
                .load(shopIntroduceModel.getImgUrl())
                .transform(new CenterCrop(),new RoundedCorners(10))
                .thumbnail(0.2f)
                .placeholder(R.drawable.icon_app_groceries)
                .error(R.drawable.icon_app_groceries)
                .fallback(R.drawable.icon_app_groceries)
                .into(binding.imageViewStore);

        shopInfoViewModel.setItems(shopIntroduceModel);
        //상단바
        setAppBar(((HomeActivity)getActivity()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //navigation setting
        navController = Navigation.findNavController(binding.getRoot());
        binding.taplayoutShopMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:{
                        Navigation.findNavController(binding.navShopFragment).navigate(R.id.shopIntroduceFragment);
                        break;
                    }
                    case 1:{
                        Navigation.findNavController(binding.navShopFragment).navigate(R.id.shopItemListFragment);
                        break;
                    }
                    case 2:{
                        Navigation.findNavController(binding.navShopFragment).navigate(R.id.shopEventFragment);
                        break;
                    }
                    case 3:{
                        Navigation.findNavController(binding.navShopFragment).navigate(R.id.shopReviewFragment);
                        break;
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        HomeActivity activity = (HomeActivity) getActivity();

        //상단바 설정
        activity.showTopAppBar();

        //하단바 숨기기
        activity.hideBottomNavigation();
        super.onResume();
    }


    public void setAppBar(HomeActivity activity){
        activity.getBinding().topAppBar.setTitle("매장 정보");
        activity.getBinding().topAppBar.setOnClickListener(null);
        activity.getBinding().topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.menu_call:{
                        if(shopInfoViewModel.getPhone() == null){
                            Toast.makeText(getContext(),"등록된 전화번호가 없습니다",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ shopInfoViewModel.getPhone().getValue())));
                        }
                        break;
                    }

                    //TODO : 채팅방 생성 (상단바) -> 고객 닉네임 필요
                    case R.id.menu_chat:{

                        if(((HomeActivity)getActivity()).getLoginToken() == null){
                            Snackbar.make(binding.getRoot(), "로그인 후에 이용해 주시기 바랍니다.", Snackbar.LENGTH_SHORT).show();
                        }
                        else if(!((HomeActivity)getActivity()).isFirebaseSetted()){
                            Snackbar.make(binding.getRoot(), "채팅 서버 인증 중입니다. 잠시만 기다려 주세요", Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            if(shopInfoViewModel.getShopId().getValue() != null
                                    && shopInfoViewModel.getOwnerId().getValue() != null
                                    && shopInfoViewModel.getShopName().getValue() != null){

                                long storeId = shopInfoViewModel.getShopId().getValue();
                                long ownerId = shopInfoViewModel.getOwnerId().getValue();
                                String storeName = shopInfoViewModel.getShopName().getValue();
                                String customerName = ((HomeActivity)getActivity()).getNickname();
                                String chatroomId = ((HomeActivity) getActivity()).makeChatroom(Long.toString(storeId), Long.toString(ownerId), storeName, customerName);

                                navController.navigate(NavGraphDirections.actionGlobalConversationFragment(storeName)
                                        .setChatroomId(chatroomId));
                            }
                        }
                        break;
                    }
                    case R.id.menu_report:{
                        if(shopInfoViewModel.getShopId().getValue() != null){
                            navController.navigate(ShopMainFragmentDirections.actionShopMainFragmentToShopReportFragment(shopInfoViewModel.getShopId().getValue()));
                        }
                        break;
                    }
                }

                return false;
            }
        });
        activity.invalidateOptionsMenu();
    }

    public void changeFavoriteState(boolean isFavorite){
        if(binding != null){
            if(isFavorite){
                binding.layoutFavorite.setVisibility(View.VISIBLE);
            }else{
                binding.layoutFavorite.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
