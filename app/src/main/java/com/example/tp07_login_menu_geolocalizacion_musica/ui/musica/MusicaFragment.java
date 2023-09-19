package com.example.tp07_login_menu_geolocalizacion_musica.ui.musica;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tp07_login_menu_geolocalizacion_musica.databinding.FragmentMusicaBinding;

public class MusicaFragment extends Fragment {

    private FragmentMusicaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MusicaViewModel.class);
                getActivity().startService(intent);

            }
        });
        binding.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicaViewModel.class);
                getActivity().stopService(intent);
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