package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.view.View;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InquilinoContrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratosViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Contrato>> mContratos;
    private MutableLiveData<Contrato> mContratoSeleccionado;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;
    
    // LiveData para los datos ya preparados del detalle
    private MutableLiveData<String> mDireccionInmueble;
    private MutableLiveData<String> mInquilino;
    private MutableLiveData<String> mPrecio;
    private MutableLiveData<String> mFechaInicio;
    private MutableLiveData<String> mFechaFin;
    private MutableLiveData<String> mEstado;
    private MutableLiveData<String> mFechaCreacion;
    private MutableLiveData<String> mMesesAdeudados;
    private MutableLiveData<Integer> mMesesAdeudadosVisibility;
    private MutableLiveData<String> mImporteAdeudado;
    private MutableLiveData<Integer> mImporteAdeudadoVisibility;

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

    public LiveData<Contrato> getMContratoSeleccionado() {
        if (mContratoSeleccionado == null) {
            mContratoSeleccionado = new MutableLiveData<>();
        }
        return mContratoSeleccionado;
    }

    // Getters para datos preparados del detalle
    public LiveData<String> getMDireccionInmueble() {
        if (mDireccionInmueble == null) {
            mDireccionInmueble = new MutableLiveData<>();
        }
        return mDireccionInmueble;
    }
    
    public LiveData<String> getMInquilino() {
        if (mInquilino == null) {
            mInquilino = new MutableLiveData<>();
        }
        return mInquilino;
    }
    
    public LiveData<String> getMPrecio() {
        if (mPrecio == null) {
            mPrecio = new MutableLiveData<>();
        }
        return mPrecio;
    }
    
    public LiveData<String> getMFechaInicio() {
        if (mFechaInicio == null) {
            mFechaInicio = new MutableLiveData<>();
        }
        return mFechaInicio;
    }
    
    public LiveData<String> getMFechaFin() {
        if (mFechaFin == null) {
            mFechaFin = new MutableLiveData<>();
        }
        return mFechaFin;
    }
    
    public LiveData<String> getMEstado() {
        if (mEstado == null) {
            mEstado = new MutableLiveData<>();
        }
        return mEstado;
    }
    
    public LiveData<String> getMFechaCreacion() {
        if (mFechaCreacion == null) {
            mFechaCreacion = new MutableLiveData<>();
        }
        return mFechaCreacion;
    }
    
    public LiveData<String> getMMesesAdeudados() {
        if (mMesesAdeudados == null) {
            mMesesAdeudados = new MutableLiveData<>();
        }
        return mMesesAdeudados;
    }
    
    public LiveData<Integer> getMMesesAdeudadosVisibility() {
        if (mMesesAdeudadosVisibility == null) {
            mMesesAdeudadosVisibility = new MutableLiveData<>();
        }
        return mMesesAdeudadosVisibility;
    }
    
    public LiveData<String> getMImporteAdeudado() {
        if (mImporteAdeudado == null) {
            mImporteAdeudado = new MutableLiveData<>();
        }
        return mImporteAdeudado;
    }
    
    public LiveData<Integer> getMImporteAdeudadoVisibility() {
        if (mImporteAdeudadoVisibility == null) {
            mImporteAdeudadoVisibility = new MutableLiveData<>();
        }
        return mImporteAdeudadoVisibility;
    }

    public void setContratoSeleccionado(Contrato contrato) {
        if (mContratoSeleccionado == null) {
            mContratoSeleccionado = new MutableLiveData<>();
        }
        mContratoSeleccionado.setValue(contrato);
        Log.d("CONTRATOS", "Contrato seleccionado establecido: " + 
            (contrato != null ? contrato.getId() : "null"));
        
        // Preparar todos los datos con la lógica de negocio
        prepararDatosDetalle(contrato);
    }
    
    /**
     * Formatea una fecha ISO para mostrar solo la fecha (sin hora)
     * Ej: "2025-06-11T00:00:00" -> "2025-06-11"
     */
    private String formatearSoloFecha(String fechaISO) {
        if (fechaISO == null || fechaISO.isEmpty()) {
            return "No especificado";
        }
        
        // Si tiene el formato ISO con 'T', extraer solo la parte de la fecha
        if (fechaISO.contains("T")) {
            return fechaISO.split("T")[0];
        }
        
        // Si tiene espacio (otro formato con hora), extraer la parte antes del espacio
        if (fechaISO.contains(" ")) {
            return fechaISO.split(" ")[0];
        }
        
        // Si ya está en formato correcto, devolverlo tal cual
        return fechaISO;
    }
    
    /**
     * Prepara todos los datos del detalle con validaciones y formateo.
     * Toda la lógica de negocio y validaciones están aquí.
     */
    private void prepararDatosDetalle(Contrato contrato) {
        if (contrato == null) {
            return;
        }
        
        // Preparar datos del inmueble con validaciones
        if (contrato.getInmueble() != null) {
            mDireccionInmueble.postValue(contrato.getInmueble().getDireccion());
        } else {
            mDireccionInmueble.postValue("Inmueble no disponible");
        }
        
        // Preparar datos del inquilino con validaciones
        if (contrato.getInquilino() != null) {
            InquilinoContrato inquilino = contrato.getInquilino();
            mInquilino.postValue(inquilino.getNombreCompleto() != null ? 
                inquilino.getNombreCompleto() : "No especificado");
        } else {
            mInquilino.postValue("Inquilino no disponible");
        }
        
        // Preparar datos del contrato con formateo
        mPrecio.postValue(String.format("$ %.2f/mes", contrato.getPrecio()));
        mFechaInicio.postValue("Inicio: " + formatearSoloFecha(contrato.getFechaInicio()));
        mFechaFin.postValue("Fin: " + formatearSoloFecha(contrato.getFechaFin()));
        mEstado.postValue("Estado: " + (contrato.getEstado() != null ? contrato.getEstado() : "No especificado"));
        mFechaCreacion.postValue("Creado: " + formatearSoloFecha(contrato.getFechaCreacion()));
        
        // Preparar información de deuda con lógica de visibilidad
        if (contrato.getMesesAdeudados() != null && contrato.getMesesAdeudados() > 0) {
            mMesesAdeudados.postValue("Meses adeudados: " + contrato.getMesesAdeudados());
            mMesesAdeudadosVisibility.postValue(View.VISIBLE);
        } else {
            mMesesAdeudadosVisibility.postValue(View.GONE);
        }
        
        if (contrato.getImporteAdeudado() != null && contrato.getImporteAdeudado() > 0) {
            mImporteAdeudado.postValue(String.format("Importe adeudado: $ %.2f", contrato.getImporteAdeudado()));
            mImporteAdeudadoVisibility.postValue(View.VISIBLE);
        } else {
            mImporteAdeudadoVisibility.postValue(View.GONE);
        }
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

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
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