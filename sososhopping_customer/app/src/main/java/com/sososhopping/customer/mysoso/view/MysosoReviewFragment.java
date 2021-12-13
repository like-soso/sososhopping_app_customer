package com.sososhopping.customer.mysoso.view;

import android.content.DialogInterface;
import android.graphics.Canvas;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.NavGraphDirections;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.MysosoMyreviewBinding;
import com.sososhopping.customer.mysoso.dto.MyReviewsDto;
import com.sososhopping.customer.mysoso.viemodel.MyReviewViewModel;
import com.sososhopping.customer.mysoso.view.adapter.MysosoReviewAdapter;
import com.sososhopping.customer.mysoso.view.adapter.SwipeController;
import com.sososhopping.customer.mysoso.view.adapter.SwipeControllerActions;

public class MysosoReviewFragment extends Fragment {
    MysosoMyreviewBinding binding;
    NavController navConroller;

    private final MysosoReviewAdapter mysosoReviewAdapter = new MysosoReviewAdapter();
    private MyReviewViewModel myReviewViewModel;

    public static MysosoReviewFragment newInstance() {
        return new MysosoReviewFragment();
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
        binding = MysosoMyreviewBinding.inflate(inflater, container, false);
        setupRecyclerView();

        //viewmodel 설정
        myReviewViewModel = new MyReviewViewModel();
        myReviewViewModel.requestMyReview(((HomeActivity) getActivity()).getLoginToken(),
                this::onSuccess,
                this::onFailedLogIn,
                this::onFailedGet,
                this::onNetworkError);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Controller 설정
        navConroller = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onResume() {
        //상단바
        ((HomeActivity) getActivity()).showTopAppBar();
        ((HomeActivity) getActivity()).getBinding().topAppBar.setTitle(getResources().getString(R.string.mysoso_myRating));

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

    public void onSuccess(MyReviewsDto dto) {
        if (binding != null) {
            if (dto != null) {
                mysosoReviewAdapter.setReviewModels(dto.getMyreviews());
                mysosoReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void onFailedLogIn() {
        if (binding != null) {
            NavHostFragment.findNavController(this)
                    .navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_token));
        }
    }

    private void onFailedGet() {
        if (binding != null) {
            Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                    getResources().getString(R.string.mysoso_myRating_get_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onFailed() {
        if (binding != null) {
            Snackbar.make(((HomeActivity) getActivity()).getMainView(),
                    getResources().getString(R.string.mysoso_myRating_delte_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onNetworkError() {
        if (binding != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
            getActivity().onBackPressed();

        }
    }

    public void onSuccess(int pos) {
        if (binding != null) {
            if (pos != RecyclerView.NO_POSITION) {
                mysosoReviewAdapter.getReviewModels().remove(pos);
                mysosoReviewAdapter.notifyItemRemoved(pos);
            }
        }
        Snackbar.make(((HomeActivity)getActivity()).getMainView()
                , getResources().getString(R.string.mysoso_myRating_delte_success),Snackbar.LENGTH_SHORT).show();

    }

    private void onFailedDelete() {
        Snackbar.make(((HomeActivity)getActivity()).getMainView(), getResources().getString(R.string.shop_error),
                Snackbar.LENGTH_SHORT).show();
    }

    private void onNetworkErrorDelete() {
        if(binding != null){
            NavHostFragment.findNavController(this).navigate(R.id.action_global_networkErrorDialog);
        }
    }

    private void setupRecyclerView() {

        binding.recyclerViewReview.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        );
        binding.recyclerViewReview.setAdapter(mysosoReviewAdapter);

        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int pos) {

                navConroller.navigate(MysosoReviewFragmentDirections.actionMysosoReviewFragmentToShopGraph(
                        mysosoReviewAdapter.getReviewModels().get(pos).getStoreId()
                ));
            }

            @Override
            public void onRightClicked(int pos) {
                //안전을 위해서 다이얼로그 추가
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("정말 삭제하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myReviewViewModel.deleteMyReview(
                                        ((HomeActivity) getActivity()).getLoginToken(),
                                        mysosoReviewAdapter.getReviewModels().get(pos).getStoreId(),
                                        pos,
                                        MysosoReviewFragment.this::onSuccess,
                                        MysosoReviewFragment.this::onFailedDelete,
                                        MysosoReviewFragment.this::onNetworkErrorDelete);
                            }
                        })
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }

            @Override
            public void onReset(int pos) {
                mysosoReviewAdapter.notifyItemChanged(pos);
            }
        }, getResources());
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(binding.recyclerViewReview);

        binding.recyclerViewReview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }
}
