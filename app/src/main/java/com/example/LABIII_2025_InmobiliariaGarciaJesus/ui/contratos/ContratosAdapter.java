package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;

import java.util.List;

public class ContratosAdapter extends RecyclerView.Adapter<ContratosAdapter.ViewHolder> {
    
    private List<Contrato> contratos;
    private Context context;
    private OnContratoClickListener listener;

    public interface OnContratoClickListener {
        void onContratoClick(Contrato contrato);
    }

    public ContratosAdapter(List<Contrato> contratos, Context context) {
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
                .inflate(R.layout.item_contrato, parent, false);
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
        private TextView tvInmueble;
        private TextView tvInquilino;
        private TextView tvPrecio;
        private TextView tvFechas;
        private TextView tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInmueble = itemView.findViewById(R.id.tvInmuebleContrato);
            tvInquilino = itemView.findViewById(R.id.tvInquilinoContrato);
            tvPrecio = itemView.findViewById(R.id.tvPrecioContrato);
            tvFechas = itemView.findViewById(R.id.tvFechasContrato);
            tvEstado = itemView.findViewById(R.id.tvEstadoContrato);
        }

        public void bind(Contrato contrato, OnContratoClickListener listener) {
            if (contrato.getInmueble() != null) {
                tvInmueble.setText(contrato.getInmueble().getDireccion());
            }
            
            if (contrato.getInquilino() != null) {
                tvInquilino.setText(contrato.getInquilino().getNombreCompleto());
            }
            
            tvPrecio.setText(String.format("$ %.2f / mes", contrato.getPrecio()));
            
            String fechas = "Desde " + contrato.getFechaInicio() + " hasta " + contrato.getFechaFin();
            tvFechas.setText(fechas);
            
            tvEstado.setText(contrato.getEstado());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContratoClick(contrato);
                }
            });
        }
    }
}
