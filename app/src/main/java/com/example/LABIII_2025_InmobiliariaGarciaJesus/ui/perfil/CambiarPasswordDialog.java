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
                .setPositiveButton("Cambiar", (dialog, id) -> {
                    cambiarPassword();
                })
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    dismiss();
                });

        return builder.create();
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
            return;
        }

        // Mostrar loading
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cambiando contraseña...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Llamar al API con PUT y form-urlencoded
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(requireContext());
        Call<Void> call = api.cambiarPassword(token, passwordActual, passwordNueva);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Contraseña cambiada exitosamente", Toast.LENGTH_LONG).show();
                    dismiss();
                } else {
                    String errorMsg = "Error al cambiar contraseña";
                    if (response.code() == 400) {
                        errorMsg = "Contraseña actual incorrecta";
                    } else if (response.code() == 401) {
                        errorMsg = "Sesión expirada";
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("CAMBIAR_PASSWORD", "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CAMBIAR_PASSWORD", "Error: " + t.getMessage());
            }
        });
    }
}
