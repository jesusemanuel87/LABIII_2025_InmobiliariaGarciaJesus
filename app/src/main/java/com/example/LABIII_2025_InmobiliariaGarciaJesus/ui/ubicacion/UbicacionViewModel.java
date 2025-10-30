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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
                return;
        }
        fused.requestLocationUpdates(request, callback, null);
    }

    public void procesarArgumentos(double latitud, double longitud, String titulo) {
        
        if (latitud != 0 && longitud != 0) {
            // Mostrar mapa del inmueble específico
            obtenerMapaInmueble(latitud, longitud, titulo);
        } else {
            // Mostrar mapa por defecto de San Luis
            obtenerMapa();
        }
    }
    
    public void obtenerMapa(){
        // Mapa por defecto (San Luis)
        MapaActual mapaActual = new MapaActual(-33.280576, -66.332482, "San Luis", 15);
        mMapa.setValue(mapaActual);
    }
    
    public void obtenerMapaInmueble(double latitud, double longitud, String titulo){
        // Mapa de un inmueble específico
        MapaActual mapaActual = new MapaActual(latitud, longitud, titulo, 15);
        mMapa.setValue(mapaActual);
    }
    
    public class MapaActual implements OnMapReadyCallback {
        private LatLng ubicacion;
        private String titulo;
        private float zoom;

        // Constructor con parámetros
        public MapaActual(double latitud, double longitud, String titulo, float zoom) {
            this.ubicacion = new LatLng(latitud, longitud);
            this.titulo = titulo;
            this.zoom = zoom;
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            MarkerOptions marcador = new MarkerOptions();
            marcador.position(ubicacion);
            marcador.title(titulo);
            marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            googleMap.addMarker(marcador);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ubicacion)
                    .zoom(zoom)
                    .bearing(0)
                    .tilt(0)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);
        }
    }
}