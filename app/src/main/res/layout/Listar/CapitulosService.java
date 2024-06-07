package com.example.simpsonsagc.Listar;



import com.example.simpsonsagc.Listar.modelos.Capi;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface CapitulosService {
    @GET("episodes")
    Observable<List<Capi>> obtenerCapitulos();

}