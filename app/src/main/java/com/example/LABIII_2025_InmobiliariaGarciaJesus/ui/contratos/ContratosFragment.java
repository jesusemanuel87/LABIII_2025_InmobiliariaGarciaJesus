package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;

import java.util.ArrayList;
import java.util.List;

public class ContratosFragment extends Fragment {

    private ContratosViewModel mv;
    private RecyclerView recyclerView;
    private ContratosAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvMensaje;

    public static ContratosFragment newInstance() {
        return new ContratosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contratos, container, false);
        
        mv = new ViewModelProvider(this).get(ContratosViewModel.class);
        
        // Inicializar vistas
        recyclerView = root.findViewById(R.id.recyclerViewContratos);
        progressBar = root.findViewById(R.id.progressBarContratos);
        tvMensaje = root.findViewById(R.id.tvMensajeContratos);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContratosAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);
        
        // Observer para lista de contratos
        mv.getMContratos().observe(getViewLifecycleOwner(), new Observer<List<Contrato>>() {
            @Override
            public void onChanged(List<Contrato> contratos) {
                List<Contrato> items = contratos != null ? contratos : java.util.Collections.<Contrato>emptyList();
                adapter.setContratos(items);
                boolean hasItems = !items.isEmpty();
                recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
                tvMensaje.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                tvMensaje.setText(hasItems ? "" : "No hay contratos registrados");
            }
        });
        
        // Observer para errores
        mv.getMError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Toast.makeText(getContext(), error == null ? "" : error, Toast.LENGTH_SHORT).show();
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
        mv.cargarContratos();
        
        return root;
    }
}