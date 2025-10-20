package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ActualizarEstadoInmuebleRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInmuebleViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<Inmueble> mInmueble;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mActualizando;

    public DetalleInmuebleViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<Inmueble> getMInmueble() {
        if (mInmueble == null) {
            mInmueble = new MutableLiveData<>();
        }
        return mInmueble;
    }

    public LiveData<String> getMError() {
        if (mError == null) {
            mError = new MutableLiveData<>();
        }
        return mError;
    }

    public LiveData<Boolean> getMActualizando() {
        if (mActualizando == null) {
            mActualizando = new MutableLiveData<>(false);
        }
        return mActualizando;
    }

    public void cargarInmueble(int inmuebleId) {
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            Log.d("DETALLE_INMUEBLE", "No hay token guardado");
            return;
        }

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<ApiResponse<Inmueble>> call = api.obtenerInmueble(token, inmuebleId);

        call.enqueue(new Callback<ApiResponse<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Inmueble>> call,
                                 @NonNull Response<ApiResponse<Inmueble>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Inmueble> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("DETALLE_INMUEBLE", "Inmueble cargado: " + apiResponse.getData().getId());
                        mInmueble.postValue(apiResponse.getData());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar el inmueble";
                        Log.d("DETALLE_INMUEBLE", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("DETALLE_INMUEBLE", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar el inmueble: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Inmueble>> call, @NonNull Throwable t) {
                Log.d("DETALLE_INMUEBLE", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void cambiarDisponibilidad(int inmuebleId, boolean disponible) {
        String token = ApiClient.getToken(context);
        
        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            return;
        }

        mActualizando.postValue(true);
        Log.d("DETALLE_INMUEBLE", "Cambiando disponibilidad a: " + disponible);
        
        ActualizarEstadoInmuebleRequest request = new ActualizarEstadoInmuebleRequest(disponible);
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<ApiResponse<Inmueble>> call = api.actualizarEstadoInmueble(token, inmuebleId, request);

        call.enqueue(new Callback<ApiResponse<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Inmueble>> call,
                                 @NonNull Response<ApiResponse<Inmueble>> response) {
                mActualizando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Inmueble> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("DETALLE_INMUEBLE", "Disponibilidad actualizada correctamente");
                        mInmueble.postValue(apiResponse.getData());
                        mError.postValue("Disponibilidad actualizada correctamente");
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al actualizar la disponibilidad";
                        Log.d("DETALLE_INMUEBLE", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                        // Recargar el inmueble para restaurar el estado anterior
                        cargarInmueble(inmuebleId);
                    }
                } else {
                    Log.d("DETALLE_INMUEBLE", "Error HTTP: " + response.code());
                    mError.postValue("Error al actualizar: " + response.code());
                    // Recargar el inmueble para restaurar el estado anterior
                    cargarInmueble(inmuebleId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Inmueble>> call, @NonNull Throwable t) {
                mActualizando.postValue(false);
                Log.d("DETALLE_INMUEBLE", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
                // Recargar el inmueble para restaurar el estado anterior
                cargarInmueble(inmuebleId);
            }
        });
    }
}
