package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Pago;

import java.util.ArrayList;
import java.util.List;

public class DetallePagosFragment extends Fragment {

    private TextView tvTitulo;
    private TextView tvMensaje;
    private RecyclerView recyclerView;
    private PagosAdapter adapter;
    
    private Contrato contrato;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalle_pagos, container, false);

        // Inicializar vistas
        tvTitulo = root.findViewById(R.id.tvTituloPagos);
        tvMensaje = root.findViewById(R.id.tvMensajePagos);
        recyclerView = root.findViewById(R.id.recyclerViewPagos);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PagosAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Obtener contrato del bundle
        if (getArguments() != null) {
            contrato = (Contrato) getArguments().getSerializable("contrato");
            if (contrato != null) {
                cargarPagos();
            }
        }

        return root;
    }

    private void cargarPagos() {
        // Establecer título con dirección del inmueble
        if (contrato.getInmueble() != null) {
            tvTitulo.setText("Pagos - " + contrato.getInmueble().getDireccion());
        } else {
            tvTitulo.setText("Pagos del Contrato");
        }

        // Cargar pagos
        List<Pago> pagos = contrato.getPagos();
        
        if (pagos != null && !pagos.isEmpty()) {
            adapter.setPagos(pagos);
            recyclerView.setVisibility(View.VISIBLE);
            tvMensaje.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvMensaje.setVisibility(View.VISIBLE);
            tvMensaje.setText("No hay pagos registrados para este contrato");
        }
    }
}
