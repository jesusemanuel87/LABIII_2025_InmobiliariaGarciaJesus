package com.example.tp07_login_menu_geolocalizacion_musica.ui.musica;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp07_login_menu_geolocalizacion_musica.databinding.FragmentMusicaBinding;

public class MusicaFragment extends Fragment {

    private FragmentMusicaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MusicaViewModel musicaViewModel =
                new ViewModelProvider(this).get(MusicaViewModel.class);

        binding = FragmentMusicaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}