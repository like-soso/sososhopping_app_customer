package com.sososhopping.customer.search;

import static com.sososhopping.customer.common.Constant.*;

import android.content.Context;
import android.location.LocationManager;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sososhopping.customer.common.Constant;
import com.sososhopping.customer.common.gps.CalculateDistance;
import com.sososhopping.customer.common.gps.GPSTracker;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.common.types.enumType.AskType;
import com.sososhopping.customer.common.types.enumType.CategoryType;
import com.sososhopping.customer.common.types.enumType.SearchType;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.search.repository.SearchRepository;
import com.sososhopping.customer.shop.model.ShopItemModel;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class HomeViewModel extends ViewModel {

    //카테고리 or 검색
    private MutableLiveData<AskType> askType = new MutableLiveData<>();

    //상점 or 상품
    private MutableLiveData<SearchType> searchType = new MutableLiveData<>();
    private MutableLiveData<String> searchContent = new MutableLiveData<>();
    private MutableLiveData<String> category = new MutableLiveData<>();

    //검색결과
    private MutableLiveData<ArrayList<ShopInfoShortModel>> shopList= new MutableLiveData<>();
    private MutableLiveData<Integer> radius = new MutableLiveData<>();

    int offset = 0;
    int numberOfElement = LIMIT_PAGE;

    private final SearchRepository searchRepository = SearchRepository.getInstance();

    public void setCategory(String s){
        category.setValue(s);
    }

    public HomeViewModel(){
        super();
        this.askType.setValue(AskType.Search);
        this.shopList.setValue(new ArrayList<>());
        this.searchType.setValue(SearchType.STORE);
        this.radius.setValue(DEFAULT_RAD);
    }

    public void resetPage(){
        this.offset = 0;
        this.numberOfElement = LIMIT_PAGE;
    }


    public void setSearchContent(String content){
        searchContent.setValue(content);
    }

    public void search(String token,
                       Location location,
                       Integer radius,
                       Integer offset,
                       Integer navigate,
                       BiConsumer<PageableShopListDto, Integer> onSuccess,
                       Runnable onError){

        if(radius != null){
            this.getRadius().setValue(radius);
        }
        else{
            radius = this.getRadius().getValue();
        }

        String type;
        double lat = location.getLat();
        double lng = location.getLng();

        if(offset == null){
            offset = this.offset;
        }

        //검색
        if(askType.getValue().equals(AskType.Search)){
            type = this.getSearchType().getValue().toString();
            String q = this.getSearchContent().getValue();
            searchRepository.searchByPage(token, type, q, lat, lng, radius, offset, navigate, onSuccess, onError);
        }

        //카테고리
        else if(askType.getValue().equals(AskType.Category)){
            type = this.getCategory().getValue();

            //지역화폐면 다르게
            if(type.equals(CategoryType.LOCALPAY.toString())){
                searchRepository.localByPage(token,lat,lng,radius,offset,navigate,onSuccess,onError);
            }
            else{
                searchRepository.categoryByPage(token, type, lat, lng, radius, offset, navigate, onSuccess, onError);
            }

        }
    }

    public Location getLocation(Context context){
        GPSTracker gpsTracker = GPSTracker.getInstance(context);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        Location location = new Location(latitude, longitude);

        return location;
    }

    //1 : 상품 / 0 : 상점
    public void setSearchType(Boolean checked){
        if(checked){
            searchType.setValue(SearchType.ITEM);
        }else{
            searchType.setValue(SearchType.STORE);
        }
    }
}