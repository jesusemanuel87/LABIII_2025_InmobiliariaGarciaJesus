package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class InmueblesAdapter extends RecyclerView.Adapter<InmueblesAdapter.ViewHolder> {
    
    private List<Inmueble> inmuebles;
    private Context context;
    private OnInmuebleClickListener listener;
    private OnEstadoChangeListener estadoListener;

    private static String formatearNumeroArgentino(double numero) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(numero);
    }

    public interface OnInmuebleClickListener {
        void onInmuebleClick(Inmueble inmueble);
    }
    
    public interface OnEstadoChangeListener {
        void onEstadoChange(Inmueble inmueble, boolean nuevoEstado);
    }

    public InmueblesAdapter(List<Inmueble> inmuebles, Context context) {
        this.inmuebles = inmuebles;
        this.context = context;
    }

    public void setOnInmuebleClickListener(OnInmuebleClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnEstadoChangeListener(OnEstadoChangeListener listener) {
        this.estadoListener = listener;
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
        holder.bind(inmueble, listener, estadoListener);
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
        private TextView tvDisponibilidad;
        private TextView tvEstado;
        private SwitchCompat switchEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagenInmueble);
            tvDireccion = itemView.findViewById(R.id.tvDireccionInmueble);
            tvTipo = itemView.findViewById(R.id.tvTipoInmueble);
            tvPrecio = itemView.findViewById(R.id.tvPrecioInmueble);
            tvAmbientes = itemView.findViewById(R.id.tvAmbientesInmueble);
            tvDisponibilidad = itemView.findViewById(R.id.tvDisponibilidadInmueble);
            tvEstado = itemView.findViewById(R.id.tvEstadoInmueble);
            switchEstado = itemView.findViewById(R.id.switchEstadoInmueble);
        }

        public void bind(Inmueble inmueble, OnInmuebleClickListener listener, OnEstadoChangeListener estadoListener) {
            // Cargar imagen con Glide
            if (inmueble.getImagenPortadaUrl() != null && !inmueble.getImagenPortadaUrl().isEmpty()) {
                String imageUrl = inmueble.getImagenPortadaUrl();
                
                Log.d("INMUEBLES_ADAPTER", "URL original de imagen: " + imageUrl);
                
                // Reemplazar localhost con DevTunnel URL
                if (imageUrl.contains("localhost:5000") || imageUrl.contains("127.0.0.1:5000")) {
                    String baseUrl = ApiClient.getBaseUrl(itemView.getContext());
                    imageUrl = imageUrl.replace("http://localhost:5000/", baseUrl);
                    imageUrl = imageUrl.replace("https://localhost:5000/", baseUrl);
                    imageUrl = imageUrl.replace("http://127.0.0.1:5000/", baseUrl);
                    imageUrl = imageUrl.replace("https://127.0.0.1:5000/", baseUrl);
                    Log.d("INMUEBLES_ADAPTER", "URL localhost reemplazada: " + imageUrl);
                }
                // Si la URL no comienza con http, construir URL completa
                else if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    
                    String baseUrl = ApiClient.getBaseUrl(itemView.getContext());
                    Log.d("INMUEBLES_ADAPTER", "Base URL: " + baseUrl);
                    
                    if (imageUrl.startsWith("/")) {
                        imageUrl = baseUrl + imageUrl.substring(1);
                    } else {
                        imageUrl = baseUrl + imageUrl;
                    }
                    Log.d("INMUEBLES_ADAPTER", "URL completa construida: " + imageUrl);
                } else {
                    Log.d("INMUEBLES_ADAPTER", "URL ya incluye protocolo: " + imageUrl);
                }
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_home)
                        .error(R.drawable.ic_home)
                        .into(ivImagen);
            } else {
                Log.d("INMUEBLES_ADAPTER", "No hay URL de imagen para inmueble ID: " + inmueble.getId());
                ivImagen.setImageResource(R.drawable.ic_home);
            }
            
            tvDireccion.setText(inmueble.getDireccion());
            tvTipo.setText(inmueble.getTipoNombre());
            
            if (inmueble.getPrecio() != null) {
                tvPrecio.setText("$ " + formatearNumeroArgentino(inmueble.getPrecio()));
            } else {
                tvPrecio.setText("Precio no disponible");
            }
            
            tvAmbientes.setText(inmueble.getAmbientes() + " ambientes");
            
            // Mostrar disponibilidad (Disponible, No Disponible, Reservado)
            String disponibilidad = inmueble.getDisponibilidad() != null ? inmueble.getDisponibilidad() : "Sin información";
            tvDisponibilidad.setText(disponibilidad);
            
            // Cambiar color según disponibilidad
            if ("Disponible".equals(disponibilidad)) {
                tvDisponibilidad.setBackgroundColor(0xFF4CAF50); // Verde
            } else if ("No Disponible".equals(disponibilidad)) {
                tvDisponibilidad.setBackgroundColor(0xFFF44336); // Rojo
            } else if ("Reservado".equals(disponibilidad)) {
                tvDisponibilidad.setBackgroundColor(0xFFFF9800); // Naranja
            } else {
                tvDisponibilidad.setBackgroundColor(0xFF9E9E9E); // Gris
            }
            
            // Mostrar estado (Activo/Inactivo)
            String estado = inmueble.getEstado() != null ? inmueble.getEstado() : "Sin estado";
            tvEstado.setText(estado);
            
            // Cambiar color del texto según estado
            if ("Activo".equals(estado)) {
                tvEstado.setTextColor(0xFF4CAF50); // Verde
            } else {
                tvEstado.setTextColor(0xFFF44336); // Rojo
            }
            
            // Configurar switch según el estado actual
            boolean esActivo = "Activo".equals(estado);
            switchEstado.setChecked(esActivo);
            
            // Habilitar switch solo si la disponibilidad es "Disponible"
            boolean puedeModificar = "Disponible".equals(disponibilidad);
            switchEstado.setEnabled(puedeModificar);
            
            // Remover listeners previos para evitar duplicados
            switchEstado.setOnCheckedChangeListener(null);
            
            // Listener para cambio de estado
            switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (estadoListener != null && buttonView.isPressed()) {
                    estadoListener.onEstadoChange(inmueble, isChecked);
                }
            });
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInmuebleClick(inmueble);
                }
            });
        }
    }
}
