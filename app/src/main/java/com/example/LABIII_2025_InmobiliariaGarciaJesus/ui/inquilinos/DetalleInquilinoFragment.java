package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

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

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;

public class DetalleInquilinoFragment extends Fragment {

    private TextView tvNombreCompleto;
    private TextView tvDni;
    private TextView tvEmail;
    private TextView tvTelefono;
    private TextView tvDireccionInmueble;
    private TextView tvPrecioAlquiler;
    private TextView tvFechaInicio;
    private TextView tvFechaFin;
    private TextView tvEstadoContrato;
    
    private InquilinosViewModel mv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        
        mv = new ViewModelProvider(this).get(InquilinosViewModel.class);
        
        View root = inflater.inflate(R.layout.fragment_detalle_inquilino, container, false);

        // Inicializar vistas
        tvNombreCompleto = root.findViewById(R.id.tvNombreCompletoInquilino);
        tvDni = root.findViewById(R.id.tvDniInquilinoDetalle);
        tvEmail = root.findViewById(R.id.tvEmailInquilinoDetalle);
        tvTelefono = root.findViewById(R.id.tvTelefonoInquilinoDetalle);
        tvDireccionInmueble = root.findViewById(R.id.tvDireccionInmuebleDetalle);
        tvPrecioAlquiler = root.findViewById(R.id.tvPrecioAlquilerDetalle);
        tvFechaInicio = root.findViewById(R.id.tvFechaInicioDetalle);
        tvFechaFin = root.findViewById(R.id.tvFechaFinDetalle);
        tvEstadoContrato = root.findViewById(R.id.tvEstadoContratoDetalle);

        // Observers para cada campo (patrón MVVM puro - sin lógica en la View)
        mv.getMNombreCompleto().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String nombre) {
                tvNombreCompleto.setText(nombre);
            }
        });
        
        mv.getMDni().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String dni) {
                tvDni.setText(dni);
            }
        });
        
        mv.getMEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String email) {
                tvEmail.setText(email);
            }
        });
        
        mv.getMTelefono().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String telefono) {
                tvTelefono.setText(telefono);
            }
        });
        
        mv.getMDireccionInmueble().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String direccion) {
                tvDireccionInmueble.setText(direccion);
            }
        });
        
        mv.getMPrecioAlquiler().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String precio) {
                tvPrecioAlquiler.setText(precio);
            }
        });
        
        mv.getMFechaInicio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String fechaInicio) {
                tvFechaInicio.setText(fechaInicio);
            }
        });
        
        mv.getMFechaFin().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String fechaFin) {
                tvFechaFin.setText(fechaFin);
            }
        });
        
        mv.getMEstadoContrato().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String estado) {
                tvEstadoContrato.setText(estado);
            }
        });

        // Obtener contrato del bundle y establecerlo en el ViewModel
        if (getArguments() != null) {
            Contrato contrato = (Contrato) getArguments().getSerializable("contrato");
            if (contrato != null) {
                mv.setContratoSeleccionado(contrato);
            }
        }

        return root;
    }
}
