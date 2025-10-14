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
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Propietarios;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<Propietarios> mPropietario;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mModoEdicion;
    private MutableLiveData<String> mTextoBoton;
    private MutableLiveData<Integer> mIconoBoton;
    private MutableLiveData<Boolean> mSolicitarFoco;
    private MutableLiveData<Integer> mFondoCampos;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<Propietarios> getMPropietario() {
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

    public void cargarPerfil() {
        // Obtener el token guardado
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        if (token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            Log.d("PERFIL", "No hay token guardado");
            return;
        }

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<Propietarios> call = api.leer(token);

        call.enqueue(new Callback<Propietarios>() {
            @Override
            public void onResponse(@NonNull Call<Propietarios> call, @NonNull Response<Propietarios> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Propietarios propietario = response.body();
                    Log.d("PERFIL", "Datos cargados: " + propietario.toString());
                    mPropietario.postValue(propietario);
                } else {
                    Log.d("PERFIL", "Error en respuesta: " + response.code());
                    mError.postValue("Error al cargar el perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Propietarios> call, @NonNull Throwable t) {
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

        if (email == null || email.trim().isEmpty()) {
            mError.postValue("El email es obligatorio");
            return;
        }

        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mError.postValue("El formato del email no es válido");
            return;
        }

        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        if (token.isEmpty()) {
            mError.postValue("No hay sesión activa");
            return;
        }

        Propietarios propietarioActual = mPropietario.getValue();
        if (propietarioActual == null) {
            mError.postValue("No hay datos del propietario");
            return;
        }

        // Validar DNI si existe
        String dni = propietarioActual.getDni();
        if (dni != null && !dni.trim().isEmpty()) {
            try {
                Integer.parseInt(dni.trim());
            } catch (NumberFormatException e) {
                mError.postValue("El DNI debe ser un número válido");
                return;
            }
        }

        // Actualizar los datos del propietario
        propietarioActual.setNombre(nombre.trim());
        propietarioActual.setApellido(apellido.trim());
        propietarioActual.setEmail(email.trim());
        propietarioActual.setTelefono(telefono != null ? telefono.trim() : null);
        propietarioActual.setClave(null);

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<Propietarios> call = api.actualizar(token, propietarioActual);

        call.enqueue(new Callback<Propietarios>() {
            @Override
            public void onResponse(@NonNull Call<Propietarios> call, @NonNull Response<Propietarios> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Propietarios propietarioActualizado = response.body();
                    Log.d("PERFIL", "Perfil actualizado correctamente");
                    mPropietario.postValue(propietarioActualizado);
                    mModoEdicion.postValue(false);
                    mTextoBoton.postValue("Editar Perfil");
                    mIconoBoton.postValue(android.R.drawable.ic_menu_edit);
                    mFondoCampos.postValue(android.R.color.transparent);
                    mError.postValue("Perfil actualizado correctamente");
                } else {
                    Log.d("PERFIL", "Error al actualizar: " + response.code());
                    mError.postValue("Error al actualizar el perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Propietarios> call, @NonNull Throwable t) {
                Log.d("PERFIL", "Error de conexión: " + t.getMessage());
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}