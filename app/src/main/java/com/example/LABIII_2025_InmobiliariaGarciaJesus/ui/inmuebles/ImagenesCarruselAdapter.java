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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

public class ImagenesCarruselAdapter extends RecyclerView.Adapter<ImagenesCarruselAdapter.ImagenViewHolder> {

    private List<InmuebleImagen> imagenes;
    private Context context;

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
            
            Log.d("CARRUSEL", "URL original: " + imageUrl);
            
            // Reemplazar localhost con DevTunnel URL
            if (imageUrl != null && (imageUrl.contains("localhost:5000") || imageUrl.contains("127.0.0.1:5000"))) {
                String baseUrl = ApiClient.getBaseUrl(context);
                imageUrl = imageUrl.replace("http://localhost:5000/", baseUrl);
                imageUrl = imageUrl.replace("https://localhost:5000/", baseUrl);
                imageUrl = imageUrl.replace("http://127.0.0.1:5000/", baseUrl);
                imageUrl = imageUrl.replace("https://127.0.0.1:5000/", baseUrl);
                Log.d("CARRUSEL", "URL localhost reemplazada: " + imageUrl);
            }
            // Si la URL no comienza con http, construir URL completa
            else if (imageUrl != null && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                String baseUrl = ApiClient.getBaseUrl(context);
                Log.d("CARRUSEL", "Base URL: " + baseUrl);
                
                if (imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + imageUrl.substring(1);
                } else {
                    imageUrl = baseUrl + imageUrl;
                }
                Log.d("CARRUSEL", "URL completa construida: " + imageUrl);
            } else {
                Log.d("CARRUSEL", "URL ya tiene protocolo o es null");
            }
            
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.ic_home)
                    .centerCrop()
                    .into(ivImagen);
        }
    }
}
