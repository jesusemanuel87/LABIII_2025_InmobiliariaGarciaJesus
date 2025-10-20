package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;

import java.util.ArrayList;
import java.util.List;

public class InquilinosFragment extends Fragment {

    private InquilinosViewModel mv;
    private RecyclerView recyclerView;
    private InquilinosAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvMensaje;

    public static InquilinosFragment newInstance() {
        return new InquilinosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inquilinos, container, false);
        
        mv = new ViewModelProvider(this).get(InquilinosViewModel.class);
        
        // Inicializar vistas
        recyclerView = root.findViewById(R.id.recyclerViewInquilinos);
        progressBar = root.findViewById(R.id.progressBarInquilinos);
        tvMensaje = root.findViewById(R.id.tvMensajeInquilinos);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InquilinosAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);
        
        // Observer para lista de inquilinos
        mv.getMInquilinos().observe(getViewLifecycleOwner(), new Observer<List<InquilinoContrato>>() {
            @Override
            public void onChanged(List<InquilinoContrato> inquilinos) {
                if (inquilinos != null && !inquilinos.isEmpty()) {
                    adapter.setInquilinos(inquilinos);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvMensaje.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                    tvMensaje.setText("No hay inquilinos registrados");
                }
            }
        });
        
        // Observer para errores
        mv.getMError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Observer para estado de carga
        mv.getMCargando().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean cargando) {
                if (cargando != null && cargando) {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        
        // Cargar datos
        mv.cargarInquilinos();
        
        return root;
    }
}