package com.example.pasapp.Bebidas;

import com.example.pasapp.Bebidas.Modelos.DrinkResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BebidasService{
    @GET("search.php")
    public Single<DrinkResponse> obtenerBebidas(
            @Query("f") String letra);

}

