package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.notificaciones;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Notificacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder> {

    private List<Notificacion> notificaciones;
    private Context context;
    private OnNotificacionClickListener listener;

    public interface OnNotificacionClickListener {
        void onNotificacionClick(Notificacion notificacion);
    }

    public NotificacionesAdapter(List<Notificacion> notificaciones, Context context) {
        this.notificaciones = notificaciones;
        this.context = context;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
        notifyDataSetChanged();
    }

    public void setOnNotificacionClickListener(OnNotificacionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificaciones.get(position);
        
        holder.tvTitulo.setText(notificacion.getTitulo() != null ? notificacion.getTitulo() : "");
        holder.tvMensaje.setText(notificacion.getMensaje() != null ? notificacion.getMensaje() : "");
        holder.tvFecha.setText(formatearFecha(notificacion.getFechaCreacion()));
        
        // Mostrar indicador de no leída
        boolean esNoLeida = !notificacion.isLeida();
        holder.indicadorNoLeida.setVisibility(esNoLeida ? View.VISIBLE : View.GONE);
        
        // Estilo diferente si no está leída
        holder.tvTitulo.setTypeface(null, esNoLeida ? Typeface.BOLD : Typeface.NORMAL);
        // No cambiar el fondo - las cards ya tienen su propio color del tema
        // holder.itemView.setBackgroundColor(esNoLeida ? Color.parseColor("#E3F2FD") : Color.WHITE);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificacionClick(notificacion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificaciones != null ? notificaciones.size() : 0;
    }

    private String formatearFecha(String fechaStr) {
        String resultado = fechaStr != null ? fechaStr : "";
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        Date fecha = parseFecha(fechaStr, inputFormat);
        resultado = fecha != null ? outputFormat.format(fecha) : resultado;
        
        return resultado;
    }
    
    private Date parseFecha(String fechaStr, SimpleDateFormat format) {
        Date resultado = null;
        boolean esValida = fechaStr != null && !fechaStr.isEmpty();
        
        resultado = esValida ? parseFechaInternal(fechaStr, format) : null;
        
        return resultado;
    }
    
    private Date parseFechaInternal(String fechaStr, SimpleDateFormat format) {
        Date resultado = null;
        try {
            resultado = format.parse(fechaStr);
        } catch (ParseException e) {
            resultado = null;
        }
        return resultado;
    }

    static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvMensaje;
        TextView tvFecha;
        View indicadorNoLeida;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloNotificacion);
            tvMensaje = itemView.findViewById(R.id.tvMensajeNotificacion);
            tvFecha = itemView.findViewById(R.id.tvFechaNotificacion);
            indicadorNoLeida = itemView.findViewById(R.id.indicadorNoLeida);
        }
    }
}
