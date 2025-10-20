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
        
        // Inicializar vistas
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
        
        // Listener para FAB
        fabAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navegar a fragment para agregar nuevo inmueble
                Toast.makeText(getContext(), "Función de agregar inmueble próximamente", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observer para lista de inmuebles
        mv.getMInmuebles().observe(getViewLifecycleOwner(), new Observer<List<Inmueble>>() {
            @Override
            public void onChanged(List<Inmueble> inmuebles) {
                if (inmuebles != null && !inmuebles.isEmpty()) {
                    adapter.setInmuebles(inmuebles);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvMensaje.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                    tvMensaje.setText("No hay inmuebles registrados");
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
        mv.cargarInmuebles();
        
        return root;
    }
}