package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.contratos;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Contrato;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Pago;

import java.util.ArrayList;
import java.util.List;

public class DetallePagosViewModel extends AndroidViewModel {
    
    // LiveData para datos preparados
    private MutableLiveData<String> mTitulo;
    private MutableLiveData<List<Pago>> mPagos;
    private MutableLiveData<Integer> mRecyclerVisibility;
    private MutableLiveData<Integer> mMensajeVisibility;
    private MutableLiveData<String> mMensajeTexto;
    
    public DetallePagosViewModel(@NonNull Application application) {
        super(application);
    }
    
    // Getters
    public LiveData<String> getMTitulo() {
        if (mTitulo == null) {
            mTitulo = new MutableLiveData<>();
        }
        return mTitulo;
    }
    
    public LiveData<List<Pago>> getMPagos() {
        if (mPagos == null) {
            mPagos = new MutableLiveData<>(new ArrayList<>());
        }
        return mPagos;
    }
    
    public LiveData<Integer> getMRecyclerVisibility() {
        if (mRecyclerVisibility == null) {
            mRecyclerVisibility = new MutableLiveData<>(View.GONE);
        }
        return mRecyclerVisibility;
    }
    
    public LiveData<Integer> getMMensajeVisibility() {
        if (mMensajeVisibility == null) {
            mMensajeVisibility = new MutableLiveData<>(View.VISIBLE);
        }
        return mMensajeVisibility;
    }
    
    public LiveData<String> getMMensajeTexto() {
        if (mMensajeTexto == null) {
            mMensajeTexto = new MutableLiveData<>();
        }
        return mMensajeTexto;
    }
    
    /**
     * Prepara los datos del contrato con toda la lógica de negocio.
     */
    public void cargarDatosContrato(Contrato contrato) {
        if (contrato == null) {
            mTitulo.postValue("Pagos del Contrato");
            mMensajeTexto.postValue("No hay información del contrato");
            mRecyclerVisibility.postValue(View.GONE);
            mMensajeVisibility.postValue(View.VISIBLE);
            return;
        }
        
        // Preparar título con validaciones
        if (contrato.getInmueble() != null) {
            mTitulo.postValue("Pagos - " + contrato.getInmueble().getDireccion());
        } else {
            mTitulo.postValue("Pagos del Contrato");
        }
        
        // Preparar lista de pagos con validaciones
        List<Pago> pagos = contrato.getPagos();
        
        if (pagos != null && !pagos.isEmpty()) {
            mPagos.postValue(pagos);
            mRecyclerVisibility.postValue(View.VISIBLE);
            mMensajeVisibility.postValue(View.GONE);
            Log.d("DETALLE_PAGOS", "Mostrando " + pagos.size() + " pagos");
        } else {
            mPagos.postValue(new ArrayList<>());
            mRecyclerVisibility.postValue(View.GONE);
            mMensajeVisibility.postValue(View.VISIBLE);
            mMensajeTexto.postValue("No hay pagos registrados para este contrato");
            Log.d("DETALLE_PAGOS", "No hay pagos para mostrar");
        }
    }
}
