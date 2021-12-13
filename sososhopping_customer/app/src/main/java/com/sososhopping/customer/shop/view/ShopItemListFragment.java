package com.sososhopping.customer.shop.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.ShopItemListBinding;
import com.sososhopping.customer.shop.dto.ItemListDto;
import com.sososhopping.customer.shop.view.adapter.ShopItemAdapter;
import com.sososhopping.customer.shop.viewmodel.ShopInfoViewModel;
import com.sososhopping.customer.shop.viewmodel.ShopItemViewModel;

public class ShopItemListFragment extends Fragment {
    private NavController navController;
    private final ShopItemAdapter shopItemAdapter = new ShopItemAdapter();
    private ShopInfoViewModel shopInfoViewModel;
    private ShopItemViewModel shopItemViewModel;
    private ShopItemListBinding binding;

    public static ShopItemListFragment newInstance() {
        return new ShopItemListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ShopItemListBinding.inflate(inflater, container, false);

        //부모레벨
        shopInfoViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(ShopInfoViewModel.class);
        shopItemViewModel = new ViewModelProvider(getParentFragment().getParentFragment()).get(ShopItemViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);


        //상품은 Create에서 로딩
        int storeId = shopInfoViewModel.getShopId().getValue();
        shopItemViewModel.requestShopItem(storeId,
                this::onSuccess,
                this::onFailed,
                this::onNetworkError);
        binding.recyclerView.setAdapter(shopItemAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        shopItemAdapter.setOnItemClickListener(new ShopItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //해당 물품의 상세정보 확인
            }

            @Override
            public void onItemAdd(View v, int pos, int num) {

                int itemId = shopItemAdapter.getShopItemModelLists().get(pos).getItemId();

                //장바구니 담기 API
                shopItemViewModel.addCart(((HomeActivity) getActivity()).getLoginToken(),
                        itemId, num,
                        ShopItemListFragment.this::onSuccessAdd,
                        ShopItemListFragment.this::onDupAdd,
                        ShopItemListFragment.this::onFailed,
                        ShopItemListFragment.this::onNetworkError);

            }
        });

        binding.buttonToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).getBinding().bottomNavigation.setSelectedItemId(R.id.menu_cart);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            shopItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccess(ItemListDto itemListDto) {
        shopItemViewModel.getShopItem().setValue(itemListDto.getResults());
        if (binding != null) {
            shopItemAdapter.setShopItemModels(shopItemViewModel.getShopItem().getValue());
            shopItemAdapter.notifyDataSetChanged();
        }
    }

    private void onSuccessAdd() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.item_addCart_succ), Snackbar.LENGTH_SHORT).show();
    }

    private void onDupAdd() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.item_addCart_dup), Snackbar.LENGTH_SHORT).show();
    }

    private void onFailed() {
        Snackbar.make(((HomeActivity) getActivity()).getMainView(), getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkError() {
        NavHostFragment.findNavController(getParentFragment().getParentFragment()).navigate(R.id.action_global_networkErrorDialog);
        getActivity().onBackPressed();
    }
}
