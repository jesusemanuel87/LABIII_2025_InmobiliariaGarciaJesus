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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            tvFechaPago.setText("Fecha: " + formatearFecha(pago.getFechaPago()));
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
        
        private String formatearFecha(String fechaISO) {
            if (fechaISO == null || fechaISO.isEmpty()) {
                return "No especificado";
            }
            
            try {
                // Formato de entrada ISO 8601
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                // Formato de salida: día/mes/año
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                
                Date fecha = formatoEntrada.parse(fechaISO);
                return formatoSalida.format(fecha);
            } catch (ParseException e) {
                // Si falla el parseo, intentar extraer solo la fecha
                if (fechaISO.contains("T")) {
                    String[] partes = fechaISO.split("T");
                    if (partes.length > 0) {
                        try {
                            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date fecha = formatoEntrada.parse(partes[0]);
                            return formatoSalida.format(fecha);
                        } catch (ParseException ex) {
                            return partes[0]; // Devolver al menos la parte de fecha
                        }
                    }
                }
                return fechaISO; // Si todo falla, devolver la fecha original
            }
        }
    }
}
