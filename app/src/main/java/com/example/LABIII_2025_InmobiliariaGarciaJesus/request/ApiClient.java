package com.example.LABIII_2025_InmobiliariaGarciaJesus.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class ApiClient {

    // Configuración de IPs por red WiFi
    /* private static final String IP_POCO = "http://192.168.248.156:5000/";
    private static final String IP_TENDA = "http://10.226.44.156:5000/";
    private static final String IP_EMULADOR = "http://10.0.2.2:5000/";
    */
    private static final String IP_DEFAULT = "https://g3kgc7hj-5000.brs.devtunnels.ms/"; // DevTunnel HTTPS

    private static MyApiInterface myApiInterface;
    private static String accessToken = null;

    /**
     * Retorna la URL base configurada (DevTunnel)
     */
    private static String getBaseUrlByNetwork(Context context) {
        Log.d("API_CLIENT", "Usando URL: " + IP_DEFAULT);
        return IP_DEFAULT;
    }

    /**
     * Obtiene la interfaz API configurada con DevTunnel
     * @param context Context de la aplicación
     */
    public static MyApiInterface getMyApiInterface(Context context){
        String baseUrl = getBaseUrlByNetwork(context);
        
        // Configurar logging interceptor para debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
            Log.d("API_HTTP", message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        
        // Crear OkHttpClient con interceptor
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Log.d("API_CLIENT", "Request URL: " + original.url());
                    Log.d("API_CLIENT", "Headers: " + original.headers().toString());
                    return chain.proceed(original);
                })
                .build();
        
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Log.d("API_CLIENT", "Base URL configurada: " + retrofit.baseUrl().toString());
        myApiInterface = retrofit.create(MyApiInterface.class);
        return myApiInterface;
    }

    /**
     * Versión legacy para compatibilidad (sin context)
     * Usa IP por defecto
     */
    public static MyApiInterface getMyApiInterface(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IP_DEFAULT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Log.d("API_CLIENT", "Base URL (default): " + retrofit.baseUrl().toString());
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

    /**
     * Obtiene la URL base configurada (DevTunnel)
     * @param context Context de la aplicación
     */
    public static String getBaseUrl(Context context){
        return getBaseUrlByNetwork(context);
    }

    /**
     * Obtiene la URL base configurada (DevTunnel) - versión sin context
     */
    public static String getBaseUrl(){
        return IP_DEFAULT;
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
        
        // === AUTENTICACIÓN (AuthApi) ===
        @POST("api/AuthApi/login")
        Call<ApiResponse<LoginResponse>> loginNuevo(@Body LoginRequest loginRequest);

        @FormUrlEncoded
        @PUT("api/Propietarios/changePassword")
        Call<Void> cambiarPassword(@Header("Authorization") String token, 
                                   @Field("currentPassword") String currentPassword,
                                   @Field("newPassword") String newPassword);

        @POST("api/AuthApi/reset-password")
        Call<ApiResponse<ResetPasswordResponse>> resetPassword(@Body ResetPasswordRequest request);

        // === PROPIETARIO (PropietarioApi) ===
        @GET("api/PropietarioApi/perfil")
        Call<ApiResponse<Propietario>> obtenerPerfil(@Header("Authorization") String token);

        @PUT("api/Propietarios/actualizar")
        Call<Propietario> actualizarPerfil(@Header("Authorization") String token, 
                                           @Body Propietario propietario);

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
        
        // === GEOREF API (Provincias y Localidades) ===
        @GET("api/GeorefApi/provincias")
        Call<ApiResponse<List<Provincia>>> listarProvincias(@Header("Authorization") String token);
        
        @GET("api/GeorefApi/localidades/{provincia}")
        Call<ApiResponse<List<Localidad>>> listarLocalidadesPorProvincia(@Header("Authorization") String token,
                                                                         @Path("provincia") String provincia);
        
        // === TIPOS DE INMUEBLE API ===
        @GET("api/TiposInmuebleApi")
        Call<ApiResponse<List<TipoInmueble>>> listarTiposInmueble(@Header("Authorization") String token);
        
        // === NOTIFICACIONES API ===
        @GET("api/NotificacionesApi")
        Call<ApiResponse<List<Notificacion>>> listarNotificaciones(@Header("Authorization") String token);
        
        @GET("api/NotificacionesApi/no-leidas")
        Call<ApiResponse<List<Notificacion>>> listarNotificacionesNoLeidas(@Header("Authorization") String token);
        
        @GET("api/NotificacionesApi/contador")
        Call<ApiResponse<Integer>> obtenerContadorNoLeidas(@Header("Authorization") String token);
        
        @PATCH("api/NotificacionesApi/{id}/marcar-leida")
        Call<ApiResponse<Void>> marcarNotificacionComoLeida(@Header("Authorization") String token,
                                                            @Path("id") int notificacionId);
        
        @PATCH("api/NotificacionesApi/marcar-todas-leidas")
        Call<ApiResponse<String>> marcarTodasLasNotificacionesComoLeidas(@Header("Authorization") String token);
        
        @HTTP(method = "DELETE", path = "api/NotificacionesApi/{id}", hasBody = false)
        Call<ApiResponse<Void>> eliminarNotificacion(@Header("Authorization") String token,
                                                     @Path("id") int notificacionId);
    }
}
