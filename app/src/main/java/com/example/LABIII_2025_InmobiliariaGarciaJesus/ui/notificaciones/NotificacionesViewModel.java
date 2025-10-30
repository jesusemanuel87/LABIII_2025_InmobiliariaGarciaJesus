package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.notificaciones;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
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
        Call<ApiResponse<List<Notificacion>>> call = tokenValido && api != null ? 
            api.listarNotificaciones(token) : null;

        Callback<ApiResponse<List<Notificacion>>> callback = new Callback<ApiResponse<List<Notificacion>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Notificacion>>> call,
                                 @NonNull Response<ApiResponse<List<Notificacion>>> response) {
                mCargando.postValue(false);
                
                boolean exitoso = response.isSuccessful() && response.body() != null;
                ApiResponse<List<Notificacion>> apiResponse = exitoso ? response.body() : null;
                boolean tieneData = apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null;
                
                mNotificaciones.postValue(tieneData ? apiResponse.getData() : java.util.Collections.emptyList());
                mError.postValue(!tieneData && exitoso ? apiResponse.getMessage() : !exitoso ? "Error al cargar notificaciones" : "");
                
                Log.d("NOTIFICACIONES", tieneData ? "Notificaciones cargadas: " + apiResponse.getData().size() : "Sin notificaciones");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Notificacion>>> call, @NonNull Throwable t) {
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
        Call<ApiResponse<Integer>> call = tokenValido && api != null ? 
            api.obtenerContadorNoLeidas(token) : null;

        Callback<ApiResponse<Integer>> callback = new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Integer>> call,
                                 @NonNull Response<ApiResponse<Integer>> response) {
                boolean exitoso = response.isSuccessful() && response.body() != null;
                ApiResponse<Integer> apiResponse = exitoso ? response.body() : null;
                boolean tieneData = apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null;
                
                mContador.postValue(tieneData ? apiResponse.getData() : 0);
                Log.d("NOTIFICACIONES", tieneData ? "Contador actualizado: " + apiResponse.getData() : "Contador: 0");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Integer>> call, @NonNull Throwable t) {
                mContador.postValue(0);
                Log.d("NOTIFICACIONES", "Error al cargar contador: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }

    public void marcarComoLeida(int notificacionId) {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<ApiResponse<Void>> call = tokenValido && api != null ? 
            api.marcarNotificacionComoLeida(token, notificacionId) : null;

        Callback<ApiResponse<Void>> callback = new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                 @NonNull Response<ApiResponse<Void>> response) {
                boolean exitoso = response.isSuccessful();
                Log.d("NOTIFICACIONES", exitoso ? "Notificación marcada como leída" : "Error al marcar");
                
                // Recargar notificaciones y contador
                cargarNotificaciones();
                cargarContadorNoLeidas();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                Log.d("NOTIFICACIONES", "Error al marcar como leída: " + t.getMessage());
            }
        };
        
        executeCall(call, callback);
    }

    public void marcarTodasComoLeidas() {
        String token = ApiClient.getToken(context);
        boolean tokenValido = token != null && !token.isEmpty();
        
        mError.postValue(!tokenValido ? "No hay sesión activa" : "");
        
        ApiClient.MyApiInterface api = tokenValido ? ApiClient.getMyApiInterface(context) : null;
        Call<ApiResponse<String>> call = tokenValido && api != null ? 
            api.marcarTodasLasNotificacionesComoLeidas(token) : null;

        Callback<ApiResponse<String>> callback = new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<String>> call,
                                 @NonNull Response<ApiResponse<String>> response) {
                boolean exitoso = response.isSuccessful() && response.body() != null;
                ApiResponse<String> apiResponse = exitoso ? response.body() : null;
                
                mError.postValue(exitoso && apiResponse != null ? apiResponse.getMessage() : "Error al marcar todas");
                Log.d("NOTIFICACIONES", exitoso ? "Todas las notificaciones marcadas" : "Error");
                
                // Recargar notificaciones y contador
                cargarNotificaciones();
                cargarContadorNoLeidas();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
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
        Call<ApiResponse<Void>> call = tokenValido && api != null ? 
            api.eliminarNotificacion(token, notificacionId) : null;

        Callback<ApiResponse<Void>> callback = new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call,
                                 @NonNull Response<ApiResponse<Void>> response) {
                boolean exitoso = response.isSuccessful();
                mError.postValue(exitoso ? "Notificación eliminada" : "Error al eliminar");
                Log.d("NOTIFICACIONES", exitoso ? "Notificación eliminada" : "Error");
                
                // Recargar notificaciones y contador
                cargarNotificaciones();
                cargarContadorNoLeidas();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
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
