package com.sososhopping.customer.mysoso.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.databinding.MysosoOrderListBinding;
import com.sososhopping.customer.mysoso.dto.OrderListDto;
import com.sososhopping.customer.mysoso.dto.PageableOrderListDto;
import com.sososhopping.customer.mysoso.model.OrderRecordShortModel;
import com.sososhopping.customer.mysoso.viemodel.OrderListViewModel;
import com.sososhopping.customer.mysoso.view.adapter.MysosoOrderListAdapter;
import com.sososhopping.customer.search.view.ShopListFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MysosoOrderListFragment extends Fragment {

    private MysosoOrderListBinding binding;
    private NavController navController;
    private OrderListViewModel orderListViewModel;

    MysosoOrderListAdapter orderListAdapter;

    private final OrderStatus INITIAL_ORDERSTAT = OrderStatus.PENDING;
    ArrayAdapter<OrderStatus> adapterApprove;
    ArrayAdapter<OrderStatus> adapterCancel;
    private final OrderStatus[] searchTypeApprove = {OrderStatus.APPROVE_ALL, OrderStatus.APPROVE, OrderStatus.READY};
    private final OrderStatus[] searchTypeCancel = {OrderStatus.CANCEL_ALL, OrderStatus.REJECT, OrderStatus.CANCEL};
    private OrderStatus beforeChecked = OrderStatus.PENDING;

    private int beforeTab = -1;

    public static MysosoOrderListFragment newInstance() {return new MysosoOrderListFragment();}

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
        binding = MysosoOrderListBinding.inflate(inflater, container, false);

        //viewmodel 설정
        orderListViewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        orderListAdapter = new MysosoOrderListAdapter(orderListViewModel.getOrderList().getValue());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(orderListAdapter);

        orderListViewModel.getOrderList().observe(this, new Observer<ArrayList<OrderRecordShortModel>>() {
            @Override
            public void onChanged(ArrayList<OrderRecordShortModel> orderRecordShortModels) {
                orderListAdapter.setItems(orderRecordShortModels);
                orderListAdapter.notifyDataSetChanged();
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    binding.progressCircular.setVisibility(View.VISIBLE);
                }

                else if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

                    if(orderListViewModel.getNumberOfElement() > 0){
                        binding.progressCircular.setVisibility(View.VISIBLE);

                        orderListViewModel.requestMyOrderListsPage(
                                ((HomeActivity)getActivity()).getLoginToken(),
                                beforeChecked,
                                null,
                                MysosoOrderListFragment.this::onSuccess,
                                MysosoOrderListFragment.this::onFailedLogIn,
                                MysosoOrderListFragment.this::onFailed,
                                MysosoOrderListFragment.this::onNetworkError
                        );
                    }
                }
                else{
                    binding.progressCircular.setVisibility(View.GONE);
                }
            }
        });

        //초기 검색
        orderListViewModel.requestMyOrderListsPage(
                ((HomeActivity)getActivity()).getLoginToken(),
                INITIAL_ORDERSTAT,
                null,
                MysosoOrderListFragment.this::onSuccess,
                MysosoOrderListFragment.this::onFailedLogIn,
                MysosoOrderListFragment.this::onFailed,
                MysosoOrderListFragment.this::onNetworkError
        );

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity)getActivity()).showTopAppBar();
        ((HomeActivity)getActivity()).setTopAppBarTitle("주문내역");

        //하단바
        ((HomeActivity)getActivity()).showBottomNavigation();
        ((HomeActivity)getActivity()).getBinding().bottomNavigation.getMenu().findItem(R.id.menu_mysoso).setChecked(true);
        Log.e("beforeT", beforeTab+"");

        if(beforeTab != -1){
            beforeChecked = OrderStatus.DONE;
            binding.taplayoutPurchaseStatus.selectTab(binding.taplayoutPurchaseStatus.getTabAt(beforeTab));
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        adapterApprove = new ArrayAdapter<OrderStatus>(getContext(), android.R.layout.simple_spinner_item, searchTypeApprove);
        adapterApprove.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCancel = new ArrayAdapter<OrderStatus>(getContext(), android.R.layout.simple_spinner_item, searchTypeCancel);
        adapterCancel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너
        binding.spinnerSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OrderStatus od = (OrderStatus)adapterView.getSelectedItem();

                if(od != beforeChecked){
                    //재검색
                    getListCall(od);
                }
                beforeChecked = od;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //탭레이아웃
        binding.taplayoutPurchaseStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tab selected?", tab.getPosition()+"");
                switch (tab.getPosition()){
                    case 0:{
                        getListCall(OrderStatus.PENDING);
                        binding.linearLayoutSpinner.setVisibility(View.GONE);
                        beforeChecked = OrderStatus.PENDING;
                        break;
                    }
                    case 1:{
                        //스피너 설정 필요
                        binding.spinnerSearchType.setAdapter(adapterApprove);

                        //초기 설정
                        binding.spinnerSearchType.setSelection(0);
                        binding.linearLayoutSpinner.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 2:{
                        //스피너 설정 필요
                        binding.spinnerSearchType.setAdapter(adapterCancel);

                        //초기 설정
                        binding.spinnerSearchType.setSelection(0);
                        binding.linearLayoutSpinner.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 3:{
                        getListCall(OrderStatus.DONE);
                        binding.linearLayoutSpinner.setVisibility(View.GONE);
                        beforeChecked = OrderStatus.DONE;
                        break;
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.e("tab reselected?", tab.getPosition()+"");
                switch (tab.getPosition()){
                    case 0:{
                        //새로고침되게
                        getListCall(OrderStatus.PENDING);
                        break;
                    }
                    case 1:
                    case 2:
                        //새로고침되게
                        beforeChecked = OrderStatus.PENDING;
                        binding.spinnerSearchType.setSelection(0);
                        break;
                    case 3:{
                        getListCall(OrderStatus.DONE);
                        break;
                    }
                }
            }
        });
        orderListAdapter.setOnItemClickListener(new MysosoOrderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long orderId) {
                //세부정보로 이동
                beforeTab = binding.taplayoutPurchaseStatus.getSelectedTabPosition();
                navController.navigate(MysosoOrderListFragmentDirections.actionMysosoOrderListMainFragmentToMysosoOrderDetailFragment(orderId));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getListCall(OrderStatus orderStatus){
        //페이징 리셋하고
        orderListViewModel.resetPage();

        //재검색
        orderListViewModel.requestMyOrderListsPage(
                ((HomeActivity)getActivity()).getLoginToken(),
                orderStatus,
                0,
                MysosoOrderListFragment.this::onSuccess,
                MysosoOrderListFragment.this::onFailedLogIn,
                MysosoOrderListFragment.this::onFailed,
                MysosoOrderListFragment.this::onNetworkError
        );
    }

    //재검색 결과 / 스크롤 결과 구분해야함
    public void onSuccess(PageableOrderListDto success){
        if(binding != null){
            binding.progressCircular.setVisibility(View.GONE);

            //최초
            if(orderListViewModel.getOffset() == 0){
                orderListViewModel.getOrderList().getValue().clear();
                orderListViewModel.getOrderList().setValue(success.getContent());
            }
            //스크롤
            else{
                if(success.getNumberOfElements() > 0){
                    orderListViewModel.getOrderList().getValue().addAll(success.getContent());
                    orderListAdapter.setItems(orderListViewModel.getOrderList().getValue());
                    orderListAdapter.notifyItemRangeInserted(orderListViewModel.getOffset(), success.getNumberOfElements());
                }
            }
            orderListViewModel.setNumberOfElement(success.getNumberOfElements());
            orderListViewModel.setOffset(success.getPageable().getOffset() + success.getNumberOfElements());
        }
    }

    private void onFailedLogIn(){
        if(binding != null){
            NavHostFragment.findNavController(this)
                    .navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));
        }
    }

    private void onFailed() {
        if(binding != null){
            Snackbar.make(((HomeActivity)getActivity()).getMainView(),
                    getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        if(binding != null){
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
            getActivity().onBackPressed();
        }
    }


}
