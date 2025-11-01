package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.content.Context;
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
            } else {
                tvEstado.setVisibility(View.GONE);
            }
        }
    }
}
