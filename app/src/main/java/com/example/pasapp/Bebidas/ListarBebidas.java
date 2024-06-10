package com.example.pasapp.Bebidas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasapp.Bebidas.Modelos.Drink;
import com.example.pasapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

;

public class ListarBebidas extends AppCompatActivity {
    private List<Drink> dato = new ArrayList<>();
    private CocktailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_coctel);

        obtenerTodasLasBebidas();
    }
    private void obtenerTodasLasBebidas() {
        for (char c = 'a'; c <= 'z'; c++) {
            obtenerListaCaps(String.valueOf(c));
        }
    }
    private void obtenerListaCaps(String letra){
        RecyclerView lista = findViewById(R.id.listaCapi);
        adapter=new CocktailsAdapter(dato);
        lista.setLayoutManager( new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        CocktailsAPI.getInstance().obtenerBebidas(letra)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    List<Drink> drinks = x.getDrinks();
                    if (drinks != null && !drinks.isEmpty()) {
                        dato.addAll(drinks);
                        Collections.sort(dato, (drink1, drink2) -> drink1.getStrDrink().compareTo(drink2.getStrDrink()));
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}