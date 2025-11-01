package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    
    private Contrato contrato;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        // Obtener contrato del bundle
        if (getArguments() != null) {
            contrato = (Contrato) getArguments().getSerializable("contrato");
            if (contrato != null) {
                cargarDatos();
            }
        }

        return root;
    }

    private void cargarDatos() {
        // Datos del inquilino
        InquilinoContrato inquilino = contrato.getInquilino();
        if (inquilino != null) {
            tvNombreCompleto.setText(inquilino.getNombreCompleto() != null ? 
                inquilino.getNombreCompleto() : "No especificado");
            tvDni.setText("DNI: " + (inquilino.getDni() != null ? inquilino.getDni() : "No especificado"));
            tvEmail.setText("Email: " + (inquilino.getEmail() != null ? inquilino.getEmail() : "No especificado"));
            tvTelefono.setText("Teléfono: " + (inquilino.getTelefono() != null ? inquilino.getTelefono() : "No especificado"));
        } else {
            tvNombreCompleto.setText("Inquilino no disponible");
            tvDni.setText("DNI: No especificado");
            tvEmail.setText("Email: No especificado");
            tvTelefono.setText("Teléfono: No especificado");
        }

        // Datos del inmueble
        if (contrato.getInmueble() != null) {
            tvDireccionInmueble.setText(contrato.getInmueble().getDireccion());
        } else {
            tvDireccionInmueble.setText("Inmueble no disponible");
        }

        // Datos del contrato
        tvPrecioAlquiler.setText(String.format("$ %.2f/mes", contrato.getPrecio()));
        tvFechaInicio.setText("Inicio: " + (contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "No especificado"));
        tvFechaFin.setText("Fin: " + (contrato.getFechaFin() != null ? contrato.getFechaFin() : "No especificado"));
        tvEstadoContrato.setText("Estado: " + (contrato.getEstado() != null ? contrato.getEstado() : "No especificado"));
    }
}
