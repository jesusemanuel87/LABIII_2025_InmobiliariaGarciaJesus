package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Pago;

import java.util.List;

public class PagosAdapter extends RecyclerView.Adapter<PagosAdapter.ViewHolder> {
    
    private List<Pago> pagos;
    private Context context;

    public PagosAdapter(List<Pago> pagos, Context context) {
        this.pagos = pagos;
        this.context = context;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pago, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pago pago = pagos.get(position);
        holder.bind(pago);
    }

    @Override
    public int getItemCount() {
        return pagos != null ? pagos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFechaPago;
        private TextView tvImporte;
        private TextView tvNumeroPago;
        private TextView tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaPago = itemView.findViewById(R.id.tvFechaPago);
            tvImporte = itemView.findViewById(R.id.tvImportePago);
            tvNumeroPago = itemView.findViewById(R.id.tvNumeroPago);
            tvEstado = itemView.findViewById(R.id.tvEstadoPago);
        }

        public void bind(Pago pago) {
            tvNumeroPago.setText("Pago #" + pago.getNumero());
            tvFechaPago.setText("Fecha: " + (pago.getFechaPago() != null ? pago.getFechaPago() : "No especificado"));
            tvImporte.setText(String.format("$ %.2f", pago.getImporte()));
            
            if (pago.getEstado() != null && !pago.getEstado().isEmpty()) {
                tvEstado.setText("Estado: " + pago.getEstado());
                tvEstado.setVisibility(View.VISIBLE);
                
                // Aplicar colores según el estado
                if ("Vencido".equalsIgnoreCase(pago.getEstado())) {
                    // Vencido: Rojo
                    tvEstado.setTextColor(Color.RED);
                    tvImporte.setTextColor(Color.RED);
                    itemView.setBackgroundColor(Color.parseColor("#FFEBEE")); // Fondo rojo suave
                } else if ("Pendiente".equalsIgnoreCase(pago.getEstado())) {
                    // Pendiente: Naranja
                    tvEstado.setTextColor(Color.parseColor("#FF9800"));
                    tvImporte.setTextColor(Color.parseColor("#FF9800"));
                    itemView.setBackgroundColor(Color.parseColor("#FFF3E0")); // Fondo naranja suave
                } else {
                    // Pagado u otros: Verde
                    tvEstado.setTextColor(Color.parseColor("#4CAF50")); // Verde
                    tvImporte.setTextColor(Color.parseColor("#2196F3")); // Azul para importe
                    itemView.setBackgroundColor(Color.WHITE); // Fondo blanco
                }
            } else {
                tvEstado.setVisibility(View.GONE);
                // Restaurar colores por defecto si no hay estado
                tvImporte.setTextColor(Color.parseColor("#2196F3"));
                itemView.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
