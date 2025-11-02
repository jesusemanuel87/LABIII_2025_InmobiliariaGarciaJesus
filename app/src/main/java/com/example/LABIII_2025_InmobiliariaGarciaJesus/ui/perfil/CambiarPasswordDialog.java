package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.perfil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarPasswordDialog extends DialogFragment {

    private TextInputEditText etPasswordActual;
    private TextInputEditText etPasswordNueva;
    private TextInputEditText etPasswordConfirmar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_cambiar_password, null);

        // Inicializar vistas
        etPasswordActual = view.findViewById(R.id.etPasswordActual);
        etPasswordNueva = view.findViewById(R.id.etPasswordNueva);
        etPasswordConfirmar = view.findViewById(R.id.etPasswordConfirmar);

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

        return dialog;
    }

    private void cambiarPassword() {
        String passwordActual = etPasswordActual.getText().toString().trim();
        String passwordNueva = etPasswordNueva.getText().toString().trim();
        String passwordConfirmar = etPasswordConfirmar.getText().toString().trim();

        // Validaciones
        if (passwordActual.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese la contraseña actual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordNueva.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese la nueva contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordNueva.length() < 6) {
            Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordNueva.equals(passwordConfirmar)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar al API para cambiar la contraseña
        cambiarPasswordAPI(passwordActual, passwordNueva);
    }

    private void cambiarPasswordAPI(String passwordActual, String passwordNueva) {
        String token = ApiClient.getToken(requireContext());

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show();
            Log.e("CAMBIAR_PASSWORD", "No hay token");
            return;
        }

        Log.d("CAMBIAR_PASSWORD", "Token presente: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
        Log.d("CAMBIAR_PASSWORD", "Password actual length: " + passwordActual.length());
        Log.d("CAMBIAR_PASSWORD", "Password nueva length: " + passwordNueva.length());

        // Mostrar loading
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cambiando contraseña...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Llamar al API con PUT y form-urlencoded
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(requireContext());
        Call<Void> call = api.cambiarPassword(token, passwordActual, passwordNueva);

        Log.d("CAMBIAR_PASSWORD", "Iniciando cambio de contraseña con PUT /api/Propietarios/changePassword");
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                progressDialog.dismiss();
                
                Log.d("CAMBIAR_PASSWORD", "Respuesta recibida - Code: " + response.code());
                
                if (response.isSuccessful()) {
                    Log.d("CAMBIAR_PASSWORD", "Contraseña cambiada exitosamente");
                    Toast.makeText(getContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_LONG).show();
                    dismiss();
                } else {
                    // Leer el cuerpo del error
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("CAMBIAR_PASSWORD", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("CAMBIAR_PASSWORD", "Error al leer error body: " + e.getMessage());
                    }
                    
                    String errorMsg = "Error al cambiar contraseña";
                    if (response.code() == 400) {
                        errorMsg = "Contraseña actual incorrecta";
                    } else if (response.code() == 401) {
                        errorMsg = "Sesión expirada. Por favor, inicie sesión nuevamente";
                    } else if (response.code() == 500) {
                        errorMsg = "Error en el servidor. Intente nuevamente";
                    }
                    
                    Toast.makeText(getContext(), errorMsg + " (Código: " + response.code() + ")", Toast.LENGTH_LONG).show();
                    Log.e("CAMBIAR_PASSWORD", "Error HTTP: " + response.code() + " - " + errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e("CAMBIAR_PASSWORD", "Error de conexión: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
