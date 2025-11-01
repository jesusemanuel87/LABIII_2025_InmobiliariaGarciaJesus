package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquilinosViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Contrato>> mContratosActivos;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;

    public InquilinosViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<Contrato>> getMContratosActivos() {
        if (mContratosActivos == null) {
            mContratosActivos = new MutableLiveData<>();
        }
        return mContratosActivos;
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

    public void cargarInmueblesAlquilados() {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("INQUILINOS", "No hay token guardado");
            return;
        }

        // Obtener todos los contratos activos
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<Contrato>>> call = api.listarContratos(token);

        call.enqueue(new Callback<ApiResponse<List<Contrato>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Contrato>>> call,
                                 @NonNull Response<ApiResponse<List<Contrato>>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Contrato>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Contrato> contratos = apiResponse.getData();
                        // Filtrar solo contratos activos
                        List<Contrato> contratosActivos = filtrarContratosActivos(contratos);
                        Log.d("INQUILINOS", "Inmuebles alquilados (contratos activos): " + contratosActivos.size());
                        mContratosActivos.postValue(contratosActivos);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar inmuebles alquilados";
                        Log.d("INQUILINOS", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("INQUILINOS", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar inmuebles alquilados: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Contrato>>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("INQUILINOS", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    private List<Contrato> filtrarContratosActivos(List<Contrato> contratos) {
        List<Contrato> contratosActivos = new ArrayList<>();

        for (Contrato contrato : contratos) {
            // Filtrar solo contratos con estado "Activo" o "Vigente"
            if (contrato.getEstado() != null && 
                (contrato.getEstado().equalsIgnoreCase("Activo") || 
                 contrato.getEstado().equalsIgnoreCase("Vigente"))) {
                contratosActivos.add(contrato);
            }
        }

        return contratosActivos;
    }
}