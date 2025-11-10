package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inquilinos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    private MutableLiveData<Contrato> mContratoSeleccionado;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;
    
    // LiveData para los datos ya preparados del detalle
    private MutableLiveData<String> mNombreCompleto;
    private MutableLiveData<String> mDni;
    private MutableLiveData<String> mEmail;
    private MutableLiveData<String> mTelefono;
    private MutableLiveData<String> mDireccionInmueble;
    private MutableLiveData<String> mPrecioAlquiler;
    private MutableLiveData<String> mFechaInicio;
    private MutableLiveData<String> mFechaFin;
    private MutableLiveData<String> mEstadoContrato;

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

    public LiveData<Contrato> getMContratoSeleccionado() {
        if (mContratoSeleccionado == null) {
            mContratoSeleccionado = new MutableLiveData<>();
        }
        return mContratoSeleccionado;
    }
    
    // Getters para datos preparados del detalle
    public LiveData<String> getMNombreCompleto() {
        if (mNombreCompleto == null) {
            mNombreCompleto = new MutableLiveData<>();
        }
        return mNombreCompleto;
    }
    
    public LiveData<String> getMDni() {
        if (mDni == null) {
            mDni = new MutableLiveData<>();
        }
        return mDni;
    }
    
    public LiveData<String> getMEmail() {
        if (mEmail == null) {
            mEmail = new MutableLiveData<>();
        }
        return mEmail;
    }
    
    public LiveData<String> getMTelefono() {
        if (mTelefono == null) {
            mTelefono = new MutableLiveData<>();
        }
        return mTelefono;
    }
    
    public LiveData<String> getMDireccionInmueble() {
        if (mDireccionInmueble == null) {
            mDireccionInmueble = new MutableLiveData<>();
        }
        return mDireccionInmueble;
    }
    
    public LiveData<String> getMPrecioAlquiler() {
        if (mPrecioAlquiler == null) {
            mPrecioAlquiler = new MutableLiveData<>();
        }
        return mPrecioAlquiler;
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
    
    public LiveData<String> getMEstadoContrato() {
        if (mEstadoContrato == null) {
            mEstadoContrato = new MutableLiveData<>();
        }
        return mEstadoContrato;
    }

    public void setContratoSeleccionado(Contrato contrato) {
        if (mContratoSeleccionado == null) {
            mContratoSeleccionado = new MutableLiveData<>();
        }
        mContratoSeleccionado.setValue(contrato);
        Log.d("INQUILINOS", "Contrato seleccionado establecido: " + 
            (contrato != null ? contrato.getId() : "null"));
        
        // Preparar todos los datos con la lógica de negocio
        prepararDatosDetalle(contrato);
    }
    
    /**
     * Prepara todos los datos del detalle con validaciones y formateo.
     * Toda la lógica de negocio y validaciones están aquí.
     */
    private void prepararDatosDetalle(Contrato contrato) {
        if (contrato == null) {
            return;
        }
        
        // Preparar datos del inquilino con validaciones
        InquilinoContrato inquilino = contrato.getInquilino();
        if (inquilino != null) {
            mNombreCompleto.postValue(inquilino.getNombreCompleto() != null ? 
                inquilino.getNombreCompleto() : "No especificado");
            mDni.postValue("DNI: " + (inquilino.getDni() != null ? inquilino.getDni() : "No especificado"));
            mEmail.postValue("Email: " + (inquilino.getEmail() != null ? inquilino.getEmail() : "No especificado"));
            mTelefono.postValue("Teléfono: " + (inquilino.getTelefono() != null ? inquilino.getTelefono() : "No especificado"));
        } else {
            mNombreCompleto.postValue("Inquilino no disponible");
            mDni.postValue("DNI: No especificado");
            mEmail.postValue("Email: No especificado");
            mTelefono.postValue("Teléfono: No especificado");
        }
        
        // Preparar datos del inmueble con validaciones
        if (contrato.getInmueble() != null) {
            mDireccionInmueble.postValue(contrato.getInmueble().getDireccion());
        } else {
            mDireccionInmueble.postValue("Inmueble no disponible");
        }
        
        // Preparar datos del contrato con formateo
        mPrecioAlquiler.postValue(String.format("$ %.2f/mes", contrato.getPrecio()));
        mFechaInicio.postValue("Inicio: " + (contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "No especificado"));
        mFechaFin.postValue("Fin: " + (contrato.getFechaFin() != null ? contrato.getFechaFin() : "No especificado"));
        mEstadoContrato.postValue("Estado: " + (contrato.getEstado() != null ? contrato.getEstado() : "No especificado"));
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
        Call<List<Contrato>> call = api.listarContratos(token);

        call.enqueue(new Callback<List<Contrato>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contrato>> call,
                                 @NonNull Response<List<Contrato>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Contrato> contratos = response.body();
                    // Filtrar solo contratos activos
                    List<Contrato> contratosActivos = filtrarContratosActivos(contratos);
                    Log.d("INQUILINOS", "Inmuebles alquilados (contratos activos): " + contratosActivos.size());
                    mContratosActivos.postValue(contratosActivos);
                } else {
                    Log.d("INQUILINOS", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar inmuebles alquilados: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contrato>> call, 
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