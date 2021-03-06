package com.sososhopping.customer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sososhopping.customer.account.dto.LogInResponseDto;
import com.sososhopping.customer.account.viewmodel.LogInViewModel;
import com.sososhopping.customer.chat.ChatroomInfor;
import com.sososhopping.customer.chat.ChatroomUsers;
import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.gps.GPSTracker;
import com.sososhopping.customer.common.sharedpreferences.SharedPreferenceManager;
import com.sososhopping.customer.databinding.ActivityMainBinding;
import com.sososhopping.customer.mysoso.model.MyInfoModel;
import com.sososhopping.customer.mysoso.viemodel.MyInfoViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class HomeActivity extends AppCompatActivity {

    public NavController navController;
    private NavHostFragment navHostFragment;
    private ActivityMainBinding binding;

    //????????????
    Boolean isLogIn = false;
    MutableLiveData<String> loginToken = new MutableLiveData<>();
    MutableLiveData<String> nickName = new MutableLiveData<>();


    //?????????????????? ??????
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseDatabase firebaseDatabase;
    public FirebaseUser user;
    public DatabaseReference ref;
    public Task<AuthResult> authResultTask;
    public String firebaseToken;
    public boolean afterLogin = false;

    public ActivityMainBinding getBinding() {
        return binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GPSTracker gpsTracker = GPSTracker.getInstance(getApplicationContext());
        if (!gpsTracker.canGetLocation()) {
            Snackbar.make(findViewById(android.R.id.content), "?????? ??????????????? ????????????, ?????? ????????? ????????? ?????? ?????????.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }

        //??????????????? ??????
        autoLogIn();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        loginToken.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                initLoginButton();

                if (s != null) {
                    //?????? ????????? ????????????
                    new MyInfoViewModel().requestMyInfo(
                            s,
                            new Consumer<MyInfoModel>() {
                                @Override
                                public void accept(MyInfoModel myInfoModel) {
                                    HomeActivity.this.nickName.setValue(myInfoModel.getNickname());
                                    Snackbar.make(binding.getRoot(),
                                            getResources().getString(R.string.login_success) + " " + myInfoModel.getNickname() + "???",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            },
                            HomeActivity.this::onLoginFailed,
                            HomeActivity.this::onLoginFailed,
                            HomeActivity.this::onLoginFailed
                    );
                }

            }
        });

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();


        //????????? -> ?????? ??????????????? ????????????
        binding.bottomNavigation.getMenu().findItem(R.id.menu_home).setChecked(true);
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_home: {
                        getViewModelStore().clear();
                        binding.bottomNavigation.getMenu().findItem(R.id.menu_home).setChecked(true);
                        navController.navigate(R.id.home2, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        break;
                    }
                    case R.id.menu_chat: {
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            getViewModelStore().clear();
                            if (user != null) {
                                binding.bottomNavigation.getMenu().findItem(R.id.menu_chat).setChecked(true);
                                navController.navigate(R.id.chatFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "?????? ?????? ?????? ????????????.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }

                    case R.id.menu_interest: {
                        //????????? ????????? ?????????
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            binding.bottomNavigation.getMenu().findItem(R.id.menu_interest).setChecked(true);
                            navController.navigate(R.id.interestShopListFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        }
                        break;
                    }

                    case R.id.menu_cart: {
                        //????????? ????????? ?????????
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            getViewModelStore().clear();
                            binding.bottomNavigation.getMenu().findItem(R.id.menu_cart).setChecked(true);
                            navController.navigate(R.id.cartMainFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        }
                        break;
                    }

                    case R.id.menu_mysoso: {

                        //????????? ????????? ?????????
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            getViewModelStore().clear();
                            binding.bottomNavigation.getMenu().findItem(R.id.menu_mysoso).setChecked(true);
                            navController.navigate(R.id.mysosoMainFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        }
                        break;
                    }
                }
                return true;
            }
        });

        //???????????? ?????????
        binding.bottomNavigation.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home: {
                        getViewModelStore().clear();
                        navController.navigate(R.id.home2, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        break;
                    }
                    case R.id.menu_chat: {
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            getViewModelStore().clear();
                            if (user != null) {
                                binding.bottomNavigation.getMenu().findItem(R.id.menu_chat).setChecked(true);
                                navController.navigate(R.id.chatFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "?????? ?????? ?????? ????????????.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }

                    case R.id.menu_interest: {
                        break;
                    }

                    //?????? ?????? x
                    case R.id.menu_cart: {
                        break;
                    }

                    case R.id.menu_mysoso: {

                        //????????? ????????? ?????????
                        if(loginToken.getValue() == null){
                            bottomItemClicked(R.id.home2);
                            navController.navigate(NavGraphDirections.actionGlobalLogInRequiredDialog().setErrorMsgId(R.string.login_error_description));
                        }
                        else{
                            getViewModelStore().clear();
                            navController.navigate(R.id.mysosoMainFragment, null, new NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build());
                        }
                    }
                }
            }
        });

        //??? ????????? ?????????????????? ??? ?????? ????????? ???????????? ?????? ??????
        initLoginButton();
        binding.buttonAccountLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_global_navigation_login);
            }
        });

        //?????????
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getAppKeyHash();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        int start = Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();

        if (start == R.id.home2) {
            Snackbar.make(binding.getRoot(), "?????????????????????????", Snackbar.LENGTH_SHORT)
                    .setAction("???", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
        }
        else if (start == R.id.conversationFragment) {
            binding.bottomNavigation.setSelectedItemId(R.id.menu_chat);
        }
        else if (start == R.id.shopListFragment){
            binding.bottomNavigation.setSelectedItemId(R.id.menu_home);
        }
        else if (navHostFragment.getChildFragmentManager().getBackStackEntryCount() < 1) {
            binding.bottomNavigation.setSelectedItemId(R.id.menu_home);
        }
        else {
            super.onBackPressed();
        }
    }


    private void autoLogIn() {
        String id = SharedPreferenceManager.getString(getApplicationContext(), Constant.SHARED_PREFERENCE_KEY_ID);
        String password = SharedPreferenceManager.getString(getApplicationContext(), Constant.SHARED_PREFERENCE_KEY_PASSWORD);

        if (id != null && password != null) {
            LogInViewModel logInViewModel = new LogInViewModel();
            logInViewModel.autoLogin(id, password, this::onLoggedIn, this::onLoginFailed);
        }
    }

    private void onLoginFailed() {
        SharedPreferenceManager.deleteString(getApplicationContext(), Constant.SHARED_PREFERENCE_KEY_ID);
        SharedPreferenceManager.deleteString(getApplicationContext(), Constant.SHARED_PREFERENCE_KEY_PASSWORD);
    }


    private void onLoggedIn(LogInResponseDto responseDto) {
        //??????
        setLoginToken(responseDto.getToken());
        this.setIsLogIn(true);
        afterLoginSuccessFirebaseInit(responseDto.getFirebaseToken());
        initLoginButton();
    }

    public void initLoginButton() {
        if (isLogIn) {
            binding.buttonAccountLogIn.setVisibility(View.GONE);
            binding.bottomNavigation.setClickable(true);
        } else {
            binding.buttonAccountLogIn.setVisibility(View.VISIBLE);
            binding.bottomNavigation.setClickable(false);
        }
    }

    public void hideLoginButton() {
        binding.buttonAccountLogIn.setVisibility(View.GONE);
    }

    public void showBottomNavigation() {
        binding.bottomNavigation.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        binding.bottomNavigation.setVisibility(View.GONE);
    }

    public void showTopAppBar() {
        binding.topAppBar.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTopAppBarHome(String title) {
        binding.topAppBar.setTitle(title);
        binding.topAppBar.setOnClickListener(null);
        binding.topAppBar.setTitleCentered(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void setTopAppBarNotHome(boolean b) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
    }

    public void hideTopAppBar() {
        binding.topAppBar.setVisibility(View.GONE);
    }

    public String getLoginToken() {
        return loginToken.getValue();
    }

    public String getNickname() {
        if (nickName.getValue() != null) {
            return nickName.getValue();
        }
        return "";
    }

    public void setLoginToken(String loginToken) {
        this.loginToken.setValue(loginToken);
    }

    public void setIsLogIn(boolean is) {
        this.isLogIn = is;
    }

    public void setTopAppBarTitle(String title) {
        binding.topAppBar.setTitle(title);
    }

    public void bottomItemClicked(int id) {
        binding.bottomNavigation.setSelectedItemId(id);
    }

    public View getMainView() {
        if (binding != null) {
            return binding.getRoot();
        }
        return null;
    }

    public boolean isFirebaseSetted() {
        return user != null;
    }

    //?????? ?????? ????????? firebase ?????????
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuthentication();
    }

    //?????? ????????? ???????????? ?????? ???????????? ??????
    @Override
    protected void onStop() {
        super.onStop();

        //???????????? ??????
        if (afterLogin == true) {
            ref.child("User").child(this.user.getUid()).child("connection").setValue(false);
            ref.child("User").child(this.user.getUid()).child("lastOnline").setValue(ServerValue.TIMESTAMP);
            mAuth.signOut();
        }
    }

    //??????, ?????? ????????? ?????? ?????? ?????????????????? ?????????
    public void afterLoginSuccessFirebaseInit(String firebaseToken) {
        this.firebaseToken = firebaseToken;

        afterLogin = true;
        authResultTask = mAuth.signInWithCustomToken(firebaseToken)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        user = authResult.getUser();
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        ref = firebaseDatabase.getReference();

                        //FcmId ??????
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        ref.child("FcmId").child(user.getUid()).setValue(task.getResult());
                                    }
                                });

                        setOnline();
                    }
                });
    }

    //?????????????????? ???????????? ?????? (+ ????????? ??????)
    public void firebaseAuthentication() {
        if (afterLogin == true) {
            user = mAuth.getCurrentUser();
            if (user == null) {
                authResultTask = mAuth.signInWithCustomToken(firebaseToken)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user = authResult.getUser();
                                setOnline();
                            }
                        });
            }
        }
    }

    private void setOnline() {
        ref.child("User").child(user.getUid()).child("connection").setValue(true);
    }

    //????????? ?????? TODO : ?????? ?????? ????????? ??????
    public String makeChatroom(String storeId, String ownerId, String storeName, String customerName) {
        String userUid = user.getUid();
        String ownerUid = "O" + ownerId;
        String chatRoomId = storeId + "@" + ownerUid + "@" + userUid;

        ref.child("ChatroomUsers").child(chatRoomId).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        ChatroomInfor chatRoomInfor = null;

                        if (task.isSuccessful()) {
                            chatRoomInfor = task.getResult().getValue(ChatroomInfor.class);
                        }

                        if (chatRoomInfor == null) {
                            chatRoomInfor = new ChatroomInfor(customerName, storeName, chatRoomId);

                            ref.child("ChatroomInfor")
                                    .child(userUid)
                                    .child(chatRoomId)
                                    .setValue(chatRoomInfor);

                            ref.child("ChatroomInfor")
                                    .child(storeId)
                                    .child(chatRoomId)
                                    .setValue(chatRoomInfor);

                            ChatroomUsers chatRoomUserInfor = new ChatroomUsers(userUid, ownerUid);
                            ref.child("ChatroomUsers")
                                    .child(chatRoomId)
                                    .setValue(chatRoomUserInfor);
                        }
                    }
                });

        return chatRoomId;
    }
}