package com.example.pasapp.Listar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasapp.Listar.modelos.Capi;
import com.example.pasapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimpsonsAdapter extends RecyclerView.Adapter<SimpsonsAdapter.ViewHolder>{
    private List<Capi> datos;
    public SimpsonsAdapter(List<Capi> datos){
        this.datos = datos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cview = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ViewHolder(cview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String titulo = datos.get(position).getName()+
                ".\nS"+datos.get(position).getSeason()+
                " E"+datos.get(position).getEpisode();
        holder.title.setText(titulo);
        holder.description.setText(datos.get(position).getDescription());
        Picasso.get().load(""+datos.get(position).getThumbnailUrl())
                .resize(600,400)
                .into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView cover;
        private TextView description;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.title);
            cover = itemView.findViewById(R.id.cover);
            description = itemView.findViewById(R.id.description);
        }
    }
}
