package com.example.pasapp.Bebidas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasapp.Bebidas.Modelos.Drink;
import com.example.pasapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CocktailsAdapter extends RecyclerView.Adapter<CocktailsAdapter.ViewHolder>{
    private List<Drink> datos;
    public CocktailsAdapter(List<Drink> datos){
        this.datos = datos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cview = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drink,parent,false);
        return new ViewHolder(cview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(datos.get(position).getStrDrink());
        holder.alcohol.setText(datos.get(position).getStrAlcoholic());
        if ("Alcoholic".equals(datos.get(position).getStrAlcoholic())) {
            holder.alcohol.setTextColor(Color.RED);
        } else {
            holder.alcohol.setTextColor(Color.GREEN);
        }
        holder.ingredientes.setText(datos.get(position).obtenerTodosLosIngredientes());
        holder.instrucciones.setText(datos.get(position).getStrInstructions());
        Picasso.get().load(""+datos.get(position).getStrDrinkThumb())
                .resize(600,600)
                .into(holder.imagenBebida);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre;
        private ImageView imagenBebida;
        private TextView alcohol;
        private TextView ingredientes;
        private TextView instrucciones;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreBebida);
            imagenBebida = itemView.findViewById(R.id.cover);
            alcohol = itemView.findViewById(R.id.alcohol);
            instrucciones = itemView.findViewById(R.id.instrucciones);
            ingredientes = itemView.findViewById(R.id.ingredientes);
        }
    }
}

