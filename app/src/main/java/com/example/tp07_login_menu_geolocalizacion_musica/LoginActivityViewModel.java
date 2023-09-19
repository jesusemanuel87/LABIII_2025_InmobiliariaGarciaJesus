package com.example.tp07_login_menu_geolocalizacion_musica;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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
        String mensaje;
        if (usuario.equals("user")&& clave.equals("123")){
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            mensaje="Usuario y/o clave incorrecto";
            mutable.setValue(mensaje);
        }
    }
}
