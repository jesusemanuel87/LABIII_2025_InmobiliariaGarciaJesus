package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;

import java.util.List;

public class InquilinosAdapter extends RecyclerView.Adapter<InquilinosAdapter.ViewHolder> {
    
    private List<InquilinoContrato> inquilinos;
    private Context context;
    private OnInquilinoClickListener listener;

    public interface OnInquilinoClickListener {
        void onInquilinoClick(InquilinoContrato inquilino);
    }

    public InquilinosAdapter(List<InquilinoContrato> inquilinos, Context context) {
        this.inquilinos = inquilinos;
        this.context = context;
    }

    public void setOnInquilinoClickListener(OnInquilinoClickListener listener) {
        this.listener = listener;
    }

    public void setInquilinos(List<InquilinoContrato> inquilinos) {
        this.inquilinos = inquilinos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquilino, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InquilinoContrato inquilino = inquilinos.get(position);
        holder.bind(inquilino, listener);
    }

    @Override
    public int getItemCount() {
        return inquilinos != null ? inquilinos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre;
        private TextView tvDni;
        private TextView tvTelefono;
        private TextView tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreInquilino);
            tvDni = itemView.findViewById(R.id.tvDniInquilino);
            tvTelefono = itemView.findViewById(R.id.tvTelefonoInquilino);
            tvEmail = itemView.findViewById(R.id.tvEmailInquilino);
        }

        public void bind(InquilinoContrato inquilino, OnInquilinoClickListener listener) {
            tvNombre.setText(inquilino.getNombreCompleto());
            tvDni.setText("DNI: " + (inquilino.getDni() != null ? inquilino.getDni() : "No especificado"));
            tvTelefono.setText("Tel: " + (inquilino.getTelefono() != null ? inquilino.getTelefono() : "No especificado"));
            tvEmail.setText(inquilino.getEmail() != null ? inquilino.getEmail() : "No especificado");
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInquilinoClick(inquilino);
                }
            });
        }
    }
}
