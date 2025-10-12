package com.example.LABIII_2025_InmobiliariaGarciaJesus.request;

import android.telecom.Call;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ApiClient {
    /*
    private static final String BASE_URL = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-\n" +
            "01.azurewebsites.net//";
    private static MyApiInterface myApiInterface;
    private static String accessToken = null;

    public static MyApiInterface getMyApiInterface(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .httpCLient(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Log.d("SALIDA", retrofit.baseURL().toString());
        myApiInterface = retrofit.create(MyApiInterface.class);
        return myApiInterface;
    }

    public interface MyApiInterface{
        @POST("propietario/login")
        Call<String> login(@Query("Usuario") String usuario, @Query("Clave") String clave);

        @GET("propietario")
        Call<Propietario> leer(@Header("Authorization") String token);

        @FormUrlEncoded
        @PUT("propietario/{id}")
        Call<Propietario> actualizar(@Header("Authorization") String token, @Path("id") int groupId, @Field("nombre") String nombre, @Field("apellido") String apellido, @Field("email") String email)
    }

*/
}
