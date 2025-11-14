package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmueblesViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Inmueble>> mInmuebles;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;

    public InmueblesViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<Inmueble>> getMInmuebles() {
        if (mInmuebles == null) {
            mInmuebles = new MutableLiveData<>();
        }
        return mInmuebles;
    }

    public LiveData<String> getMError() {
        if (mError == null) {
            mError = new MutableLiveData<>();
        }
        return mError;
    }

    public LiveData<Boolean> getMCargando() {
        if (mCargando == null) {
            mCargando = new MutableLiveData<>(false);
        }
        return mCargando;
    }

    public void cargarInmuebles() {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("INMUEBLES", "No hay token guardado");
            return;
        }

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<List<Inmueble>> call = api.listarInmuebles(token);

        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<List<Inmueble>> call,
                                 @NonNull Response<List<Inmueble>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Inmueble> inmuebles = response.body();
                    Log.d("INMUEBLES", "Inmuebles cargados: " + inmuebles.size());
                    
                    // Log de las URLs de imágenes para debug
                    for (Inmueble inmueble : inmuebles) {
                        Log.d("INMUEBLES", "Inmueble ID " + inmueble.getId() + 
                              " - imagenPortadaUrl: " + inmueble.getImagenPortadaUrl());
                    }
                    
                    mInmuebles.postValue(inmuebles);
                } else {
                    Log.d("INMUEBLES", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar inmuebles: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Inmueble>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("INMUEBLES", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void cambiarEstadoInmueble(int inmuebleId, String estado) {
        String token = ApiClient.getToken(context);
        
        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            return;
        }

        Log.d("INMUEBLES", "Cambiar estado inmueble " + inmuebleId + " a: " + estado);
        
        // Crear objeto Inmueble con solo el campo estado
        Inmueble inmueble = new Inmueble();
        inmueble.setEstado(estado);
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Inmueble> call = api.actualizarEstadoInmueble(token, inmuebleId, inmueble);

        call.enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(@NonNull Call<Inmueble> call,
                                 @NonNull Response<Inmueble> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("INMUEBLES", "Estado actualizado correctamente");
                    cargarInmuebles(); // Recargar la lista
                } else {
                    Log.d("INMUEBLES", "Error HTTP: " + response.code());
                    mError.postValue("Error al actualizar estado: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inmueble> call, 
                                @NonNull Throwable t) {
                Log.d("INMUEBLES", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}