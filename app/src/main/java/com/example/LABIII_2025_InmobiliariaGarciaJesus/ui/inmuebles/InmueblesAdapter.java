package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;

import java.util.List;

public class InmueblesAdapter extends RecyclerView.Adapter<InmueblesAdapter.ViewHolder> {
    
    private List<Inmueble> inmuebles;
    private Context context;
    private OnInmuebleClickListener listener;

    public interface OnInmuebleClickListener {
        void onInmuebleClick(Inmueble inmueble);
    }

    public InmueblesAdapter(List<Inmueble> inmuebles, Context context) {
        this.inmuebles = inmuebles;
        this.context = context;
    }

    public void setOnInmuebleClickListener(OnInmuebleClickListener listener) {
        this.listener = listener;
    }

    public void setInmuebles(List<Inmueble> inmuebles) {
        this.inmuebles = inmuebles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = inmuebles.get(position);
        holder.bind(inmueble, listener);
    }

    @Override
    public int getItemCount() {
        return inmuebles != null ? inmuebles.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagen;
        private TextView tvDireccion;
        private TextView tvTipo;
        private TextView tvPrecio;
        private TextView tvAmbientes;
        private TextView tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagenInmueble);
            tvDireccion = itemView.findViewById(R.id.tvDireccionInmueble);
            tvTipo = itemView.findViewById(R.id.tvTipoInmueble);
            tvPrecio = itemView.findViewById(R.id.tvPrecioInmueble);
            tvAmbientes = itemView.findViewById(R.id.tvAmbientesInmueble);
            tvEstado = itemView.findViewById(R.id.tvEstadoInmueble);
        }

        public void bind(Inmueble inmueble, OnInmuebleClickListener listener) {
            // Cargar imagen con Glide
            if (inmueble.getImagenPortadaUrl() != null && !inmueble.getImagenPortadaUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(inmueble.getImagenPortadaUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivImagen);
            } else {
                ivImagen.setImageResource(R.drawable.ic_launcher_background);
            }
            
            tvDireccion.setText(inmueble.getDireccion());
            tvTipo.setText(inmueble.getTipoNombre());
            
            if (inmueble.getPrecio() != null) {
                tvPrecio.setText(String.format("$ %.2f", inmueble.getPrecio()));
            } else {
                tvPrecio.setText("Precio no disponible");
            }
            
            tvAmbientes.setText(inmueble.getAmbientes() + " ambientes");
            
            String estado = inmueble.isDisponible() ? "Disponible" : "No disponible";
            tvEstado.setText(estado);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInmuebleClick(inmueble);
                }
            });
        }
    }
}
