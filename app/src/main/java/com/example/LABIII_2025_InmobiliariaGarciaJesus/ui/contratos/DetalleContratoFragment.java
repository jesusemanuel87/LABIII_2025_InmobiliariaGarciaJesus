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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;

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
    
    private ContratosViewModel mv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inicializar ViewModel
        mv = new ViewModelProvider(this).get(ContratosViewModel.class);
        
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

        // Observers para cada campo (patrón MVVM puro - sin lógica en la View)
        mv.getMDireccionInmueble().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String direccion) {
                tvDireccionInmueble.setText(direccion);
            }
        });
        
        mv.getMInquilino().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String inquilino) {
                tvInquilino.setText(inquilino);
            }
        });
        
        mv.getMPrecio().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String precio) {
                tvPrecio.setText(precio);
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
        
        mv.getMEstado().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String estado) {
                tvEstado.setText(estado);
            }
        });
        
        mv.getMFechaCreacion().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String fechaCreacion) {
                tvFechaCreacion.setText(fechaCreacion);
            }
        });
        
        mv.getMMesesAdeudados().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String mesesAdeudados) {
                tvMesesAdeudados.setText(mesesAdeudados);
            }
        });
        
        mv.getMMesesAdeudadosVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                tvMesesAdeudados.setVisibility(visibility);
            }
        });
        
        mv.getMImporteAdeudado().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String importeAdeudado) {
                tvImporteAdeudado.setText(importeAdeudado);
            }
        });
        
        mv.getMImporteAdeudadoVisibility().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                tvImporteAdeudado.setVisibility(visibility);
            }
        });
        
        // Observer para configurar el botón de pagos
        mv.getMContratoSeleccionado().observe(getViewLifecycleOwner(), new Observer<Contrato>() {
            @Override
            public void onChanged(Contrato contrato) {
                if (contrato != null) {
                    btnPagos.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("contrato", contrato);
                        Navigation.findNavController(root).navigate(R.id.action_detalleContratoFragment_to_detallePagosFragment, bundle);
                    });
                }
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
