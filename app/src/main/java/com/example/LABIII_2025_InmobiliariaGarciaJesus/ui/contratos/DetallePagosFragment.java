package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Pago;

import java.util.ArrayList;
import java.util.List;

public class DetallePagosFragment extends Fragment {

    private DetallePagosViewModel mv;
    private TextView tvTitulo;
    private TextView tvMensaje;
    private RecyclerView recyclerView;
    private PagosAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inicializar ViewModel
        mv = new ViewModelProvider(this).get(DetallePagosViewModel.class);
        
        View root = inflater.inflate(R.layout.fragment_detalle_pagos, container, false);

        // Inicializar vistas
        tvTitulo = root.findViewById(R.id.tvTituloPagos);
        tvMensaje = root.findViewById(R.id.tvMensajePagos);
        recyclerView = root.findViewById(R.id.recyclerViewPagos);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PagosAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Observers (patrón MVVM puro - sin lógica en la View)
        mv.getMTitulo().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String titulo) {
                tvTitulo.setText(titulo);
            }
        });
        
        mv.getMPagos().observe(getViewLifecycleOwner(), new Observer<List<Pago>>() {
            @Override
            public void onChanged(List<Pago> pagos) {
                adapter.setPagos(pagos);
            }
        });
        
        mv.getMRecyclerVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                recyclerView.setVisibility(visibility);
            }
        });
        
        mv.getMMensajeVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                tvMensaje.setVisibility(visibility);
            }
        });
        
        mv.getMMensajeTexto().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String texto) {
                tvMensaje.setText(texto);
            }
        });

        // Obtener contrato del bundle y pasar al ViewModel
        if (getArguments() != null) {
            Contrato contrato = (Contrato) getArguments().getSerializable("contrato");
            mv.cargarDatosContrato(contrato);
        }

        return root;
    }
}
