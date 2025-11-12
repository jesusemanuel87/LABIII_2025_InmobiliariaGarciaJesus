package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InmueblesFragment extends Fragment {

    private InmueblesViewModel mv;
    private RecyclerView recyclerView;
    private InmueblesAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvMensaje;
    private FloatingActionButton fabAgregar;

    public static InmueblesFragment newInstance() {
        return new InmueblesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inmuebles, container, false);
        
        mv = new ViewModelProvider(this).get(InmueblesViewModel.class);

        recyclerView = root.findViewById(R.id.recyclerViewInmuebles);
        progressBar = root.findViewById(R.id.progressBarInmuebles);
        tvMensaje = root.findViewById(R.id.tvMensajeInmuebles);
        fabAgregar = root.findViewById(R.id.fabAgregarInmueble);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InmueblesAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);
        
        // Listener para click en item
        adapter.setOnInmuebleClickListener(new InmueblesAdapter.OnInmuebleClickListener() {
            @Override
            public void onInmuebleClick(Inmueble inmueble) {
                // Navegar al detalle pasando el ID del inmueble
                Bundle bundle = new Bundle();
                bundle.putInt("inmuebleId", inmueble.getId());
                Navigation.findNavController(root).navigate(R.id.detalleInmuebleFragment, bundle);
            }
        });
        
        // Listener para cambio de estado
        adapter.setOnEstadoChangeListener(new InmueblesAdapter.OnEstadoChangeListener() {
            @Override
            public void onEstadoChange(Inmueble inmueble, boolean nuevoEstado) {
                String estadoTexto = nuevoEstado ? "Activo" : "Inactivo";
                mv.cambiarEstadoInmueble(inmueble.getId(), estadoTexto);
            }
        });
        
        // Listener para FAB
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al fragment de cargar inmueble
                Navigation.findNavController(root).navigate(R.id.cargarInmuebleFragment);
            }
        });
        
        // Observer para lista de inmuebles
        mv.getMInmuebles().observe(getViewLifecycleOwner(), new Observer<List<Inmueble>>() {
            @Override
            public void onChanged(List<Inmueble> inmuebles) {
                List<Inmueble> items = inmuebles != null ? inmuebles : java.util.Collections.<Inmueble>emptyList();
                adapter.setInmuebles(items);
                boolean hasItems = !items.isEmpty();
                recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
                tvMensaje.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                tvMensaje.setText(hasItems ? "" : "No hay inmuebles registrados");
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
        mv.cargarInmuebles();
        
        return root;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Recargar inmuebles al volver de otros fragments
        mv.cargarInmuebles();
    }
}