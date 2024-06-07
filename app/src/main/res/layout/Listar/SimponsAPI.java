package com.example.simpsonsagc.Listar;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SimponsAPI {
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.sampleapis.com/simpsons/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();
    private static CapitulosService service ;
    public static CapitulosService getInstance(){
        if (service==null)
            service= retrofit.create(CapitulosService.class);
        return  service;
    }
}
