package com.sososhopping.customer.interest.view;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sososhopping.customer.MainActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.gps.CalculateDistance;
import com.sososhopping.customer.common.gps.GPSTracker;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.databinding.InterestShopListBinding;
import com.sososhopping.customer.interest.viewmodel.InterestViewModel;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.search.view.adapter.ShopListAdapter;
import com.sososhopping.customer.shop.view.ShopMainFragment;

import org.jetbrains.annotations.Nullable;

import lombok.SneakyThrows;

public class InterestShopListFragment extends Fragment {

    private InterestShopListBinding binding;
    private InterestViewModel interestViewModel;
    private NavController navController;

    int clickedPos = -1;


    //shoplist와 동일 사용
    private ShopListAdapter shopListAdapter = new ShopListAdapter();

    public static InterestShopListFragment newInstance() {return new InterestShopListFragment();}

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
        binding = InterestShopListBinding.inflate(inflater,container,false);

        //Viewmodel 설정
        interestViewModel = new ViewModelProvider(getActivity()).get(InterestViewModel.class);

        //뷰 선언
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewShopList.setLayoutManager(layoutManager);
        if(interestViewModel.getFavoriteList().getValue() != null){
            shopListAdapter.setShopLists(interestViewModel.getFavoriteList().getValue());
        }
        binding.recyclerViewShopList.setAdapter(shopListAdapter);

        //검색
        interestViewModel.requestInterest(
                ((MainActivity)getActivity()).getLoginToken(),
                this::onSearchSuccessed,
                this::onFailedLogIn,
                this::onNetworkError
        );

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Controller 설정
        navController = Navigation.findNavController(view);
        shopListAdapter.setOnItemClickListener(new ShopListAdapter.OnItemClickListener() {
            @SneakyThrows
            @Override
            public void onItemClick(View v, int pos) {
                //검색조건으로 이동
                navController.navigate(InterestShopListFragmentDirections.actionInterestShopListFragmentToShopGraph(shopListAdapter.getShopLists().get(pos).getStoreId())
                .setDistance(shopListAdapter.getShopLists().get(pos).getDistance()));
            }

            @Override
            public void onFavoriteClick(View v, int pos) {
                clickedPos = pos;

                //즐겨찾기 해제
                interestViewModel.changeInterest(
                        ((MainActivity) getActivity()).getLoginToken(),
                        shopListAdapter.getShopLists().get(pos).getStoreId(),
                        InterestShopListFragment.this::onSuccessFavoriteChange,
                        InterestShopListFragment.this::onFailedLogIn,
                        InterestShopListFragment.this::onFailed,
                        InterestShopListFragment.this::onNetworkError);
            }
        });
    }

    @Override
    public void onResume() {
        //상단바
        ((MainActivity)getActivity()).showTopAppBar();
        ((MainActivity)getActivity()).getBinding().topAppBar.setTitle("관심가게");

        //하단바
        ((MainActivity)getActivity()).showBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSearchSuccessed(ShopListDto success){
        interestViewModel.getFavoriteList().setValue(success.getShopInfoShortModels());

        GPSTracker gpsTracker = GPSTracker.getInstance(getContext());
        Location me = new Location(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        for(ShopInfoShortModel s : success.getShopInfoShortModels()){
            if(s.getLocation().getLat() == 0 && s.getLocation().getLng() == 0){
                s.setDistance(-1);
            }
            else{
                s.setDistance(CalculateDistance.distance(me,s.getLocation()));
            }
        }


        shopListAdapter.setShopLists(success.getShopInfoShortModels());
        shopListAdapter.notifyDataSetChanged();
    }

    private void onFailedLogIn(){
        navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));
    }

    private void onNetworkError() {
        navController.navigate(R.id.action_global_networkErrorDialog);
    }


    private void onSuccessFavoriteChange(){
        if(clickedPos != -1){
            //화면상의 표시 변경
            ShopInfoShortModel shopInfoShortModel = shopListAdapter.getShopLists().get(clickedPos);
            shopInfoShortModel.setInterestStore(!shopInfoShortModel.isInterestStore());
            shopListAdapter.notifyItemChanged(clickedPos);
        }
    }

    private void onFailed() {
        clickedPos = -1;
        Toast.makeText(getContext(),getResources().getString(R.string.shop_error), Toast.LENGTH_LONG).show();
    }

}
