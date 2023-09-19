package com.example.tp07_login_menu_geolocalizacion_musica.ui.salir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp07_login_menu_geolocalizacion_musica.databinding.FragmentSalirBinding;

public class SalirFragment extends Fragment {

    private FragmentSalirBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSalirBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SalirDialogo.Salir(getActivity());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}