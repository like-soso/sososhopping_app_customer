package com.sososhopping.customer.mysoso.viemodel;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.mysoso.dto.PointDetailDto;
import com.sososhopping.customer.mysoso.model.PointDetailModel;
import com.sososhopping.customer.mysoso.repository.MysosoPointRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class PointDetailViewModel extends ViewModel {
    private final MysosoPointRepository mysosoPointRepository = MysosoPointRepository.getInstance();
    //0 1 2 시작
    MutableLiveData<Date>[] curDate = new MutableLiveData[3];
    MutableLiveData<ArrayList<PointDetailModel>>[] detailList = new MutableLiveData[3];

    MutableLiveData<Integer> storeId = new MutableLiveData<>();
    MutableLiveData<String> phone = new MutableLiveData<>();
    MutableLiveData<Location> location = new MutableLiveData<>();
    int counter;

    public PointDetailViewModel(){
        //init
        Calendar cal = Calendar.getInstance();

        for (int i=0; i<3; i++){
            detailList[i] = new MutableLiveData<>();
            detailList[i].setValue(new ArrayList<>());
            curDate[i] = new MutableLiveData<>();

            //set first day of month
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_MONTH,1);
            curDate[i].setValue(cal.getTime());
        }

        changeMonth(false, 0,1);
        changeMonth(true,2,1);
        counter = 1;
    }

    public void changeMonth(boolean state, int idx, int amount){
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate[idx].getValue());
        if(state){
            cal.set(Calendar.DAY_OF_MONTH,1);
            cal.add(Calendar.MONTH, amount); // Zero-based months
        }
        else{
            cal.set(Calendar.DAY_OF_MONTH,1);
            cal.add(Calendar.MONTH, -amount );
        }
        curDate[idx].setValue(cal.getTime());
    }

    public String returnDate(int idx){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM");
        return sf.format(curDate[idx].getValue());
    }

    public void setCounter(int c){
        this.counter = c;
    }

    public int addCounter(int c){
        return (c+1)%3;
    }

    public int subCounter(int c){
        if(c == 0){
            return 2;
        }
        else{
            return c-1;
        }
    }

    public void requestPointDetailFuture(String token, boolean state,
                                         BiConsumer<PointDetailDto, Integer> onSuccess,
                                         Runnable onFailed,
                                         Runnable onError){
        int target;
        if(state){
            target = addCounter(counter);
        }
        else{
            target = subCounter(counter);
        }
        changeMonth(state, target,3);
        String at = new SimpleDateFormat("yyyy-MM-dd").format(curDate[target].getValue());

        //미리 받아오기
        mysosoPointRepository.requestPointDetail(token, storeId.getValue(), at, target, onSuccess, onFailed, onError);
    }

    public void requestPointDetail(String token, int index,
                                   BiConsumer<PointDetailDto, Integer> onSuccess,
                                   Runnable onFailed,
                                   Runnable onError){
        String at = new SimpleDateFormat("yyyy-MM-dd").format(curDate[index].getValue());
        mysosoPointRepository.requestPointDetail(token, storeId.getValue(), at, index, onSuccess, onFailed, onError);
    }
}
