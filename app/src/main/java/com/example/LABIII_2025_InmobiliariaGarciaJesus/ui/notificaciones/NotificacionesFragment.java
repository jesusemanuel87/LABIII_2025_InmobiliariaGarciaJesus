package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.notificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Notificacion;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    private NotificacionesViewModel mv;
    private RecyclerView recyclerView;
    private NotificacionesAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvMensaje;
    private MaterialButton btnMarcarTodasLeidas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        
        mv = new ViewModelProvider(this).get(NotificacionesViewModel.class);
        
        // Inicializar vistas
        recyclerView = root.findViewById(R.id.recyclerViewNotificaciones);
        progressBar = root.findViewById(R.id.progressBarNotificaciones);
        tvMensaje = root.findViewById(R.id.tvMensajeNotificaciones);
        btnMarcarTodasLeidas = root.findViewById(R.id.btnMarcarTodasLeidas);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionesAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);
        
        // Listener para click en notificación
        adapter.setOnNotificacionClickListener(notificacion -> mv.marcarComoLeida(notificacion.getId()));
        
        // Listener del botón marcar todas leídas
        btnMarcarTodasLeidas.setOnClickListener(v -> mv.marcarTodasComoLeidas());
        
        // Observer para lista de notificaciones
        mv.getMNotificaciones().observe(getViewLifecycleOwner(), new Observer<List<Notificacion>>() {
            @Override
            public void onChanged(List<Notificacion> notificaciones) {
                List<Notificacion> items = notificaciones != null ? notificaciones : Collections.<Notificacion>emptyList();
                adapter.setNotificaciones(items);
                boolean hasItems = !items.isEmpty();
                recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
                tvMensaje.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                tvMensaje.setText(hasItems ? "" : "No hay notificaciones");
                btnMarcarTodasLeidas.setVisibility(hasItems ? View.VISIBLE : View.GONE);
            }
        });
        
        // Observer para errores
        mv.getMError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                // Solo mostrar Toast si hay error real
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observer para estado de carga
        mv.getMCargando().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean cargando) {
                boolean isLoading = Boolean.TRUE.equals(cargando);
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : recyclerView.getVisibility());
                tvMensaje.setVisibility(isLoading ? View.GONE : tvMensaje.getVisibility());
            }
        });
        
        // Cargar datos
        mv.cargarNotificaciones();
        
        return root;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recargar notificaciones al volver
        mv.cargarNotificaciones();
    }
}
