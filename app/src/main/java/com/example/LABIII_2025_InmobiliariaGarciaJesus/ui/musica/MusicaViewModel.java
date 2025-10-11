package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.musica;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;


public class MusicaViewModel extends Service {
    private MediaPlayer mp;

    public MusicaViewModel() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mp =MediaPlayer.create(this, R.raw.fortunate_son);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
