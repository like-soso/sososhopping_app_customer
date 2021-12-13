package com.sososhopping.customer.mysoso.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.sososhopping.customer.R;
import com.sososhopping.customer.databinding.RoadaddressWebDialogBinding;

import org.jetbrains.annotations.Nullable;

public class RoadAddressSearchDialog extends DialogFragment {

    public static RoadAddressSearchDialog newInstance() {
        return new RoadAddressSearchDialog();
    }

    RoadaddressWebDialogBinding binding;
    NavController navController;
    WebView webView;
    Handler handler;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //binding 설정
        binding = RoadaddressWebDialogBinding.inflate(inflater, container, false);

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        //Controller 설정
        navController = NavHostFragment.findNavController(this);

        init_webView();

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    public void init_webView() {
        // WebView 설정
        webView = binding.daumWebview;

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "MysosoApp");

        //DOMStorage 허용
        webView.getSettings().setDomStorageEnabled(true);

        //ssl 인증이 없는 경우 해결을 위한 부분
        webView.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // SSL 에러가 발생해도 계속 진행
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 페이지 로딩 시작시 호출
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("페이지 시작", url);
                binding.webProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                binding.webProgress.setVisibility(View.GONE);

                Log.e("페이지 로딩", url);
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        // webview url load. php 파일 주소
        webView.loadUrl("http://sososhopping.com:8080/roadSearch.html");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class AndroidBridge {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Log.e("사용된거 실행1", data);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("사용된거 실행2", data);
                    String roadAdd = data.substring(7);
                    navController.getPreviousBackStackEntry().getSavedStateHandle().set("roadAddress", roadAdd);
                    dismiss();
                }
            });


        }
    }

}
