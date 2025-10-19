package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratosViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Contrato>> mContratos;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;

    public ContratosViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<List<Contrato>> getMContratos() {
        if (mContratos == null) {
            mContratos = new MutableLiveData<>();
        }
        return mContratos;
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

    public void cargarContratos() {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("CONTRATOS", "No hay token guardado");
            return;
        }

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
                        Log.d("CONTRATOS", "Contratos cargados: " + apiResponse.getData().size());
                        mContratos.postValue(apiResponse.getData());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar contratos";
                        Log.d("CONTRATOS", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("CONTRATOS", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar contratos: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Contrato>>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("CONTRATOS", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void cargarContratosPorInmueble(int inmuebleId) {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            return;
        }

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<ApiResponse<List<Contrato>>> call = api.listarContratosPorInmueble(token, inmuebleId);

        call.enqueue(new Callback<ApiResponse<List<Contrato>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Contrato>>> call,
                                 @NonNull Response<ApiResponse<List<Contrato>>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Contrato>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("CONTRATOS", "Contratos del inmueble cargados: " + apiResponse.getData().size());
                        mContratos.postValue(apiResponse.getData());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al cargar contratos del inmueble";
                        Log.d("CONTRATOS", "Error en respuesta: " + errorMsg);
                        mError.postValue(errorMsg);
                    }
                } else {
                    Log.d("CONTRATOS", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar contratos: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Contrato>>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("CONTRATOS", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}