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
    
    // Login con API REST moderna
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
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d("LOGIN", "Login exitoso: " + loginResponse.getPropietario().getNombreCompleto());
                    
                    // Guardar token y propietario
                    ApiClient.guardarToken(context, loginResponse.getToken());
                    ApiClient.guardarPropietario(context, loginResponse.getPropietario());
                    
                    // Ir a MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Log.d("LOGIN", "Error HTTP: " + response.code());
                    mutable.postValue("Error al iniciar sesión: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("LOGIN", "Error de conexión: " + t.getMessage());
                mutable.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
