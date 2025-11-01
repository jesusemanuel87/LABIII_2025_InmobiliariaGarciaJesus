package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;

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
        
        // Configurar listener para click en contratos
        adapter.setOnContratoClickListener(contrato -> {
            // Navegar al detalle del inquilino
            Bundle bundle = new Bundle();
            bundle.putSerializable("contrato", contrato);
            Navigation.findNavController(root).navigate(R.id.action_inquilinosFragment_to_detalleInquilinoFragment, bundle);
        });
        
        // Observer para lista de inmuebles alquilados
        mv.getMContratosActivos().observe(getViewLifecycleOwner(), new Observer<List<Contrato>>() {
            @Override
            public void onChanged(List<Contrato> contratos) {
                List<Contrato> items = contratos != null ? contratos : java.util.Collections.<Contrato>emptyList();
                adapter.setContratos(items);
                boolean hasItems = !items.isEmpty();
                recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
                tvMensaje.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                tvMensaje.setText(hasItems ? "" : "No hay inmuebles alquilados actualmente");
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
                boolean isLoading = Boolean.TRUE.equals(cargando);
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : recyclerView.getVisibility());
                tvMensaje.setVisibility(isLoading ? View.GONE : tvMensaje.getVisibility());
            }
        });
        
        // Cargar datos
        mv.cargarInmueblesAlquilados();
        
        return root;
    }
}