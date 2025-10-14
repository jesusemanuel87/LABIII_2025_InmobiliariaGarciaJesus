package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.perfil;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.databinding.FragmentPerfilBinding;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Propietarios;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel mv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mv = new ViewModelProvider(this).get(PerfilViewModel.class);
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observer para los datos del propietario
        mv.getMPropietario().observe(getViewLifecycleOwner(), new Observer<Propietarios>() {
            @Override
            public void onChanged(Propietarios propietario) {
                if (propietario != null) {
                    binding.tvNombreCompleto.setText(propietario.getNombre() + " " + propietario.getApellido());
                    binding.tvEmailHeader.setText(propietario.getEmail());
                    binding.tvDni.setText(propietario.getDni() != null ? propietario.getDni() : "No especificado");
                    binding.tvNombre.setText(propietario.getNombre());
                    binding.tvApellido.setText(propietario.getApellido());
                    binding.tvEmail.setText(propietario.getEmail());
                    binding.tvTelefono.setText(propietario.getTelefono() != null ? propietario.getTelefono() : "No especificado");
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

        // Observer para modo edición
        mv.getMModoEdicion().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean modoEdicion) {
                binding.tvNombre.setEnabled(modoEdicion);
                binding.tvApellido.setEnabled(modoEdicion);
                binding.tvEmail.setEnabled(modoEdicion);
                binding.tvTelefono.setEnabled(modoEdicion);
            }
        });

        // Observer para fondo de campos editables
        mv.getMFondoCampos().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer fondo) {
                binding.tvNombre.setBackgroundResource(fondo);
                binding.tvApellido.setBackgroundResource(fondo);
                binding.tvEmail.setBackgroundResource(fondo);
                binding.tvTelefono.setBackgroundResource(fondo);
            }
        });

        // Observer para texto del botón
        mv.getMTextoBoton().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String texto) {
                binding.btnEditar.setText(texto);
            }
        });

        // Observer para icono del botón
        mv.getMIconoBoton().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer icono) {
                binding.btnEditar.setIcon(getResources().getDrawable(icono));
            }
        });

        // Observer para solicitar foco
        mv.getMSolicitarFoco().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean solicitarFoco) {
                binding.tvNombre.requestFocus();
            }
        });

        // Listener del botón editar/guardar
        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = binding.tvNombre.getText().toString();
                String apellido = binding.tvApellido.getText().toString();
                String email = binding.tvEmail.getText().toString();
                String telefono = binding.tvTelefono.getText().toString();
                mv.onBotonEditarClick(nombre, apellido, email, telefono);
            }
        });

        // Cargar datos del perfil
        mv.cargarPerfil();
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}