package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.CrearInmuebleRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Localidad;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Provincia;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.TipoInmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargarInmuebleViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<String> mMensaje;
    private MutableLiveData<Boolean> mCargando;
    private MutableLiveData<Boolean> mInmuebleCreado;
    private MutableLiveData<List<Provincia>> mProvincias;
    private MutableLiveData<List<Localidad>> mLocalidades;
    private MutableLiveData<List<TipoInmueble>> mTiposInmueble;

    public CargarInmuebleViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<String> getMMensaje() {
        if (mMensaje == null) {
            mMensaje = new MutableLiveData<>();
        }
        return mMensaje;
    }

    public LiveData<Boolean> getMCargando() {
        if (mCargando == null) {
            mCargando = new MutableLiveData<>(false);
        }
        return mCargando;
    }

    public LiveData<Boolean> getMInmuebleCreado() {
        if (mInmuebleCreado == null) {
            mInmuebleCreado = new MutableLiveData<>(false);
        }
        return mInmuebleCreado;
    }
    
    public LiveData<List<Provincia>> getMProvincias() {
        if (mProvincias == null) {
            mProvincias = new MutableLiveData<>();
        }
        return mProvincias;
    }
    
    public LiveData<List<Localidad>> getMLocalidades() {
        if (mLocalidades == null) {
            mLocalidades = new MutableLiveData<>();
        }
        return mLocalidades;
    }
    
    public LiveData<List<TipoInmueble>> getMTiposInmueble() {
        if (mTiposInmueble == null) {
            mTiposInmueble = new MutableLiveData<>();
        }
        return mTiposInmueble;
    }

    public void crearInmueble(String direccion, String localidad, String provincia, 
                              int tipoId, String ambientesStr, String superficieStr, int uso, 
                              String precioStr, String latitudStr, String longitudStr,
                              String imagenBase64, String imagenNombre) {
        
        // Validaciones de campos obligatorios
        if (direccion == null || direccion.trim().isEmpty()) {
            mMensaje.postValue("La dirección es obligatoria");
            return;
        }
        
        if (localidad == null || localidad.trim().isEmpty()) {
            mMensaje.postValue("La localidad es obligatoria");
            return;
        }
        
        if (provincia == null || provincia.trim().isEmpty()) {
            mMensaje.postValue("La provincia es obligatoria");
            return;
        }
        
        // Validar y convertir Ambientes
        int ambientes = 0;
        try {
            if (ambientesStr != null && !ambientesStr.trim().isEmpty()) {
                ambientes = Integer.parseInt(ambientesStr.trim());
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Ambientes inválido: debe ser un número entero");
            return;
        }
        
        if (ambientes <= 0) {
            mMensaje.postValue("La cantidad de ambientes debe ser mayor a 0");
            return;
        }
        
        // Validar y convertir Superficie
        Double superficie = null;
        try {
            if (superficieStr != null && !superficieStr.trim().isEmpty()) {
                superficie = Double.parseDouble(superficieStr.trim());
                if (superficie <= 0) {
                    mMensaje.postValue("La superficie debe ser mayor a 0");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Superficie inválida: debe ser un número decimal");
            return;
        }
        
        // Validar y convertir Precio
        Double precio = null;
        try {
            if (precioStr != null && !precioStr.trim().isEmpty()) {
                precio = Double.parseDouble(precioStr.trim());
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Precio inválido: debe ser un número decimal");
            return;
        }
        
        if (precio == null || precio <= 0) {
            mMensaje.postValue("El precio debe ser mayor a 0");
            return;
        }
        
        // Validar y convertir Latitud
        Double latitud = null;
        try {
            if (latitudStr != null && !latitudStr.trim().isEmpty()) {
                latitud = Double.parseDouble(latitudStr.trim());
                if (latitud < -90 || latitud > 90) {
                    mMensaje.postValue("Latitud inválida: debe estar entre -90 y 90");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Latitud inválida: debe ser un número decimal");
            return;
        }
        
        // Validar y convertir Longitud
        Double longitud = null;
        try {
            if (longitudStr != null && !longitudStr.trim().isEmpty()) {
                longitud = Double.parseDouble(longitudStr.trim());
                if (longitud < -180 || longitud > 180) {
                    mMensaje.postValue("Longitud inválida: debe estar entre -180 y 180");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Longitud inválida: debe ser un número decimal");
            return;
        }

        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mMensaje.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("CARGAR_INMUEBLE", "No hay token guardado");
            return;
        }

        // Crear el request
        CrearInmuebleRequest request = new CrearInmuebleRequest();
        request.setDireccion(direccion.trim());
        request.setLocalidad(localidad.trim());
        request.setProvincia(provincia.trim());
        request.setTipoId(tipoId);
        request.setAmbientes(ambientes);
        request.setSuperficie(superficie);
        request.setUso(uso);
        request.setPrecio(precio);
        request.setLatitud(latitud);
        request.setLongitud(longitud);
        
        // Agregar imagen si existe
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            request.setImagenBase64(imagenBase64);
            request.setImagenNombre(imagenNombre);
            Log.d("CARGAR_INMUEBLE", "Imagen incluida: " + imagenNombre);
        }

        Log.d("CARGAR_INMUEBLE", "Creando inmueble: " + direccion);

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<Inmueble>> call = api.crearInmueble(token, request);

        call.enqueue(new Callback<ApiResponse<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Inmueble>> call,
                                 @NonNull Response<ApiResponse<Inmueble>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Inmueble> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("CARGAR_INMUEBLE", "Inmueble creado exitosamente: " + 
                              apiResponse.getData().getId());
                        mMensaje.postValue("Inmueble creado exitosamente");
                        mInmuebleCreado.postValue(true);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al crear el inmueble";
                        Log.d("CARGAR_INMUEBLE", "Error en respuesta: " + errorMsg);
                        mMensaje.postValue(errorMsg);
                    }
                } else {
                    Log.d("CARGAR_INMUEBLE", "Error HTTP: " + response.code());
                    mMensaje.postValue("Error al crear el inmueble: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Inmueble>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("CARGAR_INMUEBLE", "Error de conexión: " + t.getMessage());
                mMensaje.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    // Cargar provincias desde API
    public void cargarProvincias() {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar provincias");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<Provincia>>> call = api.listarProvincias(token);
        
        call.enqueue(new Callback<ApiResponse<List<Provincia>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Provincia>>> call,
                                 @NonNull Response<ApiResponse<List<Provincia>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Provincia>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mProvincias.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Provincias cargadas: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Provincia>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar provincias: " + t.getMessage());
            }
        });
    }
    
    // Cargar localidades por provincia
    public void cargarLocalidadesPorProvincia(String nombreProvincia) {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar localidades");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<Localidad>>> call = api.listarLocalidadesPorProvincia(token, nombreProvincia);
        
        call.enqueue(new Callback<ApiResponse<List<Localidad>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Localidad>>> call,
                                 @NonNull Response<ApiResponse<List<Localidad>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Localidad>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mLocalidades.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Localidades cargadas: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Localidad>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar localidades: " + t.getMessage());
            }
        });
    }
    
    // Cargar tipos de inmueble
    public void cargarTiposInmueble() {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar tipos");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<TipoInmueble>>> call = api.listarTiposInmueble(token);
        
        call.enqueue(new Callback<ApiResponse<List<TipoInmueble>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<TipoInmueble>>> call,
                                 @NonNull Response<ApiResponse<List<TipoInmueble>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TipoInmueble>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mTiposInmueble.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Tipos de inmueble cargados: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<TipoInmueble>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar tipos: " + t.getMessage());
            }
        });
    }
}
