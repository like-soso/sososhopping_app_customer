package com.sososhopping.customer.mysoso.viemodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.mysoso.dto.OrderCancelDto;
import com.sososhopping.customer.mysoso.dto.OrderDetailDto;
import com.sososhopping.customer.mysoso.repository.MysosoOrderRepository;

import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class OrderDetailViewModel extends ViewModel {
    MysosoOrderRepository orderRepository = MysosoOrderRepository.getInstance();

    MutableLiveData<OrderDetailDto> orderDetailDto = new MutableLiveData<>();

    public void requestMyOrderDetails(String token,
                                      long orderId,
                                      Consumer<OrderDetailDto> onSuccess,
                                      Runnable onFailed,
                                      Runnable onError){
        orderRepository.requestMyOrderDetails(token, orderId, onSuccess, onFailed, onError);
    }

    public void requestMyOrderCancel(String token,
                                       long orderId,
                                       Runnable onSuccess,
                                       Runnable onFailed,
                                       Runnable onError){
        orderRepository.requestMyOrderCancel(token, orderId, new OrderCancelDto("CANCEL"),onSuccess, onFailed, onError);
    }

    public void requestMyOrderDone(String token,
                                   long orderId,
                                   Runnable onSuccess,
                                   Runnable onFailed,
                                   Runnable onError) {
        orderRepository.requestMyOrderCancel(token, orderId, new OrderCancelDto("DONE"), onSuccess, onFailed, onError);
    }
}
