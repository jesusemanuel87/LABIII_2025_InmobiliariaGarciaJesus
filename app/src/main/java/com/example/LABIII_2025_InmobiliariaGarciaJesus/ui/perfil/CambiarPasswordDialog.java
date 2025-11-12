package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.perfil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.google.android.material.textfield.TextInputEditText;

public class CambiarPasswordDialog extends DialogFragment {

    private CambiarPasswordViewModel viewModel;
    private TextInputEditText etPasswordActual;
    private TextInputEditText etPasswordNueva;
    private TextInputEditText etPasswordConfirmar;
    private ProgressDialog progressDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(CambiarPasswordViewModel.class);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cambiar_password, null);

        // Inicializar vistas
        etPasswordActual = view.findViewById(R.id.etPasswordActual);
        etPasswordNueva = view.findViewById(R.id.etPasswordNueva);
        etPasswordConfirmar = view.findViewById(R.id.etPasswordConfirmar);
        
        // Inicializar ProgressDialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cambiando contraseña...");
        progressDialog.setCancelable(false);

        builder.setView(view)
                .setTitle("Cambiar Contraseña")
                .setPositiveButton("Cambiar", null) // Configurar después para evitar auto-close
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    dismiss();
                });

        AlertDialog dialog = builder.create();
        
        // Configurar el botón positivo después de crear el diálogo para prevenir auto-close
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                cambiarPassword();
            });
        });
        
        // Observar ViewModel
        observarViewModel();

        return dialog;
    }
    
    /**
     * Configura los observers del ViewModel
     */
    private void observarViewModel() {
        // Observar estado de carga
        viewModel.getMCargando().observe(this, cargando -> {
            if (cargando != null) {
                if (cargando) {
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
        
        // Observar errores
        viewModel.getMError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
        
        // Observar éxito
        viewModel.getMExito().observe(this, exito -> {
            if (exito != null && exito) {
                Toast.makeText(getContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    /**
     * Maneja el evento de cambiar contraseña
     * Solo se encarga de UI: obtener datos y delegar al ViewModel
     */
    private void cambiarPassword() {
        String passwordActual = etPasswordActual.getText().toString().trim();
        String passwordNueva = etPasswordNueva.getText().toString().trim();
        String passwordConfirmar = etPasswordConfirmar.getText().toString().trim();

        // Validar usando ViewModel
        String errorValidacion = viewModel.validarCambioPassword(passwordActual, passwordNueva, passwordConfirmar);
        if (errorValidacion != null) {
            Toast.makeText(getContext(), errorValidacion, Toast.LENGTH_SHORT).show();
            return;
        }

        // Ejecutar cambio de contraseña mediante ViewModel
        viewModel.cambiarPassword(passwordActual, passwordNueva);
    }
}
