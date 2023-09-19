package com.example.tp07_login_menu_geolocalizacion_musica.ui.ubicacion;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp07_login_menu_geolocalizacion_musica.databinding.FragmentUbicacionBinding;

public class UbicacionFragment extends Fragment {

    private FragmentUbicacionBinding binding;

    private UbicacionViewModel mv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mv = new ViewModelProvider(this).get(UbicacionViewModel.class);
        binding = FragmentUbicacionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mv.getMLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                binding.tvUbicacion.setText(location.getLatitude()+" "+location.getLongitude());
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}