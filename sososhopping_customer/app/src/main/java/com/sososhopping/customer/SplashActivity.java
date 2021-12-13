package com.sososhopping.customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.sososhopping.customer.common.gps.GPSTracker;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    //권한용
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //권한 요청
        getPermission();

    }

    public void getPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //포그라운드 위치 권한 확인
            Snackbar.make(findViewById(android.R.id.content), "앱을 사용하기 위해서는 위치정보 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //위치 권한 요청
                            ActivityCompat.requestPermissions(SplashActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();
        }
        else{
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    //권한없으면 꺼버리기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                return;
            }

            for(String s : permissions){
                //다시보지 않기
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,s)){
                    Snackbar.make(findViewById(android.R.id.content),"권한이 거부되었습니다.\n설정(앱 정보)에서 권한을 허용해야 합니다.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).show();
                }
            }

            Snackbar.make(findViewById(android.R.id.content),"권한이 거부되었습니다.\n앱을 다시 실행하여 권한을 허용해 주세요.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();



        }
    }
}
