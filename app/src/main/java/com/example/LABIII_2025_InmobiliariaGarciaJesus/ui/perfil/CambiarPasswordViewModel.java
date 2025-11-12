package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarPasswordViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<Boolean> mCargando;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mExito;

    public CambiarPasswordViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<Boolean> getMCargando() {
        if (mCargando == null) {
            mCargando = new MutableLiveData<>(false);
        }
        return mCargando;
    }

    public LiveData<String> getMError() {
        if (mError == null) {
            mError = new MutableLiveData<>();
        }
        return mError;
    }

    public LiveData<Boolean> getMExito() {
        if (mExito == null) {
            mExito = new MutableLiveData<>(false);
        }
        return mExito;
    }

    /**
     * Valida los datos del cambio de contraseña
     * @return null si no hay errores, o el mensaje de error si hay problemas
     */
    public String validarCambioPassword(String passwordActual, String passwordNueva, String passwordConfirmar) {
        if (passwordActual == null || passwordActual.trim().isEmpty()) {
            return "Ingrese la contraseña actual";
        }

        if (passwordNueva == null || passwordNueva.trim().isEmpty()) {
            return "Ingrese la nueva contraseña";
        }

        if (passwordNueva.length() < 6) {
            return "La contraseña debe tener al menos 6 caracteres";
        }

        if (passwordConfirmar == null || !passwordNueva.equals(passwordConfirmar)) {
            return "Las contraseñas no coinciden";
        }

        return null; // Sin errores
    }

    /**
     * Realiza el cambio de contraseña mediante API
     */
    public void cambiarPassword(String passwordActual, String passwordNueva) {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.e("CAMBIAR_PASSWORD", "No hay token");
            return;
        }

        Log.d("CAMBIAR_PASSWORD", "Token presente: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
        Log.d("CAMBIAR_PASSWORD", "Password actual length: " + passwordActual.length());
        Log.d("CAMBIAR_PASSWORD", "Password nueva length: " + passwordNueva.length());

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Void> call = api.cambiarPassword(token, passwordActual, passwordNueva);

        Log.d("CAMBIAR_PASSWORD", "Iniciando cambio de contraseña con PUT /api/Propietarios/changePassword");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                mCargando.postValue(false);

                Log.d("CAMBIAR_PASSWORD", "Respuesta recibida - Code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d("CAMBIAR_PASSWORD", "Contraseña cambiada exitosamente");
                    mExito.postValue(true);
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

                    String errorMsg = obtenerMensajeError(response.code());
                    mError.postValue(errorMsg + " (Código: " + response.code() + ")");
                    Log.e("CAMBIAR_PASSWORD", "Error HTTP: " + response.code() + " - " + errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.e("CAMBIAR_PASSWORD", "Error de conexión: " + t.getMessage(), t);
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Obtiene el mensaje de error apropiado según el código HTTP
     */
    private String obtenerMensajeError(int code) {
        switch (code) {
            case 400:
                return "Contraseña actual incorrecta";
            case 401:
                return "Sesión expirada. Por favor, inicie sesión nuevamente";
            case 500:
                return "Error en el servidor. Intente nuevamente";
            default:
                return "Error al cambiar contraseña";
        }
    }
}
