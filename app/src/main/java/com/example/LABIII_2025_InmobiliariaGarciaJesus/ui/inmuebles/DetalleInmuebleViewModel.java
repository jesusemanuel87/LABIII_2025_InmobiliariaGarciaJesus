package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.view.View;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ActualizarEstadoInmuebleRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.InmuebleImagen;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInmuebleViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<Inmueble> mInmueble;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mActualizando;
    
    // LiveData para datos preparados (sin lógica en Fragment)
    private MutableLiveData<List<InmuebleImagen>> mImagenes;
    private MutableLiveData<Integer> mIndicadoresVisibility;
    private MutableLiveData<String> mDireccion;
    private MutableLiveData<String> mLocalidad;
    private MutableLiveData<String> mTipo;
    private MutableLiveData<String> mAmbientes;
    private MutableLiveData<String> mSuperficie;
    private MutableLiveData<String> mUso;
    private MutableLiveData<String> mPrecio;
    private MutableLiveData<String> mDisponibilidad;
    private MutableLiveData<Integer> mDisponibilidadColor;
    private MutableLiveData<String> mEstado;
    private MutableLiveData<Boolean> mSwitchChecked;
    private MutableLiveData<Boolean> mSwitchEnabled;
    private MutableLiveData<Integer> mCantidadIndicadores;
    private MutableLiveData<Integer> mIndicadorActivo;
    private MutableLiveData<Boolean> mBotonMapaEnabled;
    private MutableLiveData<Double> mLatitud;
    private MutableLiveData<Double> mLongitud;
    private MutableLiveData<String> mTituloMapa;
    
    private int inmuebleIdActual = 0;

    public DetalleInmuebleViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }
    
    

    private String formatearNumeroArgentino(double numero) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(numero);
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
    
    // Getters para datos preparados
    public LiveData<List<InmuebleImagen>> getMImagenes() {
        if (mImagenes == null) {
            mImagenes = new MutableLiveData<>();
        }
        return mImagenes;
    }
    
    public LiveData<Integer> getMIndicadoresVisibility() {
        if (mIndicadoresVisibility == null) {
            mIndicadoresVisibility = new MutableLiveData<>(View.GONE);
        }
        return mIndicadoresVisibility;
    }
    
    public LiveData<String> getMDireccion() {
        if (mDireccion == null) {
            mDireccion = new MutableLiveData<>();
        }
        return mDireccion;
    }
    
    public LiveData<String> getMLocalidad() {
        if (mLocalidad == null) {
            mLocalidad = new MutableLiveData<>();
        }
        return mLocalidad;
    }
    
    public LiveData<String> getMTipo() {
        if (mTipo == null) {
            mTipo = new MutableLiveData<>();
        }
        return mTipo;
    }
    
    public LiveData<String> getMAmbientes() {
        if (mAmbientes == null) {
            mAmbientes = new MutableLiveData<>();
        }
        return mAmbientes;
    }
    
    public LiveData<String> getMSuperficie() {
        if (mSuperficie == null) {
            mSuperficie = new MutableLiveData<>();
        }
        return mSuperficie;
    }
    
    public LiveData<String> getMUso() {
        if (mUso == null) {
            mUso = new MutableLiveData<>();
        }
        return mUso;
    }
    
    public LiveData<String> getMPrecio() {
        if (mPrecio == null) {
            mPrecio = new MutableLiveData<>();
        }
        return mPrecio;
    }
    
    public LiveData<String> getMDisponibilidad() {
        if (mDisponibilidad == null) {
            mDisponibilidad = new MutableLiveData<>();
        }
        return mDisponibilidad;
    }
    
    public LiveData<Integer> getMDisponibilidadColor() {
        if (mDisponibilidadColor == null) {
            mDisponibilidadColor = new MutableLiveData<>();
        }
        return mDisponibilidadColor;
    }
    
    public LiveData<String> getMEstado() {
        if (mEstado == null) {
            mEstado = new MutableLiveData<>();
        }
        return mEstado;
    }
    
    public LiveData<Boolean> getMSwitchChecked() {
        if (mSwitchChecked == null) {
            mSwitchChecked = new MutableLiveData<>(false);
        }
        return mSwitchChecked;
    }
    
    public LiveData<Boolean> getMSwitchEnabled() {
        if (mSwitchEnabled == null) {
            mSwitchEnabled = new MutableLiveData<>(true);
        }
        return mSwitchEnabled;
    }
    
    public LiveData<Integer> getMCantidadIndicadores() {
        if (mCantidadIndicadores == null) {
            mCantidadIndicadores = new MutableLiveData<>(0);
        }
        return mCantidadIndicadores;
    }
    
    public LiveData<Integer> getMIndicadorActivo() {
        if (mIndicadorActivo == null) {
            mIndicadorActivo = new MutableLiveData<>(0);
        }
        return mIndicadorActivo;
    }
    
    public LiveData<Boolean> getMBotonMapaEnabled() {
        if (mBotonMapaEnabled == null) {
            mBotonMapaEnabled = new MutableLiveData<>(false);
        }
        return mBotonMapaEnabled;
    }
    
    public LiveData<Double> getMLatitud() {
        if (mLatitud == null) {
            mLatitud = new MutableLiveData<>(0.0);
        }
        return mLatitud;
    }
    
    public LiveData<Double> getMLongitud() {
        if (mLongitud == null) {
            mLongitud = new MutableLiveData<>(0.0);
        }
        return mLongitud;
    }
    
    public LiveData<String> getMTituloMapa() {
        if (mTituloMapa == null) {
            mTituloMapa = new MutableLiveData<>("");
        }
        return mTituloMapa;
    }
    
    /**
     * Actualiza la posición activa del indicador del carrusel
     */
    public void actualizarPosicionCarrusel(int posicion) {
        mIndicadorActivo.postValue(posicion);
    }

    public void cargarInmueble(int inmuebleId) {
        this.inmuebleIdActual = inmuebleId; // Guardar para uso posterior
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            Log.d("DETALLE_INMUEBLE", "No hay token guardado");
            return;
        }

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Inmueble> call = api.obtenerInmueble(token, inmuebleId);

        call.enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(@NonNull Call<Inmueble> call,
                                 @NonNull Response<Inmueble> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Inmueble inmueble = response.body();
                    Log.d("DETALLE_INMUEBLE", "Inmueble cargado: " + inmueble.getId());
                    mInmueble.postValue(inmueble);
                    prepararDatos(inmueble);
                } else {
                    Log.d("DETALLE_INMUEBLE", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar el inmueble: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inmueble> call, @NonNull Throwable t) {
                Log.d("DETALLE_INMUEBLE", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    /**
     * Maneja el cambio del switch de estado desde la UI
     * Convierte el boolean a texto "Activo"/"Inactivo"
     */
    public void onSwitchEstadoChanged(boolean isChecked) {
        String estadoTexto = isChecked ? "Activo" : "Inactivo";
        cambiarEstadoInmueble(inmuebleIdActual, estadoTexto, false);
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
        Call<Inmueble> call = debeEjecutar ? api.actualizarEstadoInmueble(token, inmuebleId, request) : null;
        
        Callback<Inmueble> callback = new Callback<Inmueble>() {
            @Override
            public void onResponse(@NonNull Call<Inmueble> call,
                                 @NonNull Response<Inmueble> response) {
                mActualizando.postValue(false);
                Log.d("DETALLE_INMUEBLE", "Respuesta HTTP Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    Inmueble inmuebleActualizado = response.body();
                    Log.d("DETALLE_INMUEBLE", "Estado devuelto por servidor: " + inmuebleActualizado.getEstado());
                    Log.d("DETALLE_INMUEBLE", "Disponibilidad devuelta: " + inmuebleActualizado.getDisponibilidad());
                    
                    mInmueble.postValue(inmuebleActualizado);
                    prepararDatos(inmuebleActualizado);
                    mError.postValue("Estado del inmueble actualizado a " + estado);
                } else {
                    Log.d("DETALLE_INMUEBLE", "Error HTTP: " + response.code());
                    mError.postValue("Error al actualizar: " + response.code());
                    // Recargar el inmueble para restaurar el estado anterior
                    cargarInmueble(inmuebleId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inmueble> call, @NonNull Throwable t) {
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
    
    /**
     * Prepara todos los datos del inmueble con validaciones y formateo.
     * TODA la lógica de negocio está aquí.
     */
    private void prepararDatos(Inmueble inmueble) {
        if (inmueble == null) {
            return;
        }
        
        // Preparar imágenes con validaciones
        List<InmuebleImagen> imagenes = inmueble.getImagenes();
        if (imagenes != null && !imagenes.isEmpty()) {
            Log.d("DETALLE_INMUEBLE", "Cargando " + imagenes.size() + " imágenes en el carrusel");
            mImagenes.postValue(imagenes);
            mIndicadoresVisibility.postValue(View.VISIBLE);
            mCantidadIndicadores.postValue(imagenes.size());
            mIndicadorActivo.postValue(0);
        } else {
            Log.d("DETALLE_INMUEBLE", "No hay imágenes para mostrar");
            mIndicadoresVisibility.postValue(View.GONE);
            mCantidadIndicadores.postValue(0);
        }
        
        // Preparar datos básicos con formateo
        mDireccion.postValue(inmueble.getDireccion());
        mLocalidad.postValue(inmueble.getLocalidad() + ", " + inmueble.getProvincia());
        mTipo.postValue("Tipo: " + inmueble.getTipoNombre());
        mAmbientes.postValue("Ambientes: " + inmueble.getAmbientes());
        
        // Superficie con validación y formato argentino
        if (inmueble.getSuperficie() != null) {
            mSuperficie.postValue("Superficie: " + formatearNumeroArgentino(inmueble.getSuperficie()) + " m²");
        } else {
            mSuperficie.postValue("Superficie: No especificada");
        }
        
        mUso.postValue("Uso: " + inmueble.getUso());
        
        // Precio con validación y formato argentino
        if (inmueble.getPrecio() != null) {
            mPrecio.postValue("$ " + formatearNumeroArgentino(inmueble.getPrecio()));
        }
        
        // Disponibilidad con lógica de colores
        String disponibilidad = inmueble.getDisponibilidad() != null ? inmueble.getDisponibilidad() : "Sin información";
        mDisponibilidad.postValue(disponibilidad);
        
        // Lógica de color según disponibilidad
        int color;
        if ("Disponible".equals(disponibilidad)) {
            color = 0xFF4CAF50; // Verde
        } else if ("No Disponible".equals(disponibilidad)) {
            color = 0xFFF44336; // Rojo
        } else if ("Reservado".equals(disponibilidad)) {
            color = 0xFFFF9800; // Naranja
        } else {
            color = 0xFF9E9E9E; // Gris
        }
        mDisponibilidadColor.postValue(color);
        
        mEstado.postValue("Estado: " + inmueble.getEstado());
        
        // Lógica del switch según estado y disponibilidad
        boolean esActivo = "Activo".equals(inmueble.getEstado());
        boolean puedeModificar = "Disponible".equals(disponibilidad);
        
        mSwitchChecked.postValue(esActivo);
        mSwitchEnabled.postValue(puedeModificar);
        
        // Preparar datos para el mapa con validación
        boolean hasCoordinates = inmueble.getLatitud() != null && inmueble.getLongitud() != null;
        mBotonMapaEnabled.postValue(hasCoordinates);
        
        if (hasCoordinates) {
            mLatitud.postValue(inmueble.getLatitud());
            mLongitud.postValue(inmueble.getLongitud());
            mTituloMapa.postValue(inmueble.getDireccion());
        } else {
            mLatitud.postValue(0.0);
            mLongitud.postValue(0.0);
            mTituloMapa.postValue("");
        }
    }
}
