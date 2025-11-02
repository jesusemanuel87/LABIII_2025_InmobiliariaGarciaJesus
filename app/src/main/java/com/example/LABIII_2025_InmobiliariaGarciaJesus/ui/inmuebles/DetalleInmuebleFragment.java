package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InmuebleImagen;

import java.util.ArrayList;
import java.util.List;

public class DetalleInmuebleFragment extends Fragment {

    private DetalleInmuebleViewModel mv;
    private ViewPager2 viewPagerImagenes;
    private LinearLayout layoutIndicadores;
    private TextView tvDireccion, tvLocalidad, tvTipo, tvAmbientes, tvSuperficie, tvUso, tvPrecio, tvEstado, tvDisponibilidad;
    private SwitchCompat switchDisponible;
    private Button btnVerEnMapa;
    private ProgressBar progressBar;
    private int inmuebleId;
    private Inmueble inmuebleActual;
    private ImagenesCarruselAdapter imagenesAdapter;
    private ImageView[] indicadores;

    public static DetalleInmuebleFragment newInstance(int inmuebleId) {
        DetalleInmuebleFragment fragment = new DetalleInmuebleFragment();
        Bundle args = new Bundle();
        args.putInt("inmuebleId", inmuebleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inmuebleId = getArguments().getInt("inmuebleId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalle_inmueble, container, false);
        
        mv = new ViewModelProvider(this).get(DetalleInmuebleViewModel.class);
        
        // Inicializar vistas
        viewPagerImagenes = root.findViewById(R.id.viewPagerImagenes);
        layoutIndicadores = root.findViewById(R.id.layoutIndicadores);
        tvDireccion = root.findViewById(R.id.tvDireccionDetalle);
        tvLocalidad = root.findViewById(R.id.tvLocalidadDetalle);
        tvTipo = root.findViewById(R.id.tvTipoDetalle);
        tvAmbientes = root.findViewById(R.id.tvAmbientesDetalle);
        tvSuperficie = root.findViewById(R.id.tvSuperficieDetalle);
        tvUso = root.findViewById(R.id.tvUsoDetalle);
        tvPrecio = root.findViewById(R.id.tvPrecioDetalle);
        tvDisponibilidad = root.findViewById(R.id.tvDisponibilidadDetalle);
        tvEstado = root.findViewById(R.id.tvEstadoDetalle);
        switchDisponible = root.findViewById(R.id.switchDisponible);
        btnVerEnMapa = root.findViewById(R.id.btnVerEnMapa);
        progressBar = root.findViewById(R.id.progressBarDetalle);
        
        // Observer para el inmueble (solo para referencia en botón)
        mv.getMInmueble().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
            @Override
            public void onChanged(Inmueble inmueble) {
                inmuebleActual = inmueble;
            }
        });
        
        // Listener del botón Ver en Mapa
        btnVerEnMapa.setOnClickListener(v -> {
            boolean hasCoordinates = inmuebleActual != null && inmuebleActual.getLatitud() != null && inmuebleActual.getLongitud() != null;
            String mensaje = hasCoordinates ? "" : "No hay coordenadas disponibles para este inmueble";
            
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            
            Bundle bundle = new Bundle();
            bundle.putDouble("latitud", hasCoordinates ? inmuebleActual.getLatitud() : 0);
            bundle.putDouble("longitud", hasCoordinates ? inmuebleActual.getLongitud() : 0);
            bundle.putString("titulo", hasCoordinates ? inmuebleActual.getDireccion() : "");
            
            int destination = hasCoordinates ? R.id.menu_inicio : -1;
            Navigation.findNavController(v).navigate(destination >= 0 ? destination : v.getId(), bundle);
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
        
        // Observer para carga
        mv.getMActualizando().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean actualizando) {
                boolean isUpdating = Boolean.TRUE.equals(actualizando);
                progressBar.setVisibility(isUpdating ? View.VISIBLE : View.GONE);
                switchDisponible.setEnabled(!isUpdating);
            }
        });
        
        // Observer para switch checked (patrón MVVM puro)
        mv.getMSwitchChecked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean checked) {
                switchDisponible.setOnCheckedChangeListener(null);
                switchDisponible.setChecked(checked);
                switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String estadoTexto = isChecked ? "Activo" : "Inactivo";
                    mv.cambiarEstadoInmueble(inmuebleId, estadoTexto, false);
                });
            }
        });
        
        // Observer para switch enabled
        mv.getMSwitchEnabled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                switchDisponible.setEnabled(enabled);
            }
        });
        
        // Cargar datos del inmueble
        mv.cargarInmueble(inmuebleId);
        
        // Observers para datos preparados (patrón MVVM puro - sin lógica en la View)
        mv.getMImagenes().observe(getViewLifecycleOwner(), new Observer<List<InmuebleImagen>>() {
            @Override
            public void onChanged(List<InmuebleImagen> imagenes) {
                if (imagenes != null && !imagenes.isEmpty()) {
                    imagenesAdapter = new ImagenesCarruselAdapter(getContext(), imagenes);
                    viewPagerImagenes.setAdapter(imagenesAdapter);
                    configurarIndicadores(imagenes.size());
                    viewPagerImagenes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            actualizarIndicadores(position);
                        }
                    });
                    actualizarIndicadores(0);
                }
            }
        });
        
        mv.getMIndicadoresVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                layoutIndicadores.setVisibility(visibility);
            }
        });
        
        mv.getMDireccion().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String direccion) {
                tvDireccion.setText(direccion);
            }
        });
        
        mv.getMLocalidad().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String localidad) {
                tvLocalidad.setText(localidad);
            }
        });
        
        mv.getMTipo().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String tipo) {
                tvTipo.setText(tipo);
            }
        });
        
        mv.getMAmbientes().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String ambientes) {
                tvAmbientes.setText(ambientes);
            }
        });
        
        mv.getMSuperficie().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String superficie) {
                tvSuperficie.setText(superficie);
            }
        });
        
        mv.getMUso().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String uso) {
                tvUso.setText(uso);
            }
        });
        
        mv.getMPrecio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String precio) {
                tvPrecio.setText(precio);
            }
        });
        
        mv.getMDisponibilidad().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String disponibilidad) {
                tvDisponibilidad.setText(disponibilidad);
            }
        });
        
        mv.getMDisponibilidadColor().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                tvDisponibilidad.setBackgroundColor(color);
            }
        });
        
        mv.getMEstado().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String estado) {
                tvEstado.setText(estado);
            }
        });
        
        return root;
    }
    
    private void configurarIndicadores(int cantidad) {
        layoutIndicadores.removeAllViews();
        indicadores = new ImageView[cantidad];
        
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        
        for (int i = 0; i < cantidad; i++) {
            indicadores[i] = new ImageView(getContext());
            indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_inactivo));
            indicadores[i].setLayoutParams(layoutParams);
            layoutIndicadores.addView(indicadores[i]);
        }
        
        layoutIndicadores.setVisibility(View.VISIBLE);
    }
    
    private void actualizarIndicadores(int posicionActual) {
        if (indicadores == null) return;
        
        for (int i = 0; i < indicadores.length; i++) {
            if (i == posicionActual) {
                indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_activo));
            } else {
                indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_inactivo));
            }
        }
    }
}
