package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.bumptech.glide.Glide;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;

public class DetalleInmuebleFragment extends Fragment {

    private DetalleInmuebleViewModel mv;
    private ImageView ivImagen;
    private TextView tvDireccion, tvLocalidad, tvTipo, tvAmbientes, tvSuperficie, tvUso, tvPrecio, tvEstado;
    private SwitchCompat switchDisponible;
    private ProgressBar progressBar;
    private int inmuebleId;

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
        ivImagen = root.findViewById(R.id.ivImagenDetalle);
        tvDireccion = root.findViewById(R.id.tvDireccionDetalle);
        tvLocalidad = root.findViewById(R.id.tvLocalidadDetalle);
        tvTipo = root.findViewById(R.id.tvTipoDetalle);
        tvAmbientes = root.findViewById(R.id.tvAmbientesDetalle);
        tvSuperficie = root.findViewById(R.id.tvSuperficieDetalle);
        tvUso = root.findViewById(R.id.tvUsoDetalle);
        tvPrecio = root.findViewById(R.id.tvPrecioDetalle);
        tvEstado = root.findViewById(R.id.tvEstadoDetalle);
        switchDisponible = root.findViewById(R.id.switchDisponible);
        progressBar = root.findViewById(R.id.progressBarDetalle);
        
        // Observer para el inmueble
        mv.getMInmueble().observe(getViewLifecycleOwner(), new Observer<Inmueble>() {
            @Override
            public void onChanged(Inmueble inmueble) {
                if (inmueble != null) {
                    mostrarDatosInmueble(inmueble);
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
        
        // Observer para carga
        mv.getMActualizando().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean actualizando) {
                if (actualizando != null && actualizando) {
                    progressBar.setVisibility(View.VISIBLE);
                    switchDisponible.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    switchDisponible.setEnabled(true);
                }
            }
        });
        
        // Listener del switch
        switchDisponible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) { // Solo cuando el usuario lo cambia manualmente
                    mv.cambiarDisponibilidad(inmuebleId, isChecked);
                }
            }
        });
        
        // Cargar datos del inmueble
        mv.cargarInmueble(inmuebleId);
        
        return root;
    }

    private void mostrarDatosInmueble(Inmueble inmueble) {
        // Cargar imagen
        if (inmueble.getImagenPortadaUrl() != null && !inmueble.getImagenPortadaUrl().isEmpty()) {
            Glide.with(this)
                    .load(inmueble.getImagenPortadaUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivImagen);
        } else {
            ivImagen.setImageResource(R.drawable.ic_launcher_background);
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
        
        tvEstado.setText("Estado: " + inmueble.getEstado());
        
        // Switch sin trigger del listener
        switchDisponible.setOnCheckedChangeListener(null);
        switchDisponible.setChecked(inmueble.isDisponible());
        switchDisponible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    mv.cambiarDisponibilidad(inmuebleId, isChecked);
                }
            }
        });
    }
}
