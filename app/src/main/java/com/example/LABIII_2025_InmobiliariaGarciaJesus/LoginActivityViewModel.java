package com.example.LABIII_2025_InmobiliariaGarciaJesus;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityViewModel extends AndroidViewModel{
    private final Context context;
    private MutableLiveData<String> mutable = new MutableLiveData<>();

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
    public void Login(String usuario, String clave){
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        Call<String> call = api.login(usuario, clave);
        
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    Log.d("LOGIN", "Token recibido: " + token);
                    
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
                Log.d("LOGIN", "Error de conexión: " + t.getMessage());
                mutable.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
