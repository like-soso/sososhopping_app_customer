package com.sososhopping.customer.account.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.sososhopping.customer.HomeActivity;
import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.AccountSignUpStartBinding;

public class SignUpStartFragment extends Fragment {

    private NavController navController;
    AccountSignUpStartBinding binding;
    public static SignUpStartFragment newInstance() {
        return new SignUpStartFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AccountSignUpStartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        navController = Navigation.findNavController(view);

        binding.buttonSignUpStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpStartFragment_to_signUpIdFragment);
            }
        });

        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpStartFragment_to_logInDialogFragment);
            }
        });
    }

    @Override
    public void onResume() {
        ((HomeActivity)getActivity()).hideTopAppBar();
        ((HomeActivity)getActivity()).hideBottomNavigation();
        ((HomeActivity)getActivity()).hideLoginButton();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}