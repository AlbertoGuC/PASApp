package com.example.pasapp.Bebidas;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CocktailsAPI {
    //https://www.thecocktaildb.com/api/json/v1/1/search.php?f=a
    //https://www.thecocktaildb.com/api/json/v1/1/ ->base url
    //search.php?f=a -> end`point
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();
    private static BebidasService service;
    public static BebidasService  getInstance(){
        if (service==null)
            service= retrofit.create(BebidasService.class);

        return  service;
    }
}

