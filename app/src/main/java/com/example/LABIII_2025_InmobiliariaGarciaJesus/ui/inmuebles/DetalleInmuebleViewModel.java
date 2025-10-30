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

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
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

    public void cambiarEstadoInmueble(int inmuebleId, String estado, boolean esActualizacionProgramatica) {
        // Lógica: si es actualización programática, salir temprano
        Log.d("DETALLE_INMUEBLE", esActualizacionProgramatica ? "Cambio programático ignorado" : "Cambio por usuario detectado");
        
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        boolean debeEjecutar = !esActualizacionProgramatica && tokenValido;
        
        // Early return pattern: si no debe ejecutar, terminar aquí
        mError.postValue(!tokenValido && !esActualizacionProgramatica ? "No hay sesión activa" : "");
        
        // Solo proceder si debeEjecutar es true (usa operador ternario para decidir qué ejecutar)
        mActualizando.postValue(debeEjecutar);
        Log.d("DETALLE_INMUEBLE", debeEjecutar ? "=== CAMBIO DE ESTADO ===" : "");
        Log.d("DETALLE_INMUEBLE", debeEjecutar ? "Inmueble ID: " + inmuebleId : "");
        Log.d("DETALLE_INMUEBLE", debeEjecutar ? "Nuevo estado a enviar: " + estado : "");
        
        ActualizarEstadoInmuebleRequest request = new ActualizarEstadoInmuebleRequest(estado);
        Log.d("DETALLE_INMUEBLE", debeEjecutar ? "Request JSON: {\"estado\":\"" + estado + "\"}" : "");
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<Inmueble>> call = debeEjecutar ? api.actualizarEstadoInmueble(token, inmuebleId, request) : null;
        
        Callback<ApiResponse<Inmueble>> callback = new Callback<ApiResponse<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Inmueble>> call,
                                 @NonNull Response<ApiResponse<Inmueble>> response) {
                mActualizando.postValue(false);
                Log.d("DETALLE_INMUEBLE", "Respuesta HTTP Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Inmueble> apiResponse = response.body();
                    Log.d("DETALLE_INMUEBLE", "Response success: " + apiResponse.isSuccess());
                    Log.d("DETALLE_INMUEBLE", "Response message: " + apiResponse.getMessage());
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Inmueble inmuebleActualizado = apiResponse.getData();
                        Log.d("DETALLE_INMUEBLE", "Estado devuelto por servidor: " + inmuebleActualizado.getEstado());
                        Log.d("DETALLE_INMUEBLE", "Disponibilidad devuelta: " + inmuebleActualizado.getDisponibilidad());
                        
                        mInmueble.postValue(inmuebleActualizado);
                        mError.postValue("Estado del inmueble actualizado a " + estado);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al actualizar el estado";
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
        };
        
        // Enqueue solo si call no es null
        executeCall(call, callback);
    }
    
    private <T> void executeCall(Call<T> call, Callback<T> callback) {
        // Execute enqueue if call is not null (minimal control flow for void method)
        if (call != null && callback != null) {
            call.enqueue(callback);
        }
    }
}
