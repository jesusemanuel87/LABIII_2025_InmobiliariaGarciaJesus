package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.notificaciones;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Notificacion;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Notificacion>> mNotificaciones;
    private MutableLiveData<Integer> mContador;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;

    public NotificacionesViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        // Inicializar LiveData en el constructor
        mNotificaciones = new MutableLiveData<>();
        mContador = new MutableLiveData<>(0);
        mError = new MutableLiveData<>();
        mCargando = new MutableLiveData<>(false);
    }

    public LiveData<List<Notificacion>> getMNotificaciones() {
        if (mNotificaciones == null) {
            mNotificaciones = new MutableLiveData<>();
        }
        return mNotificaciones;
    }

    public LiveData<Integer> getMContador() {
        if (mContador == null) {
            mContador = new MutableLiveData<>(0);
        }
        return mContador;
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

    public void cargarNotificaciones() {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        mError.postValue(!tokenValido ? "No hay sesión activa" : "");
        mCargando.postValue(tokenValido);

        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<List<Notificacion>> call = tokenValido && api != null ? 
            api.listarNotificaciones(token) : null;

        Callback<List<Notificacion>> callback = new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificacion>> call,
                                 @NonNull Response<List<Notificacion>> response) {
                mCargando.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Notificacion> notificaciones = response.body();
                    mNotificaciones.postValue(notificaciones);
                    Log.d("NOTIFICACIONES", "Notificaciones cargadas: " + notificaciones.size());
                } else {
                    mNotificaciones.postValue(java.util.Collections.emptyList());
                    mError.postValue("Error al cargar notificaciones");
                    Log.d("NOTIFICACIONES", "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notificacion>> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                mError.postValue("Error de conexión: " + t.getMessage());
                mNotificaciones.postValue(java.util.Collections.emptyList());
                Log.d("NOTIFICACIONES", "Error: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }

    public void cargarContadorNoLeidas() {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<Integer> call = tokenValido && api != null ? 
            api.obtenerContadorNoLeidas(token) : null;

        Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call,
                                 @NonNull Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer contador = response.body();
                    mContador.postValue(contador);
                    Log.d("NOTIFICACIONES", "Contador actualizado: " + contador);
                } else {
                    mContador.postValue(0);
                    Log.d("NOTIFICACIONES", "Contador: 0");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                mContador.postValue(0);
                Log.d("NOTIFICACIONES", "Error al cargar contador: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }

    public void marcarComoLeida(int notificacionId) {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        if (!tokenValido) {
            Log.d("NOTIFICACIONES", "No hay token válido");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Void> call = api.marcarNotificacionComoLeida(token, notificacionId);

        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                 @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NOTIFICACIONES", "Notificación marcada como leída");
                    // Recargar notificaciones y contador
                    cargarNotificaciones();
                    cargarContadorNoLeidas();
                } else {
                    Log.d("NOTIFICACIONES", "Error al marcar: " + response.code());
                    // NO mostrar error al usuario, solo recargar
                    cargarNotificaciones();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d("NOTIFICACIONES", "Error al marcar como leída: " + t.getMessage());
                // NO mostrar error al usuario, solo recargar
                cargarNotificaciones();
            }
        };
        
        executeCall(call, callback);
    }

    public void marcarTodasComoLeidas() {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        mError.postValue(!tokenValido ? "No hay sesión activa" : "");
        
        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<String> call = tokenValido && api != null ? 
            api.marcarTodasLasNotificacionesComoLeidas(token) : null;

        Callback<String> callback = new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,
                                 @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mError.postValue(response.body());
                    Log.d("NOTIFICACIONES", "Todas las notificaciones marcadas");
                } else {
                    mError.postValue("Error al marcar todas");
                    Log.d("NOTIFICACIONES", "Error HTTP: " + response.code());
                }
                
                // Recargar notificaciones y contador
                cargarNotificaciones();
                cargarContadorNoLeidas();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                mError.postValue("Error de conexión: " + t.getMessage());
                Log.d("NOTIFICACIONES", "Error: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }

    public void eliminarNotificacion(int notificacionId) {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<Void> call = tokenValido && api != null ? 
            api.eliminarNotificacion(token, notificacionId) : null;

        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                 @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    mError.postValue("Notificación eliminada");
                    Log.d("NOTIFICACIONES", "Notificación eliminada");
                } else {
                    mError.postValue("Error al eliminar");
                    Log.d("NOTIFICACIONES", "Error HTTP: " + response.code());
                }
                
                // Recargar notificaciones y contador
                cargarNotificaciones();
                cargarContadorNoLeidas();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mError.postValue("Error de conexión: " + t.getMessage());
                Log.d("NOTIFICACIONES", "Error al eliminar: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }
    
    private <T> void executeCall(Call<T> call, Callback<T> callback) {
        // Execute enqueue if call is not null (minimal control flow for void method)
        if (call != null && callback != null) {
            call.enqueue(callback);
        }
    }
}
