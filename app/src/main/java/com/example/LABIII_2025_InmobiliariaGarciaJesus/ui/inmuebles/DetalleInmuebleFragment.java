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
    private boolean ignorarCambiosSwitch = false;

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
        
        // Observer para el inmueble
        mv.getMInmueble().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
            @Override
            public void onChanged(Inmueble inmueble) {
                inmuebleActual = inmueble;
                mostrarDatosInmueble(inmueble != null ? inmueble : new Inmueble());
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
                Toast.makeText(getContext(), error == null ? "" : error, Toast.LENGTH_SHORT).show();
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
        
        // Listener del switch - solo llama al ViewModel si no es cambio
        switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean esActualizacionProgramatica = ignorarCambiosSwitch;
            ignorarCambiosSwitch = false;
            
            String estadoTexto = isChecked ? "Activo" : "Inactivo";
            mv.cambiarEstadoInmueble(inmuebleId, estadoTexto, esActualizacionProgramatica);
        });
        
        // Cargar datos del inmueble
        mv.cargarInmueble(inmuebleId);
        
        return root;
    }

    private void mostrarDatosInmueble(Inmueble inmueble) {
        // Cargar carrusel de imágenes
        List<InmuebleImagen> imagenes = inmueble.getImagenes();
        
        if (imagenes != null && !imagenes.isEmpty()) {
            Log.d("DETALLE_INMUEBLE", "Cargando " + imagenes.size() + " imágenes en el carrusel");
            
            // Configurar adapter del ViewPager2
            imagenesAdapter = new ImagenesCarruselAdapter(getContext(), imagenes);
            viewPagerImagenes.setAdapter(imagenesAdapter);
            
            // Configurar indicadores
            configurarIndicadores(imagenes.size());
            
            // Listener para cambiar el indicador activo
            viewPagerImagenes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    actualizarIndicadores(position);
                }
            });
            
            // Mostrar el primer indicador como activo
            actualizarIndicadores(0);
        } else {
            Log.d("DETALLE_INMUEBLE", "No hay imágenes para mostrar");
            // Si no hay imágenes, ocultar indicadores
            layoutIndicadores.setVisibility(View.GONE);
        }
        
        // Datos básicos
        tvDireccion.setText(inmueble.getDireccion());
        tvLocalidad.setText(inmueble.getLocalidad() + ", " + inmueble.getProvincia());
        tvTipo.setText("Tipo: " + inmueble.getTipoNombre());
        tvAmbientes.setText("Ambientes: " + inmueble.getAmbientes());
        
        if (inmueble.getSuperficie() != null) {
            tvSuperficie.setText(String.format("Superficie: %.2f m²", inmueble.getSuperficie()));
        } else {
            tvSuperficie.setText("Superficie: No especificada");
        }
        
        tvUso.setText("Uso: " + inmueble.getUso());
        
        if (inmueble.getPrecio() != null) {
            tvPrecio.setText(String.format("$ %.2f", inmueble.getPrecio()));
        }
        
        // Mostrar disponibilidad con colores
        String disponibilidad = inmueble.getDisponibilidad() != null ? inmueble.getDisponibilidad() : "Sin información";
        tvDisponibilidad.setText(disponibilidad);
        
        // Cambiar color según disponibilidad
        if ("Disponible".equals(disponibilidad)) {
            tvDisponibilidad.setBackgroundColor(0xFF4CAF50); // Verde
        } else if ("No Disponible".equals(disponibilidad)) {
            tvDisponibilidad.setBackgroundColor(0xFFF44336); // Rojo
        } else if ("Reservado".equals(disponibilidad)) {
            tvDisponibilidad.setBackgroundColor(0xFFFF9800); // Naranja
        } else {
            tvDisponibilidad.setBackgroundColor(0xFF9E9E9E); // Gris
        }
        
        tvEstado.setText("Estado: " + inmueble.getEstado());
        
        // Configurar switch según el estado y disponibilidad
        boolean esActivo = "Activo".equals(inmueble.getEstado());
        boolean puedeModificar = "Disponible".equals(disponibilidad);
        
        ignorarCambiosSwitch = true;
        switchDisponible.setChecked(esActivo);
        switchDisponible.setEnabled(puedeModificar);
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
