package com.sososhopping.customer.mysoso.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.MysosoPointBinding;
import com.sososhopping.customer.mysoso.dto.PointListDto;
import com.sososhopping.customer.mysoso.viemodel.PointInfoViewModel;
import com.sososhopping.customer.mysoso.view.adapter.MysosoPointAdapter;

public class MysosoPointFragment extends Fragment {

    MysosoPointBinding binding;
    NavController navConroller;
    PointInfoViewModel pointInfoViewModel;

    private final MysosoPointAdapter adapterFavorite = new MysosoPointAdapter();
    private final MysosoPointAdapter adapterNotFavorite = new MysosoPointAdapter();

    public static MysosoPointFragment newInstance() {
        return new MysosoPointFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //메뉴 변경 확인
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_top_none, menu);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //binding 설정
        binding = MysosoPointBinding.inflate(inflater, container, false);

        //viewmodel 설정
        pointInfoViewModel = new PointInfoViewModel();
        pointInfoViewModel.requestPointList(((HomeActivity) getActivity()).getLoginToken(),
                this::onSuccess,
                this::onFailedLogIn,
                this::onFailed,
                this::onNetworkError);

        LinearLayoutManager layoutManagerFavorite = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        LinearLayoutManager layoutManagerNotFavorite = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerViewPointFavorite.setLayoutManager(layoutManagerFavorite);
        binding.recyclerViewPointNotFavorite.setLayoutManager(layoutManagerNotFavorite);

        binding.recyclerViewPointFavorite.setAdapter(adapterFavorite);
        binding.recyclerViewPointNotFavorite.setAdapter(adapterNotFavorite);

        binding.imageButtonFavoriteArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내리기
                binding.imageButtonFavoriteArrowDown.setVisibility(View.GONE);
                binding.imageButtonFavoriteArrowUp.setVisibility(View.VISIBLE);
                binding.recyclerViewPointFavorite.setVisibility(View.GONE);
            }
        });

        binding.imageButtonFavoriteArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //올리기
                binding.imageButtonFavoriteArrowDown.setVisibility(View.VISIBLE);
                binding.imageButtonFavoriteArrowUp.setVisibility(View.GONE);
                binding.recyclerViewPointFavorite.setVisibility(View.VISIBLE);
            }
        });

        binding.imageButtonNotFavoriteArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //내리기
                binding.imageButtonNotFavoriteArrowDown.setVisibility(View.GONE);
                binding.imageButtonNotFavoriteArrowUp.setVisibility(View.VISIBLE);
                binding.recyclerViewPointNotFavorite.setVisibility(View.GONE);
            }
        });

        binding.imageButtonNotFavoriteArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //올리기
                binding.imageButtonNotFavoriteArrowDown.setVisibility(View.VISIBLE);
                binding.imageButtonNotFavoriteArrowUp.setVisibility(View.GONE);
                binding.recyclerViewPointNotFavorite.setVisibility(View.VISIBLE);
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Controller 설정
        navConroller = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceState);

        adapterFavorite.setOnItemClickListener(new MysosoPointAdapter.OnItemClickListenerBoard() {
            @Override
            public void onItemClick(int storeId) {
                //이동
                navConroller.navigate(MysosoPointFragmentDirections.actionMysosoPointFragmentToMysosoPointDetailFragment(storeId));
            }
        });

        adapterNotFavorite.setOnItemClickListener(new MysosoPointAdapter.OnItemClickListenerBoard() {
            @Override
            public void onItemClick(int storeId) {
                //이동
                navConroller.navigate(MysosoPointFragmentDirections.actionMysosoPointFragmentToMysosoPointDetailFragment(storeId));
            }
        });

    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity) getActivity()).showTopAppBar();
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.mysoso_point));
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitleCentered(true);
        //하단바
        ((HomeActivity) getActivity()).showBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onSuccess(PointListDto dto) {
        if (binding != null) {
            if (dto != null) {
                adapterFavorite.setItems(dto.getPointListFavorite());
                adapterNotFavorite.setItems(dto.getPointListNotFavorite());

                adapterFavorite.notifyDataSetChanged();
                adapterNotFavorite.notifyDataSetChanged();
            }
        }
    }

    private void onFailedLogIn() {
        if (binding != null) {
            NavHostFragment.findNavController(this)
                    .navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));

        }
    }

    private void onFailed() {
        if(binding != null){
            Snackbar.make(((HomeActivity)getActivity()).getMainView(), getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        if(binding != null){
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
        }
    }

}