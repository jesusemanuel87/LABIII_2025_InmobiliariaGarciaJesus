package com.example.tp07_login_menu_geolocalizacion_musica;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp07_login_menu_geolocalizacion_musica.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel mv;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mv = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mv.Login(
                        binding.edUser.getText().toString(),
                        binding.edPass.getText().toString()
                );
            }
        });

        mv.getMutable().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},2500);
        }
    }


    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}