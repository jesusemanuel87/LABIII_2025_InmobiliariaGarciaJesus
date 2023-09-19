package com.example.tp07_login_menu_geolocalizacion_musica.ui.salir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import com.example.tp07_login_menu_geolocalizacion_musica.MainActivity;

public class SalirDialogo {

    public static void Salir (Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle("Salir")
                .setMessage("¿Salir de la aplicación?")
                .setPositiveButton("Si", (dialogInterface, i) -> System.exit(0))
                .setNegativeButton("No", (dialogInterface, i) -> {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                })
                .show();
    }
}