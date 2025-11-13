package com.example.LABIII_2025_InmobiliariaGarciaJesus;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.LoginRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.LoginResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Propietario;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityViewModel extends AndroidViewModel{
    private final Context context;
    private MutableLiveData<String> mutable = new MutableLiveData<>();
    private MutableLiveData<Boolean> mCargando = new MutableLiveData<>(false);

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }
    
    public LiveData<String> getMutable(){
        if(mutable==null){
            mutable = new MutableLiveData<>();
        }
        return  mutable;
    }
    
    public LiveData<Boolean> getMCargando(){
        if(mCargando==null){
            mCargando = new MutableLiveData<>(false);
        }
        return  mCargando;
    }
    
    // Login con API REST - Backend devuelve datos directos
    public void login(String email, String password){
        mCargando.postValue(true);
        Log.d("LOGIN", "Intentando login con email: " + email);
        
        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<LoginResponse> call = api.login(loginRequest);
        Log.d("LOGIN", "URL: " + call.request().url().toString());
        
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, 
                                 @NonNull Response<LoginResponse> response) {
                Log.d("LOGIN", "===== onResponse LLAMADO =====");
                Log.d("LOGIN", "Response code: " + response.code());
                
                mCargando.postValue(false);
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginData = response.body(); // Directo sin wrapper
                        
                        Log.d("LOGIN", "=== RESPUESTA DEL BACKEND ===");
                        Log.d("LOGIN", "Token: " + (loginData.getToken() != null ? "OK" : "NULL"));
                        Log.d("LOGIN", "Propietario: " + (loginData.getPropietario() != null ? "OK" : "NULL"));
                        
                        // Validar token
                        if (loginData.getToken() == null || loginData.getToken().isEmpty()) {
                            Log.e("LOGIN", "ERROR: Token es null o vacío");
                            mutable.postValue("Error: No se recibió token de autenticación");
                            return;
                        }
                        
                        // Validar propietario
                        if (loginData.getPropietario() == null) {
                            Log.e("LOGIN", "ERROR: Propietario es null");
                            mutable.postValue("Error: No se recibieron datos del usuario");
                            return;
                        }
                        
                        // Log de datos del propietario
                        Propietario prop = loginData.getPropietario();
                        Log.d("LOGIN", "Propietario ID: " + prop.getId());
                        Log.d("LOGIN", "Nombre: " + prop.getNombre());
                        Log.d("LOGIN", "Apellido: " + prop.getApellido());
                        Log.d("LOGIN", "Email: " + prop.getEmail());
                        
                        Log.d("LOGIN", "✅ Login exitoso para: " + prop.getNombre() + " " + prop.getApellido());
                        
                        // Guardar token y propietario
                        ApiClient.guardarToken(context, loginData.getToken());
                        ApiClient.guardarPropietario(context, loginData.getPropietario());
                        
                        // Ir a MainActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        
                    } else {
                        Log.e("LOGIN", "Error: Response no exitoso o body es null");
                        Log.e("LOGIN", "Response code: " + response.code());
                        
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                Log.e("LOGIN", "Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e("LOGIN", "No se pudo leer error body: " + e.getMessage());
                            }
                        }
                        
                        mutable.postValue("Error al iniciar sesión: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e("LOGIN", "EXCEPCIÓN en onResponse: " + e.getMessage());
                    Log.e("LOGIN", "Stack trace: ", e);
                    mutable.postValue("Error procesando respuesta: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.e("LOGIN", "Error de conexión: " + t.getMessage());
                Log.e("LOGIN", "Stack trace: ", t);
                mutable.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
