package com.sososhopping.customer.search.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sososhopping.customer.common.retrofit.ApiServiceFactory;
import com.sososhopping.customer.common.types.Location;
import com.sososhopping.customer.search.dto.PageableShopListDto;
import com.sososhopping.customer.search.dto.ShopListDto;
import com.sososhopping.customer.search.model.ShopInfoShortModel;
import com.sososhopping.customer.search.service.SearchService;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {
    private static SearchRepository instance;
    private final SearchService searchService;

    private SearchRepository() {
        this.searchService = ApiServiceFactory.createService(SearchService.class);
    }

    public static synchronized SearchRepository getInstance() {
        if (instance == null) {
            instance = new SearchRepository();
        }
        return instance;
    }


    //페이징
    public void searchByPage(String token, String type, String q, Double lat, Double lng, Integer radius, Integer offset,
                             Integer navigate,
                             BiConsumer<PageableShopListDto, Integer> onSuccess,
                             Runnable onError) {
        searchService.searchBySearchPage(token, type, lat, lng, radius, q, offset).enqueue(new Callback<PageableShopListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PageableShopListDto> call, Response<PageableShopListDto> response) {
                Log.d("log", response.raw().toString());
                if (response.code() == 200) {
                    onSuccess.accept(response.body(), navigate);
                    for (ShopInfoShortModel s : response.body().getContent()) {
                        Log.e("search", s.getName() + " " + s.getDistance() + " " + response.body().getPageable().getOffset() + " OFFSET : " + offset);
                    }
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<PageableShopListDto> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }

    public void categoryByPage(String token, String type, Double lat, Double lng, Integer radius, Integer offset,
                               Integer navigate,
                               BiConsumer<PageableShopListDto, Integer> onSuccess,
                               Runnable onError) {
        searchService.searchByCategoryPage(token, type, lat, lng, radius, offset).enqueue(new Callback<PageableShopListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<PageableShopListDto> call, Response<PageableShopListDto> response) {
                Log.d("log", response.raw().toString());
                if (response.code() == 200) {
                    onSuccess.accept(response.body(), navigate);
                    for (ShopInfoShortModel s : response.body().getContent()) {
                        Log.e("categ", s.getName() + " " + s.getDistance());
                    }
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<PageableShopListDto> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }

    public void localByPage(String token, Double lat, Double lng, Integer radius, Integer offset,
                            Integer navigate,
                            BiConsumer<PageableShopListDto, Integer> onSuccess,
                            Runnable onError){
        searchService.searchLocalPage(token, lat, lng, radius, offset).enqueue(new Callback<PageableShopListDto>() {
            @Override
            public void onResponse(Call<PageableShopListDto> call, Response<PageableShopListDto> response) {
                Log.d("log", response.raw().toString());
                if (response.code() == 200) {
                    onSuccess.accept(response.body(), navigate);
                    for (ShopInfoShortModel s : response.body().getContent()) {
                        Log.e("categ", s.getName() + " " + s.getDistance());
                    }
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<PageableShopListDto> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }


    public void searchCategory(String token,
                               String category,
                               Double lat, Double lng,
                               Integer radius,
                               BiConsumer<ShopListDto, Boolean> onSuccess,
                               Runnable onError) {
        searchService.searchByCategory(token, category, lat, lng, radius).enqueue(new Callback<ShopListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ShopListDto> call, Response<ShopListDto> response) {
                Log.d("response", response.raw().toString());
                if (response.code() == 200) {
                    onSuccess.accept(response.body(), true);
                } else {
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<ShopListDto> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });
    }

    public void searchSearch(String token, String type, String q, Double lat, Double lng, Integer radius,
                             boolean b,
                             BiConsumer<ShopListDto, Boolean> onSuccess,
                             Runnable onError) {
        searchService.searchBySearch(token, type, lat, lng, radius, q).enqueue(new Callback<ShopListDto>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<ShopListDto> call, Response<ShopListDto> response) {
                if (response.code() == 200) {
                    onSuccess.accept(response.body(), b);
                } else {
                    Log.d("log", response.raw().toString());
                    Log.d("log", response.errorBody().toString());
                    onError.run();
                }
            }

            @Override
            public void onFailure(Call<ShopListDto> call, Throwable t) {
                t.printStackTrace();
                onError.run();
            }
        });

    }
}
