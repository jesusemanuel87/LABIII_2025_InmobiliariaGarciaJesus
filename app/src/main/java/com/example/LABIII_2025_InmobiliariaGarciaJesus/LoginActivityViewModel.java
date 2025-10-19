package com.example.LABIII_2025_InmobiliariaGarciaJesus;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.LoginRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.LoginResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

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
    
    // Método legacy - mantener compatibilidad
    public void Login(String usuario, String clave){
        loginLegacy(usuario, clave);
    }
    
    // Login con API antigua (compatibilidad)
    private void loginLegacy(String usuario, String clave){
        mCargando.postValue(true);
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<String> call = api.login(usuario, clave);
        
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    Log.d("LOGIN", "Token recibido (legacy): " + token);
                    
                    // Guardar el token usando ApiClient
                    ApiClient.guardarToken(context, token);
                    
                    // Ir a MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Log.d("LOGIN", "Error en respuesta: " + response.code());
                    mutable.postValue("Usuario y/o clave incorrecto");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("LOGIN", "Error de conexión: " + t.getMessage());
                mutable.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    // Nuevo método con API REST moderna
    public void loginNuevo(String email, String password){
        mCargando.postValue(true);
        
        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<ApiResponse<LoginResponse>> call = api.loginNuevo(loginRequest);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginResponse>> call, 
                                 @NonNull Response<ApiResponse<LoginResponse>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        Log.d("LOGIN", "Login exitoso: " + loginResponse.getPropietario().getNombreCompleto());
                        
                        // Guardar token y propietario
                        ApiClient.guardarToken(context, loginResponse.getToken());
                        ApiClient.guardarPropietario(context, loginResponse.getPropietario());
                        
                        // Ir a MainActivity
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al iniciar sesión";
                        Log.d("LOGIN", "Error en respuesta: " + errorMsg);
                        mutable.postValue(errorMsg);
                    }
                } else {
                    Log.d("LOGIN", "Error HTTP: " + response.code());
                    mutable.postValue("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginResponse>> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("LOGIN", "Error de conexión: " + t.getMessage());
                mutable.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
