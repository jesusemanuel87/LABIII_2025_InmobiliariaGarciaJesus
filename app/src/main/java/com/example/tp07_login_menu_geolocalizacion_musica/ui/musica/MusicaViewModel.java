package com.example.tp07_login_menu_geolocalizacion_musica.ui.musica;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MusicaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is musica fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}