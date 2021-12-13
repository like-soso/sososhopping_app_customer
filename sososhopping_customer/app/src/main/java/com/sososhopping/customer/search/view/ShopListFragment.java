package com.sososhopping.customer.search.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.SearchShopListBinding;
import com.sososhopping.customer.search.HomeViewModel;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.search.view.adapter.ShopListAdapter;

import java.util.ArrayList;

public class ShopListFragment extends Fragment {


    private NavController navController;
    private final ShopListAdapter shopListAdapter = new ShopListAdapter();
    private HomeViewModel homeViewModel;
    public SearchShopListBinding binding;

    public static ShopListFragment newInstance() {return new ShopListFragment();}

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_search, menu);
        menu.findItem(R.id.menu_list).setVisible(false);
        menu.findItem(R.id.menu_map).setVisible(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){


        binding = SearchShopListBinding.inflate(inflater, container, false);

        //데이터 받아오기
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        //뷰 선언
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewShopList.setLayoutManager(layoutManager);

        //리스트 바뀔때마다 업로딩되게
        homeViewModel.getShopList().observe(this, new Observer<ArrayList<ShopInfoShortModel>>() {
            @Override
            public void onChanged(ArrayList<ShopInfoShortModel> shopInfoShortModels) {
                shopListAdapter.setShopLists(homeViewModel.getShopList().getValue());
                shopListAdapter.notifyDataSetChanged();


            }
        });

        shopListAdapter.setShopLists(homeViewModel.getShopList().getValue());
        binding.recyclerViewShopList.setAdapter(shopListAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //컨트롤러
        navController = Navigation.findNavController(binding.getRoot());
        shopListAdapter.setOnItemClickListener(new ShopListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //해당 매장을 검색조건으로 이동

                navController.navigate(ShopListFragmentDirections.actionShopListFragmentToShopGraph(
                        shopListAdapter.getShopLists().get(pos).getStoreId())
                        .setDistance(shopListAdapter.getShopLists().get(pos).getDistance()));
            }

            @Override
            public void onFavoriteClick(View v, int pos) {
                //여기선 아무일 x
            }
        });


        binding.recyclerViewShopList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    binding.progressCircular.setVisibility(View.VISIBLE);
                }

                else if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

                    if(homeViewModel.getNumberOfElement() > 0){
                        binding.progressCircular.setVisibility(View.VISIBLE);
                        homeViewModel.search(
                                ((HomeActivity)getActivity()).getLoginToken(),
                                homeViewModel.getLocation(getContext()),
                                null,
                                null,
                                null,
                                ShopListFragment.this::onSearchSuccess,
                                ShopListFragment.this::onNetworkError);
                    }
                }
                else{
                    binding.progressCircular.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        ((HomeActivity)getActivity()).showTopAppBar();
        setAppBar(homeViewModel);

        ((HomeActivity)getActivity()).showBottomNavigation();
        ((HomeActivity)getActivity()).getBinding().bottomNavigation.getMenu().findItem(R.id.menu_home).setChecked(true);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setAppBar(HomeViewModel homeViewModel){
        HomeActivity activity = (HomeActivity) getActivity();
        activity.getBinding().topAppBar.setTitle(homeViewModel.getSearchContent().getValue());
        activity.getBinding().topAppBar.setTitleCentered(false);
        activity.getBinding().topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색 dialog 띄우기
                navController.navigate(ShopListFragmentDirections.actionShopListFragmentToSearchDialogFragment(R.id.shopListFragment));
                return;
            }
        });
        activity.getBinding().topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_search :{
                        //검색 dialog 띄우기
                        navController.navigate(ShopListFragmentDirections.actionShopListFragmentToSearchDialogFragment(R.id.shopListFragment));
                        break;
                    }

                    case R.id.menu_map:{
                        //지도로 navigate
                        navController.navigate(ShopListFragmentDirections.actionShopListFragmentToShopMapFragment(R.id.shopListFragment));
                        break;
                    }
                    case R.id.menu_list:{
                        break;
                    }
                }
                return false;
            }
        });
        activity.invalidateOptionsMenu();
    }


    private void onSearchSuccess(PageableShopListDto success, Integer navigate){
        //몇개 추가되었는지
        homeViewModel.getShopList().getValue().addAll(success.getContent());

        if(binding != null){
            binding.progressCircular.setVisibility(View.GONE);

            if(success.getNumberOfElements() > 0){
                //추가된거 밑에 추가
                shopListAdapter.setShopLists(homeViewModel.getShopList().getValue());
                shopListAdapter.notifyItemRangeInserted(homeViewModel.getOffset(), success.getNumberOfElements());
            }
        }

        homeViewModel.setNumberOfElement(success.getNumberOfElements());
        homeViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());
    }

    private void onNetworkError(){
        if(binding != null){
            Snackbar.make(binding.getRoot(), "상점 정보를 더 불러오는데 실패했습니다", Snackbar.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.GONE);
        }

    }
}