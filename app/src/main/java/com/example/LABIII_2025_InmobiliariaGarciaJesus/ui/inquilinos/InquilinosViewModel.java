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
    private MutableLiveData<List<InquilinoContrato>> mInquilinos;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;

    public InquilinosViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<InquilinoContrato>> getMInquilinos() {
        if (mInquilinos == null) {
            mInquilinos = new MutableLiveData<>();
        }
        return mInquilinos;
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

    public void cargarInquilinos() {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("INQUILINOS", "No hay token guardado");
            return;
        }

        // Obtener todos los contratos y extraer inquilinos únicos
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
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
                        List<InquilinoContrato> inquilinosUnicos = extraerInquilinosUnicos(contratos);
                        Log.d("INQUILINOS", "Inquilinos cargados: " + inquilinosUnicos.size());
                        mInquilinos.postValue(inquilinosUnicos);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar inquilinos";
                        Log.d("INQUILINOS", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("INQUILINOS", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar inquilinos: " + response.code());
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

    private List<InquilinoContrato> extraerInquilinosUnicos(List<Contrato> contratos) {
        Set<Integer> idsInquilinos = new HashSet<>();
        List<InquilinoContrato> inquilinosUnicos = new ArrayList<>();

        for (Contrato contrato : contratos) {
            if (contrato.getInquilino() != null) {
                InquilinoContrato inquilino = contrato.getInquilino();
                // Solo agregar si no se ha agregado antes (por ID único)
                if (!idsInquilinos.contains(inquilino.getId())) {
                    idsInquilinos.add(inquilino.getId());
                    inquilinosUnicos.add(inquilino);
                }
            }
        }

        return inquilinosUnicos;
    }
}