package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.ubicacion;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class UbicacionViewModel extends AndroidViewModel {

    private final Context context;
    private final FusedLocationProviderClient fused;
    private MutableLiveData<Location> mLocation;
    private MutableLiveData<MapaActual> mMapa;

    public UbicacionViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        fused = LocationServices.getFusedLocationProviderClient(context);
    }
    public LiveData<Location> getMLocation() {
        if (mLocation == null) {
            mLocation = new MutableLiveData<>();
        }
        return mLocation;
    }
    public LiveData<MapaActual> getMMapa(){
        if (mMapa==null){
            mMapa=new MutableLiveData<>();
        }
        return mMapa;
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
                if (location != null) {
                    mLocation.postValue(location);
                }
            }
        });
    }

    public void lecturaPermanente() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(5000);
        request.setFastestInterval(5000);
        request.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    mLocation.postValue(location);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions.
            return;
        }
        fused.requestLocationUpdates(request, callback, null);
    }

    public void obtenerMapa(){
        MapaActual mapaActual=new MapaActual();
        mMapa.setValue(mapaActual);
    }
    public class MapaActual implements OnMapReadyCallback {
        LatLng sanLuis = new LatLng(-33.280576, -66.332482);
      //  LatLng ulp = new LatLng(-33.150720, -66.306864);

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            MarkerOptions marcadosSanLuis = new MarkerOptions();
            marcadosSanLuis.position(sanLuis);
            marcadosSanLuis.title("San Luis");

        //    MarkerOptions marcadosUlp = new MarkerOptions();
        //    marcadosUlp.position(ulp);
        //    marcadosUlp.title("Universidad La Punta");

            googleMap.addMarker(marcadosSanLuis);
        //    googleMap.addMarker(marcadosUlp);
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sanLuis)
                    .zoom(10)
                    .bearing(45)
                    .tilt(15)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);
        }
    }
}