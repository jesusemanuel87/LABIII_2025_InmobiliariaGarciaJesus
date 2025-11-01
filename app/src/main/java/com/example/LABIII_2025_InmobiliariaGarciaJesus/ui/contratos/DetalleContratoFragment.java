package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;

public class DetalleContratoFragment extends Fragment {

    private TextView tvDireccionInmueble;
    private TextView tvInquilino;
    private TextView tvPrecio;
    private TextView tvFechaInicio;
    private TextView tvFechaFin;
    private TextView tvEstado;
    private TextView tvFechaCreacion;
    private TextView tvMesesAdeudados;
    private TextView tvImporteAdeudado;
    private Button btnPagos;
    
    private Contrato contrato;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detalle_contrato, container, false);

        // Inicializar vistas
        tvDireccionInmueble = root.findViewById(R.id.tvDireccionInmuebleContrato);
        tvInquilino = root.findViewById(R.id.tvInquilinoContrato);
        tvPrecio = root.findViewById(R.id.tvPrecioContrato);
        tvFechaInicio = root.findViewById(R.id.tvFechaInicioContrato);
        tvFechaFin = root.findViewById(R.id.tvFechaFinContrato);
        tvEstado = root.findViewById(R.id.tvEstadoContratoDetalle);
        tvFechaCreacion = root.findViewById(R.id.tvFechaCreacionContrato);
        tvMesesAdeudados = root.findViewById(R.id.tvMesesAdeudados);
        tvImporteAdeudado = root.findViewById(R.id.tvImporteAdeudado);
        btnPagos = root.findViewById(R.id.btnVerPagos);

        // Obtener contrato del bundle
        if (getArguments() != null) {
            contrato = (Contrato) getArguments().getSerializable("contrato");
            if (contrato != null) {
                cargarDatos();
            }
        }

        // Configurar botón PAGOS
        btnPagos.setOnClickListener(v -> {
            if (contrato != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("contrato", contrato);
                Navigation.findNavController(root).navigate(R.id.action_detalleContratoFragment_to_detallePagosFragment, bundle);
            }
        });

        return root;
    }

    private void cargarDatos() {
        // Datos del inmueble
        if (contrato.getInmueble() != null) {
            tvDireccionInmueble.setText(contrato.getInmueble().getDireccion());
        } else {
            tvDireccionInmueble.setText("Inmueble no disponible");
        }

        // Datos del inquilino
        if (contrato.getInquilino() != null) {
            InquilinoContrato inquilino = contrato.getInquilino();
            tvInquilino.setText(inquilino.getNombreCompleto() != null ? 
                inquilino.getNombreCompleto() : "No especificado");
        } else {
            tvInquilino.setText("Inquilino no disponible");
        }

        // Datos del contrato
        tvPrecio.setText(String.format("$ %.2f/mes", contrato.getPrecio()));
        tvFechaInicio.setText("Inicio: " + (contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "No especificado"));
        tvFechaFin.setText("Fin: " + (contrato.getFechaFin() != null ? contrato.getFechaFin() : "No especificado"));
        tvEstado.setText("Estado: " + (contrato.getEstado() != null ? contrato.getEstado() : "No especificado"));
        tvFechaCreacion.setText("Creado: " + (contrato.getFechaCreacion() != null ? contrato.getFechaCreacion() : "No especificado"));
        
        // Información de deuda
        if (contrato.getMesesAdeudados() != null && contrato.getMesesAdeudados() > 0) {
            tvMesesAdeudados.setText("Meses adeudados: " + contrato.getMesesAdeudados());
            tvMesesAdeudados.setVisibility(View.VISIBLE);
        } else {
            tvMesesAdeudados.setVisibility(View.GONE);
        }
        
        if (contrato.getImporteAdeudado() != null && contrato.getImporteAdeudado() > 0) {
            tvImporteAdeudado.setText(String.format("Importe adeudado: $ %.2f", contrato.getImporteAdeudado()));
            tvImporteAdeudado.setVisibility(View.VISIBLE);
        } else {
            tvImporteAdeudado.setVisibility(View.GONE);
        }
    }
}
