package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
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

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<ApiResponse<List<Inmueble>>> call = api.listarInmuebles(token);

        call.enqueue(new Callback<ApiResponse<List<Inmueble>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Inmueble>>> call,
                                 @NonNull Response<ApiResponse<List<Inmueble>>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Inmueble>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("INMUEBLES", "Inmuebles cargados: " + apiResponse.getData().size());
                        mInmuebles.postValue(apiResponse.getData());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar inmuebles";
                        Log.d("INMUEBLES", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("INMUEBLES", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar inmuebles: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Inmueble>>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("INMUEBLES", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void cambiarEstadoInmueble(int inmuebleId, boolean disponible) {
        String token = ApiClient.getToken(context);
        
        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            return;
        }

        // Aquí se implementaría la llamada al endpoint de actualizar estado
        // Por ahora, solo recargar la lista
        Log.d("INMUEBLES", "Cambiar estado inmueble " + inmuebleId + " a disponible: " + disponible);
        cargarInmuebles();
    }
}