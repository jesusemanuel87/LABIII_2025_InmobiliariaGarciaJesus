package com.example.LABIII_2025_InmobiliariaGarciaJesus.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class ApiClient {

    // URL base configurable - cambiar según el entorno
    //private static final String BASE_URL = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-01.azurewebsites.net/";
    
    // Para emulador Android usar: "http://10.0.2.2:5500/"
    // Para dispositivo físico usar: "http://TU_IP_LOCAL:5500/" (ejemplo: "http://192.168.1.105:5500/")
    // Reemplaza TU_IP_LOCAL con la IP de tu PC obtenida con 'ipconfig' en CMD
    private static final String BASE_URL = "http://10.226.44.156:5000/"; // ⚠️ CAMBIAR IP

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
        Log.d("API_CLIENT", "Base URL: " + retrofit.baseUrl().toString());
        myApiInterface = retrofit.create(MyApiInterface.class);
        return myApiInterface;
    }

    public static void guardarToken(Context context, String token){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // Agregar "Bearer " si no lo tiene
        if (!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }
        editor.putString("token", token);
        editor.apply();
        Log.d("API_CLIENT", "Token guardado correctamente");
    }

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        accessToken = sp.getString("token", null);
        return accessToken;
    }

    public static void guardarPropietario(Context context, Propietario propietario){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String propietarioJson = gson.toJson(propietario);
        editor.putString("propietario", propietarioJson);
        editor.apply();
        Log.d("API_CLIENT", "Propietario guardado correctamente");
    }

    public static Propietario getPropietario(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        String propietarioJson = sp.getString("propietario", null);
        if (propietarioJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(propietarioJson, Propietario.class);
        }
        return null;
    }

    public static void clearAuth(Context context){
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        accessToken = null;
        Log.d("API_CLIENT", "Sesión cerrada");
    }

    public static boolean isLoggedIn(Context context){
        return getToken(context) != null;
    }

    public interface MyApiInterface{
        // ==================== ENDPOINTS LEGACY (Compatibilidad) ====================
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String usuario, @Field("Clave") String clave);

        @GET("api/Propietarios")
        Call<Propietarios> leer(@Header("Authorization") String token);

        @PUT("api/Propietarios/actualizar")
        Call<Propietarios> actualizar(@Header("Authorization") String token, @Body Propietarios propietario);

        @GET("api/Inmuebles")
        Call<List<Inmueble>> obtenerInmuebles(@Header("Authorization") String token);

        @GET("api/contratos/inmueble/{id}")
        Call<Contrato> obtenerContratoPorInmueble(@Header("Authorization") String token, @Path("id") int inmuebleId);

        @GET("api/pagos/contrato/{id}")
        Call<List<Pago>> obtenerPagosPorContrato(@Header("Authorization") String token, @Path("id") int contratoId);

        // ==================== NUEVOS ENDPOINTS API REST ====================
        
        // === AUTENTICACIÓN (AuthApi) ===
        @POST("api/AuthApi/login")
        Call<ApiResponse<LoginResponse>> loginNuevo(@Body LoginRequest loginRequest);

        @POST("api/AuthApi/cambiar-password")
        Call<ApiResponseSimple> cambiarPassword(@Header("Authorization") String token, 
                                                @Body CambiarPasswordRequest request);

        @POST("api/AuthApi/reset-password")
        Call<ApiResponse<ResetPasswordResponse>> resetPassword(@Body ResetPasswordRequest request);

        // === PROPIETARIO (PropietarioApi) ===
        @GET("api/PropietarioApi/perfil")
        Call<ApiResponse<Propietario>> obtenerPerfil(@Header("Authorization") String token);

        @PUT("api/PropietarioApi/perfil")
        Call<ApiResponse<Propietario>> actualizarPerfil(@Header("Authorization") String token, 
                                                        @Body ActualizarPerfilRequest request);

        @Multipart
        @POST("api/PropietarioApi/perfil/foto")
        Call<ApiResponse<Propietario>> subirFotoPerfil(@Header("Authorization") String token, 
                                                       @Part MultipartBody.Part foto);

        // === INMUEBLES (InmueblesApi) ===
        @GET("api/InmueblesApi")
        Call<ApiResponse<List<Inmueble>>> listarInmuebles(@Header("Authorization") String token);

        @GET("api/InmueblesApi/{id}")
        Call<ApiResponse<Inmueble>> obtenerInmueble(@Header("Authorization") String token, 
                                                    @Path("id") int inmuebleId);

        @POST("api/InmueblesApi")
        Call<ApiResponse<Inmueble>> crearInmueble(@Header("Authorization") String token, 
                                                  @Body CrearInmuebleRequest request);

        @PATCH("api/InmueblesApi/{id}/estado")
        Call<ApiResponse<Inmueble>> actualizarEstadoInmueble(@Header("Authorization") String token, 
                                                             @Path("id") int inmuebleId, 
                                                             @Body ActualizarEstadoInmuebleRequest request);

        // === CONTRATOS (ContratosApi) ===
        @GET("api/ContratosApi")
        Call<ApiResponse<List<Contrato>>> listarContratos(@Header("Authorization") String token);

        @GET("api/ContratosApi/{id}")
        Call<ApiResponse<Contrato>> obtenerContrato(@Header("Authorization") String token, 
                                                    @Path("id") int contratoId);

        @GET("api/ContratosApi/inmueble/{inmuebleId}")
        Call<ApiResponse<List<Contrato>>> listarContratosPorInmueble(@Header("Authorization") String token, 
                                                                     @Path("inmuebleId") int inmuebleId);
    }
}
