package com.sososhopping.customer.mysoso.view;

import android.content.Intent;
import android.net.Uri;
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
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.databinding.MysosoPointDetailBinding;
import com.sososhopping.customer.mysoso.dto.PointDetailDto;
import com.sososhopping.customer.mysoso.model.PointDetailModel;
import com.sososhopping.customer.mysoso.viemodel.PointDetailViewModel;
import com.sososhopping.customer.mysoso.view.adapter.MysosoPointDetailAdapter;

import java.util.ArrayList;

public class MysosoPointDetailFragment extends Fragment {
    MysosoPointDetailBinding binding;
    NavController navConroller;

    private PointDetailViewModel pointDetailViewModel;
    private final RecyclerView[] dateList = new RecyclerView[3];
    private final MysosoPointDetailAdapter[] adapters = new MysosoPointDetailAdapter[3];

    public static MysosoPointDetailFragment newInstance() {
        return new MysosoPointDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //binding 설정
        binding = MysosoPointDetailBinding.inflate(inflater, container, false);

        dateList[0] = binding.recyclerViewPast;
        dateList[1] = binding.recyclerViewCur;
        dateList[2] = binding.recyclerViewFuture;

        for (int i = 0; i < 3; i++) {
            dateList[i].setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            adapters[i] = new MysosoPointDetailAdapter();
            dateList[i].setAdapter(adapters[i]);
        }

        //처음 2개는 안보게
        dateList[0].setVisibility(View.GONE);
        dateList[1].setVisibility(View.VISIBLE);
        dateList[2].setVisibility(View.GONE);

        //viewmodel 설정
        pointDetailViewModel = new ViewModelProvider(this).get(PointDetailViewModel.class);
        pointDetailViewModel.getStoreId().setValue(MysosoPointDetailFragmentArgs.fromBundle(getArguments()).getStoreId());
        initSetting();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Controller 설정
        navConroller = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curCounter = pointDetailViewModel.getCounter();
                int ftrCounter = pointDetailViewModel.addCounter(curCounter);
                pointDetailViewModel.setCounter(ftrCounter);

                //화면전환
                dateList[curCounter].setVisibility(View.GONE);
                dateList[ftrCounter].setVisibility(View.VISIBLE);
                binding.textView7.setText(pointDetailViewModel.returnDate(ftrCounter));

                //0 1(현재) 2 -> 1 2(현재) 0으로 변경 -> 0의 데이터를 새로 받아와야함
                pointDetailViewModel.requestPointDetailFuture(
                        ((HomeActivity) getActivity()).getLoginToken(),
                        true,
                        MysosoPointDetailFragment.this::onSuccess,
                        MysosoPointDetailFragment.this::onFailed,
                        MysosoPointDetailFragment.this::onNetworkError);
            }
        });

        binding.buttonPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curCounter = pointDetailViewModel.getCounter();
                int pastCounter = pointDetailViewModel.subCounter(curCounter);
                pointDetailViewModel.setCounter(pastCounter);

                //화면전환
                dateList[curCounter].setVisibility(View.GONE);
                dateList[pastCounter].setVisibility(View.VISIBLE);
                binding.textView7.setText(pointDetailViewModel.returnDate(pastCounter));

                //다음꺼 세팅
                pointDetailViewModel.requestPointDetailFuture(
                        ((HomeActivity) getActivity()).getLoginToken(),
                        false,
                        MysosoPointDetailFragment.this::onSuccess,
                        MysosoPointDetailFragment.this::onFailed,
                        MysosoPointDetailFragment.this::onNetworkError);
            }
        });

        binding.buttonShopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointDetailViewModel.getPhone().getValue() != null) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + pointDetailViewModel.getPhone().getValue())));
                }
            }
        });

        binding.buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navConroller.navigate(MysosoPointDetailFragmentDirections
                        .actionMysosoPointDetailFragmentToShopGraph(pointDetailViewModel.getStoreId().getValue()));
            }
        });


        binding.buttonShopChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResume() {
        //상단바
        ((HomeActivity) getActivity()).showTopAppBar();
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.mysoso_point_detail));
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitleCentered(true);
        //하단바
        ((HomeActivity) getActivity()).hideBottomNavigation();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSuccessInit(PointDetailDto dto, Integer index) {
        if (binding != null) {
            if (dto != null) {
                pointDetailViewModel.getPhone().setValue(dto.getPhone());
                pointDetailViewModel.getLocation().setValue(new Location(dto.getLat(), dto.getLng()));
                binding.textViewShopname.setText(dto.getStoreName());
            }
            onSuccess(dto, index);
            adapters[index].notifyDataSetChanged();
            binding.textView7.setText(pointDetailViewModel.returnDate(index));

        }
    }

    private void onSuccess(PointDetailDto dto, Integer index) {
        if (binding != null) {
            //리사이클러뷰 세팅
            if (dto != null) {
                pointDetailViewModel.getDetailList()[index].setValue(dto.getLogs());
                Log.e("왜 안왜", pointDetailViewModel.getCurDate()[index].getValue() + " " +dto.getLogs().size() + " ");
                adapters[index].setItems(pointDetailViewModel.getDetailList()[index].getValue());
            }
        }
    }

    private void onFailed() {
        if (binding != null) {
            Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                    getResources().getString(R.string.shop_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        if (binding != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
            getActivity().onBackPressed();

        }
    }

    public void initSetting() {
        pointDetailViewModel.requestPointDetail(
                ((HomeActivity) getActivity()).getLoginToken(),
                1,
                this::onSuccessInit,
                this::onFailed,
                this::onNetworkError);

        pointDetailViewModel.requestPointDetail(
                ((HomeActivity) getActivity()).getLoginToken(),
                0,
                this::onSuccess,
                this::onFailed,
                this::onNetworkError);

        pointDetailViewModel.requestPointDetail(
                ((HomeActivity) getActivity()).getLoginToken(),
                2,
                this::onSuccess,
                this::onFailed,
                this::onNetworkError);

        binding.textView7.setText(pointDetailViewModel.returnDate(1));
    }
}
