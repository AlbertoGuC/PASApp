package com.example.pasapp.Listar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasapp.Listar.modelos.Capi;
import com.example.pasapp.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListarEpisodios extends AppCompatActivity {
    private List<Capi> datos = new ArrayList<>();
    private SimpsonsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_episodios);

        obtenerListaCaps();
    }

    private void obtenerListaCaps(){
        RecyclerView lista = findViewById(R.id.listaCapi);
        adapter=new SimpsonsAdapter(datos);
        lista.setLayoutManager( new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        SimponsAPI.getInstance().obtenerCapitulos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    datos.addAll(x);
                    adapter.notifyDataSetChanged();
                });
    }
}