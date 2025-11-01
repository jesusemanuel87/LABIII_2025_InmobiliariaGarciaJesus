package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

public class InquilinosAdapter extends RecyclerView.Adapter<InquilinosAdapter.ViewHolder> {
    
    private List<Contrato> contratos;
    private Context context;
    private OnContratoClickListener listener;

    public interface OnContratoClickListener {
        void onContratoClick(Contrato contrato);
    }

    public InquilinosAdapter(List<Contrato> contratos, Context context) {
        this.contratos = contratos;
        this.context = context;
    }

    public void setOnContratoClickListener(OnContratoClickListener listener) {
        this.listener = listener;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
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
        Contrato contrato = contratos.get(position);
        holder.bind(contrato, listener);
    }

    @Override
    public int getItemCount() {
        return contratos != null ? contratos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagen;
        private TextView tvDireccion;
        private TextView tvTipo;
        private TextView tvPrecio;
        private TextView tvEstado;
        private SwitchCompat switchEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Reutilizar los IDs del layout item_inmueble.xml
            ivImagen = itemView.findViewById(R.id.ivImagenInmueble);
            tvDireccion = itemView.findViewById(R.id.tvDireccionInmueble);
            tvTipo = itemView.findViewById(R.id.tvTipoInmueble);
            tvPrecio = itemView.findViewById(R.id.tvPrecioInmueble);
            tvEstado = itemView.findViewById(R.id.tvEstadoInmueble);
            switchEstado = itemView.findViewById(R.id.switchEstadoInmueble);
            
            // Ocultar el switch en Inquilinos
            switchEstado.setVisibility(View.GONE);
        }

        public void bind(Contrato contrato, OnContratoClickListener listener) {
            // Mostrar dirección del inmueble
            if (contrato.getInmueble() != null) {
                tvDireccion.setText(contrato.getInmueble().getDireccion());
                
                // Cargar imagen del inmueble
                String imageUrl = contrato.getInmueble().getImagenPortadaUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Log.d("INQUILINOS_ADAPTER", "URL original: " + imageUrl);
                    
                    // Reemplazar localhost con DevTunnel URL
                    if (imageUrl.contains("localhost:5000") || imageUrl.contains("127.0.0.1:5000")) {
                        String baseUrl = ApiClient.getBaseUrl(itemView.getContext());
                        imageUrl = imageUrl.replace("http://localhost:5000/", baseUrl);
                        imageUrl = imageUrl.replace("https://localhost:5000/", baseUrl);
                        imageUrl = imageUrl.replace("http://127.0.0.1:5000/", baseUrl);
                        imageUrl = imageUrl.replace("https://127.0.0.1:5000/", baseUrl);
                        Log.d("INQUILINOS_ADAPTER", "URL reemplazada: " + imageUrl);
                    }
                    // Si la URL no comienza con http, construir URL completa
                    else if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                        String baseUrl = ApiClient.getBaseUrl(itemView.getContext());
                        if (imageUrl.startsWith("/")) {
                            imageUrl = baseUrl + imageUrl.substring(1);
                        } else {
                            imageUrl = baseUrl + imageUrl;
                        }
                        Log.d("INQUILINOS_ADAPTER", "URL construida: " + imageUrl);
                    }
                    
                    Glide.with(itemView.getContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_home)
                            .error(R.drawable.ic_home)
                            .into(ivImagen);
                } else {
                    Log.d("INQUILINOS_ADAPTER", "No hay imagen para el inmueble");
                    ivImagen.setImageResource(R.drawable.ic_home);
                }
                
                // Mostrar tipo (o inquilino)
                if (contrato.getInquilino() != null) {
                    tvTipo.setText("Inquilino: " + contrato.getInquilino().getNombreCompleto());
                } else {
                    tvTipo.setText("Inquilino: No especificado");
                }
            } else {
                tvDireccion.setText("Inmueble no disponible");
                tvTipo.setText("Sin información");
                ivImagen.setImageResource(R.drawable.ic_home);
            }
            
            // Mostrar precio del alquiler
            tvPrecio.setText(String.format("$ %.2f", contrato.getPrecio()));
            
            // Mostrar estado del contrato en lugar del estado del inmueble
            tvEstado.setText(contrato.getEstado() != null ? contrato.getEstado() : "Sin estado");
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContratoClick(contrato);
                }
            });
        }
    }
}
