package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ActualizarPerfilRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Propietario;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<Propietario> mPropietario;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mModoEdicion;
    private MutableLiveData<String> mTextoBoton;
    private MutableLiveData<Integer> mIconoBoton;
    private MutableLiveData<Boolean> mSolicitarFoco;
    private MutableLiveData<Integer> mFondoCampos;
    private MutableLiveData<Boolean> mCargando;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<Propietario> getMPropietario() {
        if (mPropietario == null) {
            mPropietario = new MutableLiveData<>();
        }
        return mPropietario;
    }

    public LiveData<String> getMError() {
        if (mError == null) {
            mError = new MutableLiveData<>();
        }
        return mError;
    }

    public LiveData<Boolean> getMModoEdicion() {
        if (mModoEdicion == null) {
            mModoEdicion = new MutableLiveData<>(false);
        }
        return mModoEdicion;
    }

    public LiveData<String> getMTextoBoton() {
        if (mTextoBoton == null) {
            mTextoBoton = new MutableLiveData<>("Editar Perfil");
        }
        return mTextoBoton;
    }

    public LiveData<Integer> getMIconoBoton() {
        if (mIconoBoton == null) {
            mIconoBoton = new MutableLiveData<>(android.R.drawable.ic_menu_edit);
        }
        return mIconoBoton;
    }

    public LiveData<Boolean> getMSolicitarFoco() {
        if (mSolicitarFoco == null) {
            mSolicitarFoco = new MutableLiveData<>(false);
        }
        return mSolicitarFoco;
    }

    public LiveData<Integer> getMFondoCampos() {
        if (mFondoCampos == null) {
            mFondoCampos = new MutableLiveData<>(android.R.color.transparent);
        }
        return mFondoCampos;
    }
    
    public LiveData<Boolean> getMCargando() {
        if (mCargando == null) {
            mCargando = new MutableLiveData<>(false);
        }
        return mCargando;
    }

    public void cargarPerfil() {
        cargarPerfilNuevo();
    }
    
    private void cargarPerfilNuevo() {
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mCargando.postValue(false);
            Log.d("PERFIL", "No hay token guardado");
            return;
        }
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Propietario> call = api.obtenerPerfil(token);

        call.enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(@NonNull Call<Propietario> call, 
                                 @NonNull Response<Propietario> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Propietario propietario = response.body();
                    mPropietario.postValue(propietario);
                    Log.d("PERFIL", "Perfil cargado: " + propietario.getNombre());
                } else {
                    Log.d("PERFIL", "Error HTTP: " + response.code());
                    mError.postValue("Error al cargar perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Propietario> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("PERFIL", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void onBotonEditarClick(String nombre, String apellido, String email, String telefono) {
        Boolean modoActual = mModoEdicion.getValue();
        if (modoActual == null || !modoActual) {
            // Activar modo edición
            mModoEdicion.postValue(true);
            mTextoBoton.postValue("Guardar");
            mIconoBoton.postValue(android.R.drawable.ic_menu_save);
            mFondoCampos.postValue(R.drawable.edittext_background);
            mSolicitarFoco.postValue(true);
            Log.d("PERFIL", "Modo edición activado");
        } else {
            // Guardar cambios
            Log.d("PERFIL", "Intentando guardar cambios");
            guardarCambios(nombre, apellido, email, telefono);
        }
    }

    public void guardarCambios(String nombre, String apellido, String email, String telefono) {
        // Validar campos obligatorios
        if (nombre == null || nombre.trim().isEmpty()) {
            mError.postValue("El nombre es obligatorio");
            return;
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            mError.postValue("El apellido es obligatorio");
            return;
        }
        
        mCargando.postValue(true);
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            mCargando.postValue(false);
            return;
        }

        Propietario propietarioActual = mPropietario.getValue();
        if (propietarioActual == null) {
            mError.postValue("No hay datos del propietario");
            mCargando.postValue(false);
            return;
        }

        // Crear el request con solo los campos permitidos para actualizar
        ActualizarPerfilRequest request = new ActualizarPerfilRequest();
        request.setNombre(nombre.trim());
        request.setApellido(apellido.trim());
        request.setTelefono(telefono != null ? telefono.trim() : null);
        request.setDireccion(propietarioActual.getDireccion()); // Mantener dirección actual

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<Propietario> call = api.actualizarPerfil(token, request);

        call.enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(@NonNull Call<Propietario> call, 
                                 @NonNull Response<Propietario> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Propietario propietarioActualizado = response.body();
                    Log.d("PERFIL", "Perfil actualizado correctamente");
                    mPropietario.postValue(propietarioActualizado);
                    // Guardar en SharedPreferences
                    ApiClient.guardarPropietario(context, propietarioActualizado);
                    mModoEdicion.postValue(false);
                    mTextoBoton.postValue("Editar Perfil");
                    mIconoBoton.postValue(android.R.drawable.ic_menu_edit);
                    mFondoCampos.postValue(android.R.color.transparent);
                    mError.postValue("Perfil actualizado correctamente");
                } else {
                    Log.d("PERFIL", "Error HTTP: " + response.code());
                    mError.postValue("Error al actualizar el perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Propietario> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("PERFIL", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}