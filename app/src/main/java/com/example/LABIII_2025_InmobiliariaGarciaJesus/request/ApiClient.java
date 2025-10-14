package com.example.LABIII_2025_InmobiliariaGarciaJesus.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Propietarios;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ApiClient {

    private static final String BASE_URL = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-01.azurewebsites.net/";
    private static MyApiInterface myApiInterface;
    private static String accessToken = null;

    public static MyApiInterface getMyApiInterface(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Log.d("SALIDA", retrofit.baseUrl().toString());
        myApiInterface = retrofit.create(MyApiInterface.class);
        return myApiInterface;
    }

    public static void guardarToken(Context context, String token){
        // Guardar el token en SharedPreferences
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", "Bearer " + token);
        editor.apply();
    }

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        accessToken = sp.getString("token", null);
        return accessToken;
    }


    public interface MyApiInterface{
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String usuario, @Field("Clave") String clave);

        @GET("api/Propietarios")
        Call<Propietarios> leer(@Header("Authorization") String token);

        @PUT("api/Propietarios/actualizar")
        Call<Propietarios> actualizar(@Header("Authorization") String token, @Body Propietarios propietario);
    }



}
