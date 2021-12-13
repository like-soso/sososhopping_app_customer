package com.sososhopping.customer.mysoso.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.mysoso.dto.OrderCancelDto;
import com.sososhopping.customer.mysoso.dto.OrderDetailDto;
import com.sososhopping.customer.mysoso.dto.OrderListDto;
import com.sososhopping.customer.mysoso.dto.PageableOrderListDto;
import com.sososhopping.customer.mysoso.service.MysosoService;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MysosoOrderRepository {

    private static MysosoOrderRepository instance;
    private final MysosoService mysosoService;

    private MysosoOrderRepository(){
        this.mysosoService = ApiServiceFactory.createService(MysosoService.class);
    }

    public static synchronized MysosoOrderRepository getInstance(){
        if(instance == null){
            instance = new MysosoOrderRepository();
        }
        return instance;
    }

    public void requestMyOrderListsPage(String token,
                                    String statuses,
                                    Integer offset,
                                    Consumer<PageableOrderListDto> onSuccess,
                                    Runnable onLogInFailed,
                                    Runnable onFailed,
                                    Runnable onError){
        mysosoService.requestMyOrdersPage(token, statuses, offset).enqueue(new Callback<PageableOrderListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PageableOrderListDto> call, Response<PageableOrderListDto> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.accept(response.body());
                        break;
                    }

                    case 403:{
                        onLogInFailed.run();
                        break;
                    }

                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<PageableOrderListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestMyOrderLists(String token,
                                    String statuses,
                                    Consumer<OrderListDto> onSuccess,
                                    Runnable onLogInFailed,
                                    Runnable onFailed,
                                    Runnable onError){
        mysosoService.requestMyOrders(token, statuses).enqueue(new Callback<OrderListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<OrderListDto> call, Response<OrderListDto> response) {
                switch (response.code()){
                    case 200:{
                        onSuccess.accept(response.body());
                        break;
                    }

                    case 403:{
                        onLogInFailed.run();
                        break;
                    }

                    default:
                        onFailed.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<OrderListDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestMyOrderDetails(String token,
                                      long orderId,
                                      Consumer<OrderDetailDto> onSuccess,
                                      Runnable onFailed,
                                      Runnable onError){

        mysosoService.requestMyOrdersDetail(token, orderId).enqueue(new Callback<OrderDetailDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<OrderDetailDto> call, Response<OrderDetailDto> response) {
                switch (response.code()){
                    case 200:
                        onSuccess.accept(response.body());
                        break;

                    default:
                        onFailed.run();
                        break;

                }
            }
            @Override
            public void onFailure(Call<OrderDetailDto> call, Throwable t) {
                onError.run();
            }
        });
    }

    public void requestMyOrderCancel(String token,
                                      long orderId,
                                      OrderCancelDto action,
                                      Runnable onSuccess,
                                      Runnable onFailed,
                                      Runnable onError){

        mysosoService.requestMyOrdersCancel(token, orderId,action).enqueue(new Callback<OrderDetailDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<OrderDetailDto> call, Response<OrderDetailDto> response) {
                Log.e("?", response.raw().toString());
                switch (response.code()){
                    case 200:
                        onSuccess.run();
                        break;

                    default:
                        onFailed.run();
                        break;

                }
            }
            @Override
            public void onFailure(Call<OrderDetailDto> call, Throwable t) {
                onError.run();
            }
        });
    }

}
