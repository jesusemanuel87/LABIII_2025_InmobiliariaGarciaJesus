package com.example.tp07_login_menu_geolocalizacion_musica.ui.ubicacion;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class UbicacionViewModel extends AndroidViewModel {

    Context context;
    FusedLocationProviderClient fused;
    MutableLiveData<Location> mLocation;

    public UbicacionViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        fused = LocationServices.getFusedLocationProviderClient(context);
    }
    public LiveData<Location> getMLocation(){
        if(mLocation == null){
            mLocation = new MutableLiveData<>();
        }
        return mLocation;
    }
    public void obtenerUltimaUbicacion() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        Task<Location> tarea = fused.getLastLocation();
        tarea.addOnSuccessListener(getApplication().getMainExecutor(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mLocation.postValue(location);
            }
        });
    }
}