package com.sososhopping.customer.search.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.StringFormatMethod;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.databinding.SearchShopMapBinding;
import com.sososhopping.customer.search.HomeViewModel;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.shop.view.ShopMainFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class ShopMapFragment extends Fragment implements OnMapReadyCallback {
    private NavController navController;
    private SearchShopMapBinding binding;
    private HomeViewModel homeViewModel;
    private int navigateFrom;

    //????????? ?????? GPS
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private MapFragment mapFragment;
    private NaverMap naverMap;
    private InfoWindow infoWindow;

    MutableLiveData<ShopInfoShortModel> focusedShop;
    ArrayList<Marker> markers = new ArrayList<>();

    public static ShopMainFragment newInstance() {return new ShopMainFragment();}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        //????????????
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map);


        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this::onMapReady);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_search, menu);
        menu.findItem(R.id.menu_list).setVisible(true);
        menu.findItem(R.id.menu_map).setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        binding = SearchShopMapBinding.inflate(inflater,container,false);

        focusedShop = new MutableLiveData<>();
        focusedShop.observe(getViewLifecycleOwner(), new Observer<ShopInfoShortModel>() {
            @Override
            public void onChanged(ShopInfoShortModel shopInfoShortModel) {
                if(shopInfoShortModel == null){
                    binding.itemSearchMap.getRoot().setVisibility(View.GONE);
                }else{
                    bindShopItem(shopInfoShortModel);
                }
            }
        });

        homeViewModel.getShopList().observe(getViewLifecycleOwner(), new Observer<ArrayList<ShopInfoShortModel>>() {
            @Override
            public void onChanged(ArrayList<ShopInfoShortModel> shopInfoShortModels) {
                if(naverMap != null){
                    //?????? ?????????
                    addMarkers(naverMap);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        binding.buttonPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(homeViewModel.getNumberOfElement() > 0){
                    homeViewModel.search(
                            ((HomeActivity)getActivity()).getLoginToken(),
                            homeViewModel.getLocation(getContext()),
                            null,
                            null,
                            null,
                            ShopMapFragment.this::onSearchSuccess,
                            ShopMapFragment.this::onNetworkError);
                }
                else {
                    Snackbar.make(view, "?????? ?????? ?????????????????????.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        navigateFrom = ShopMapFragmentArgs.fromBundle(getArguments()).getNavigateFrom();
        this.naverMap = naverMap;

        //????????? ??????On
        naverMap.setLocationSource(locationSource);
        if(!locationSource.isActivated()){
            naverMap.setLocationTrackingMode(LocationTrackingMode.None);
        }
        else{
            naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);
        }

        Location location =  homeViewModel.getLocation(getContext());
        LatLng currentLocation = new LatLng(
                location.getLat(),
                location.getLng()
        );

        //?????? ??????
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setPosition(currentLocation);
        locationOverlay.setVisible(true);


        //?????? ????????? ??????
        binding.buttonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location =  homeViewModel.getLocation(getContext());
                LatLng currentLocation = new LatLng(
                        location.getLat(),
                        location.getLng()
                );
                //????????????
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation));
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        });


        //??? + ???????????? ??????
        naverMap.setMaxZoom(18.0);
        naverMap.setExtent(new LatLngBounds(new LatLng(31.43, 122.37),
                new LatLng(44.35, 132)));

        //?????? ????????? ??????
        switch (navigateFrom){
            //???
            //??????????????? ??? -> ???????????? ????????? ??????
            //??????????????? ??????
            case R.id.home2 :
            case R.id.shopListFragment:
            case R.id.searchDialogFragment:
            default:
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation));
                break;

            //???????????? ???
            case R.id.mysosoPointDetailFragment:
            case R.id.shopMainFragment:
                double shopLat = ShopMapFragmentArgs.fromBundle(getArguments()).getLat();
                double shopLng = ShopMapFragmentArgs.fromBundle(getArguments()).getLng();

                //???????????? ??????
                naverMap.moveCamera(CameraUpdate.scrollTo(new LatLng(shopLat, shopLng)));
                break;
        }
        naverMap.moveCamera(CameraUpdate.zoomTo(16));

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                focusedShop.postValue(null);
                infoWindow.close();
            }
        });

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);

        //?????? ??????
        infoWindow = new InfoWindow();
        addMarkers(naverMap);
    }

    @Override
    public void onResume() {
        //?????? ??????
        ((HomeActivity) getActivity()).hideBottomNavigation();
        ((HomeActivity) getActivity()).showTopAppBar();
        setAppBar(homeViewModel);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addMarkers(NaverMap naverMap){
        //?????? ?????? ??????
        int size = markers.size();
        for(int i=0; i<size; i++){
            markers.get(0).setMap(null);
            markers.remove(0);
        }

        addMarkersPage(naverMap,0);
    }

    //?????? ??? ??????
    public void addMarkersPage(NaverMap naverMap, int index){
        //??????????????? ???????????? ?????? ??????
        /*Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        };

        executor.execute(new Runnable() {
            @Override
            public void run() {
                createMarkers(index);
            }
        });*/

        createMarkers(index);
        int size = markers.size();
        for(int i=0; i<size; i++){
            markers.get(i).setMap(naverMap);
        }

        //???????????? ?????? ??????
        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            int size = markers.size();
            for(int i=0; i<size; i++){
                markers.get(i).setMap(naverMap);
            }
        });*/
    }

    public void createMarkers(int startIdx){
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return ((CustomTag)infoWindow.getMarker().getTag()).getName();
            }
        });

        ArrayList<ShopInfoShortModel> models = homeViewModel.getShopList().getValue();
        if(models != null){
            for(int i= startIdx; i<models.size(); i++){
                ShopInfoShortModel s = models.get(i);

                Marker m = new Marker();
                m.setPosition(new LatLng(s.getLocation().getLat(), s.getLocation().getLng()));
                m.setIcon(MarkerIcons.GREEN);
                m.setWidth(72);
                m.setHeight(108);
                m.setTag(new CustomTag(i, s.getName()));

                m.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        infoWindow.close();
                        Marker marker = (Marker) overlay;
                        int idx = ((CustomTag)marker.getTag()).getIdx();
                        focusedShop.postValue(models.get(idx));
                        infoWindow.open(marker);
                        return true;
                    }
                });
                markers.add(m);
            }
        }

    }

    public void bindShopItem(ShopInfoShortModel s){
        binding.itemSearchMap.textViewShopName.setText(s.getName());

        //????????????
        if(!s.isLocalCurrencyStatus()){
            binding.itemSearchMap.layoutLocalPay.setVisibility(View.GONE);
        }else{

            binding.itemSearchMap.layoutLocalPay.setVisibility(View.VISIBLE);
        }

        if(!s.isDeliveryStatus()){
            binding.itemSearchMap.layoutDelivery.setVisibility(View.GONE);
        }
        else{
            binding.itemSearchMap.layoutDelivery.setVisibility(View.VISIBLE);
        }

        //????????????
        binding.itemSearchMap.textViewRating.setText(StringFormatMethod.getRating(s.getScore()));
        binding.itemSearchMap.buttonMove.setText("?????? ???????????? (" + StringFormatMethod.getDistance(s.getDistance())+")");


        binding.itemSearchMap.buttonMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????
                navController.navigate(ShopMapFragmentDirections.actionShopMapFragmentToShopGraph(s.getStoreId()));
            }
        });

        //???????????? ?????? ??????
        int drawableImage = 0;
        if(s.isInterestStore()){
            drawableImage = R.drawable.ic_baseline_favorite_24;
        }
        else{
            drawableImage = R.drawable.ic_baseline_favorite_border_24;
        }
        //Draw New Heart
        Drawable wrapped = DrawableCompat.wrap(AppCompatResources.getDrawable(binding.getRoot().getContext(), drawableImage));
        DrawableCompat.setTint(wrapped, binding.getRoot().getResources().getColor(R.color.main_400));
        binding.itemSearchMap.imageButtonFavorite.setImageDrawable(wrapped);

        //TODO : ????????? ?????? (??????) -> ?????? ????????? ??????
        binding.itemSearchMap.buttonShopChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((HomeActivity)getActivity()).getLoginToken() == null){
                    Snackbar.make(binding.getRoot(), "????????? ?????? ????????? ????????? ????????????.", Snackbar.LENGTH_SHORT).show();
                }
                else if(!((HomeActivity)getActivity()).isFirebaseSetted()){
                    Snackbar.make(binding.getRoot(), "?????? ?????? ?????? ????????????. ????????? ????????? ?????????", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    //??????
                    long storeId = s.getStoreId();
                    long ownerId = s.getOwnerId();
                    String storeName = s.getName();
                    String customerName = ((HomeActivity)getActivity()).getNickname();

                    String chatroomId = ((HomeActivity) getActivity()).makeChatroom(Long.toString(storeId), Long.toString(ownerId), storeName, customerName);

                    //???????????? bundle ?????? ????????? ?????????
                    navController.navigate(NavGraphDirections.actionGlobalConversationFragment(storeName)
                            .setChatroomId(chatroomId));
                }

            }
        });

        binding.itemSearchMap.buttonShopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s.getPhone() == null){
                    Snackbar.make(binding.getRoot(), "????????? ??????????????? ????????????", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ s.getPhone())));
                }
            }
        });
        binding.itemSearchMap.getRoot().setVisibility(View.VISIBLE);
    }
    public void setAppBar(HomeViewModel homeViewModel){
        HomeActivity activity = (HomeActivity) getActivity();
        activity.getBinding().topAppBar.setTitle(homeViewModel.getSearchContent().getValue());
        activity.getBinding().topAppBar.setTitleCentered(false);
        activity.getBinding().topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????? dialog ?????????
                navController.navigate(ShopMapFragmentDirections.actionShopMapFragmentToSearchDialogFragment(R.id.shopMapFragment));
                return;
            }
        });
        activity.getBinding().topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_search :{

                        //?????? dialog ?????????
                        navController.navigate(ShopMapFragmentDirections.actionShopMapFragmentToSearchDialogFragment(R.id.shopMapFragment));
                        break;
                    }
                    case R.id.menu_map:{
                        //????????? navigate
                        break;
                    }

                    case R.id.menu_list:{
                        navController.navigate(ShopMapFragmentDirections.actionShopMapFragmentToShopListFragment());
                        break;
                    }
                }
                return false;
            }
        });
        activity.invalidateOptionsMenu();
    }

    private void onSearchSuccess(PageableShopListDto success, Integer navigate){

        homeViewModel.getShopList().getValue().addAll(success.getContent());

        if(binding != null){
            if(success.getNumberOfElements() > 0){
                //???????????? ??????
                addMarkersPage(naverMap, homeViewModel.getOffset());
            }
        }

        //?????? ?????????????????? ??????
        homeViewModel.setNumberOfElement(success.getNumberOfElements());
        homeViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());
    }

    private void onNetworkError(){
        if(binding != null){
            Snackbar.make(binding.getRoot(), "?????? ????????? ??? ??????????????? ??????????????????", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class CustomTag{
        int idx;
        String name;
    }

}