package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InmuebleImagen;

import java.util.List;

public class ImagenesCarruselAdapter extends RecyclerView.Adapter<ImagenesCarruselAdapter.ImagenViewHolder> {

    private List<InmuebleImagen> imagenes;
    private Context context;
    private String baseUrl = "http://10.226.44.156:5000/"; // ⚠️ Usar la misma IP del proyecto

    public ImagenesCarruselAdapter(Context context, List<InmuebleImagen> imagenes) {
        this.context = context;
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imagen_carrusel, parent, false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        InmuebleImagen imagen = imagenes.get(position);
        holder.bind(imagen);
    }

    @Override
    public int getItemCount() {
        return imagenes != null ? imagenes.size() : 0;
    }

    public class ImagenViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagen;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagenCarrusel);
        }

        public void bind(InmuebleImagen imagen) {
            String imageUrl = imagen.getRutaCompleta();
            
            Log.d("CARRUSEL", "Cargando imagen: " + imageUrl);
            
            // Si la URL no comienza con http, construir URL completa
            if (imageUrl != null && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                if (imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + imageUrl.substring(1);
                } else {
                    imageUrl = baseUrl + imageUrl;
                }
                Log.d("CARRUSEL", "URL completa: " + imageUrl);
            }
            
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(ivImagen);
        }
    }
}
