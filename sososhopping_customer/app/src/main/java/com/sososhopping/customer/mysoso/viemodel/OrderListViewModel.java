package com.sososhopping.customer.mysoso.viemodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.types.enumType.OrderStatus;
import com.sososhopping.customer.mysoso.dto.OrderListDto;
import com.sososhopping.customer.mysoso.dto.PageableOrderListDto;
import com.sososhopping.customer.mysoso.model.OrderRecordShortModel;
import com.sososhopping.customer.mysoso.repository.MysosoOrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderListViewModel extends ViewModel {

    MysosoOrderRepository mysosoOrderRepository = MysosoOrderRepository.getInstance();

    MutableLiveData<ArrayList<OrderRecordShortModel>> orderList = new MutableLiveData<>();

    int offset;
    int numberOfElement = Constant.LIMIT_PAGE;

    public void resetPage(){
        this.offset = 0;
        this.numberOfElement = Constant.LIMIT_PAGE;
    }

    public OrderListViewModel() {
        orderList.setValue(new ArrayList<>());
    }

    public void requestMyOrderListsPage(String token,
                                    OrderStatus orderStatuses,
                                    Integer offset,
                                    Consumer<PageableOrderListDto> onSuccess,
                                    Runnable onLogInFailed,
                                    Runnable onFailed,
                                    Runnable onError){

        if(offset == null){
            offset = this.offset;
        }

        mysosoOrderRepository.requestMyOrderListsPage(token, changeOrderStatus(orderStatuses), offset, onSuccess, onLogInFailed, onFailed, onError);
    }

    public String changeOrderStatus(OrderStatus orderStatuses){
        StringBuilder sb = new StringBuilder();
        switch (orderStatuses){
            case APPROVE_ALL:
                sb.append(OrderStatus.APPROVE.getStat());
                sb.append(",");
                sb.append(OrderStatus.READY.getStat());
                break;

            case CANCEL_ALL:
                sb.append(OrderStatus.CANCEL.getStat());
                sb.append(",");
                sb.append(OrderStatus.REJECT.getStat());
                break;

            default:
                sb.append(orderStatuses.getStat());
                break;
        }

        return sb.toString();
    }
}
