package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.ApiResponse;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.CrearInmuebleRequest;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Inmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Localidad;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Provincia;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.TipoInmueble;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargarInmuebleViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<String> mMensaje;
    private MutableLiveData<Boolean> mCargando;
    private MutableLiveData<Boolean> mInmuebleCreado;
    private MutableLiveData<List<Provincia>> mProvincias;
    private MutableLiveData<List<Localidad>> mLocalidades;
    private MutableLiveData<List<TipoInmueble>> mTiposInmueble;

    public CargarInmuebleViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public LiveData<String> getMMensaje() {
        if (mMensaje == null) {
            mMensaje = new MutableLiveData<>();
        }
        return mMensaje;
    }

    public LiveData<Boolean> getMCargando() {
        if (mCargando == null) {
            mCargando = new MutableLiveData<>(false);
        }
        return mCargando;
    }

    public LiveData<Boolean> getMInmuebleCreado() {
        if (mInmuebleCreado == null) {
            mInmuebleCreado = new MutableLiveData<>(false);
        }
        return mInmuebleCreado;
    }
    
    public LiveData<List<Provincia>> getMProvincias() {
        if (mProvincias == null) {
            mProvincias = new MutableLiveData<>();
        }
        return mProvincias;
    }
    
    public LiveData<List<Localidad>> getMLocalidades() {
        if (mLocalidades == null) {
            mLocalidades = new MutableLiveData<>();
        }
        return mLocalidades;
    }
    
    public LiveData<List<TipoInmueble>> getMTiposInmueble() {
        if (mTiposInmueble == null) {
            mTiposInmueble = new MutableLiveData<>();
        }
        return mTiposInmueble;
    }

    /**
     * Versión mejorada: recibe objetos completos del Fragment
     */
    public void crearInmueble(String direccion, Localidad localidad, Provincia provincia,
                              TipoInmueble tipo, String ambientesStr, String superficieStr, int uso,
                              String precioStr, String latitudStr, String longitudStr,
                              String imagenBase64, String imagenNombre) {
        
        // Validar objetos recibidos
        if (provincia == null) {
            mMensaje.postValue("Debe seleccionar una provincia");
            return;
        }
        
        if (localidad == null) {
            mMensaje.postValue("Debe seleccionar una localidad");
            return;
        }
        
        if (tipo == null) {
            mMensaje.postValue("Debe seleccionar un tipo de inmueble");
            return;
        }
        
        // Extraer datos de los objetos
        String provinciaNombre = provincia.getNombre();
        String localidadNombre = localidad.getNombre();
        int tipoId = tipo.getId();
        
        // Llamar al método original
        crearInmuebleInterno(direccion, localidadNombre, provinciaNombre, tipoId,
                           ambientesStr, superficieStr, uso, precioStr, latitudStr,
                           longitudStr, imagenBase64, imagenNombre);
    }
    
    /**
     * Método interno con la lógica de creación (antes era público)
     */
    private void crearInmuebleInterno(String direccion, String localidad, String provincia, 
                              int tipoId, String ambientesStr, String superficieStr, int uso, 
                              String precioStr, String latitudStr, String longitudStr,
                              String imagenBase64, String imagenNombre) {
        
        // Validaciones de campos obligatorios
        if (direccion == null || direccion.trim().isEmpty()) {
            mMensaje.postValue("La dirección es obligatoria");
            return;
        }
        
        // Validar y convertir Ambientes
        int ambientes = 0;
        try {
            if (ambientesStr != null && !ambientesStr.trim().isEmpty()) {
                ambientes = Integer.parseInt(ambientesStr.trim());
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Ambientes inválido: debe ser un número entero");
            return;
        }
        
        if (ambientes <= 0) {
            mMensaje.postValue("La cantidad de ambientes debe ser mayor a 0");
            return;
        }
        
        // Validar y convertir Superficie
        Double superficie = null;
        try {
            if (superficieStr != null && !superficieStr.trim().isEmpty()) {
                superficie = Double.parseDouble(superficieStr.trim());
                if (superficie <= 0) {
                    mMensaje.postValue("La superficie debe ser mayor a 0");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Superficie inválida: debe ser un número decimal");
            return;
        }
        
        // Validar y convertir Precio
        Double precio = null;
        try {
            if (precioStr != null && !precioStr.trim().isEmpty()) {
                precio = Double.parseDouble(precioStr.trim());
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Precio inválido: debe ser un número decimal");
            return;
        }
        
        if (precio == null || precio <= 0) {
            mMensaje.postValue("El precio debe ser mayor a 0");
            return;
        }
        
        // Validar y convertir Latitud
        Double latitud = null;
        try {
            if (latitudStr != null && !latitudStr.trim().isEmpty()) {
                latitud = Double.parseDouble(latitudStr.trim());
                if (latitud < -90 || latitud > 90) {
                    mMensaje.postValue("Latitud inválida: debe estar entre -90 y 90");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Latitud inválida: debe ser un número decimal");
            return;
        }
        
        // Validar y convertir Longitud
        Double longitud = null;
        try {
            if (longitudStr != null && !longitudStr.trim().isEmpty()) {
                longitud = Double.parseDouble(longitudStr.trim());
                if (longitud < -180 || longitud > 180) {
                    mMensaje.postValue("Longitud inválida: debe estar entre -180 y 180");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mMensaje.postValue("Longitud inválida: debe ser un número decimal");
            return;
        }

        mCargando.postValue(true);
        String token = ApiClient.getToken(context);

        if (token == null || token.isEmpty()) {
            mMensaje.postValue("No hay sesión activa");
            mCargando.postValue(false);
            Log.d("CARGAR_INMUEBLE", "No hay token guardado");
            return;
        }

        // Crear el request
        CrearInmuebleRequest request = new CrearInmuebleRequest();
        request.setDireccion(direccion.trim());
        request.setLocalidad(localidad.trim());
        request.setProvincia(provincia.trim());
        request.setTipoId(tipoId);
        request.setAmbientes(ambientes);
        request.setSuperficie(superficie);
        request.setUso(uso);
        request.setPrecio(precio);
        request.setLatitud(latitud);
        request.setLongitud(longitud);
        
        // Agregar imagen si existe
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            request.setImagenBase64(imagenBase64);
            request.setImagenNombre(imagenNombre);
            Log.d("CARGAR_INMUEBLE", "Imagen incluida: " + imagenNombre);
        }

        Log.d("CARGAR_INMUEBLE", "Creando inmueble: " + direccion);

        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<Inmueble>> call = api.crearInmueble(token, request);

        call.enqueue(new Callback<ApiResponse<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Inmueble>> call,
                                 @NonNull Response<ApiResponse<Inmueble>> response) {
                mCargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Inmueble> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("CARGAR_INMUEBLE", "Inmueble creado exitosamente: " + 
                              apiResponse.getData().getId());
                        mMensaje.postValue("Inmueble creado exitosamente");
                        mInmuebleCreado.postValue(true);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? 
                            apiResponse.getMessage() : "Error al crear el inmueble";
                        Log.d("CARGAR_INMUEBLE", "Error en respuesta: " + errorMsg);
                        mMensaje.postValue(errorMsg);
                    }
                } else {
                    Log.d("CARGAR_INMUEBLE", "Error HTTP: " + response.code());
                    mMensaje.postValue("Error al crear el inmueble: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Inmueble>> call, 
                                @NonNull Throwable t) {
                mCargando.postValue(false);
                Log.d("CARGAR_INMUEBLE", "Error de conexión: " + t.getMessage());
                mMensaje.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    // Cargar provincias desde API
    public void cargarProvincias() {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar provincias");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<Provincia>>> call = api.listarProvincias(token);
        
        call.enqueue(new Callback<ApiResponse<List<Provincia>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Provincia>>> call,
                                 @NonNull Response<ApiResponse<List<Provincia>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Provincia>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mProvincias.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Provincias cargadas: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Provincia>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar provincias: " + t.getMessage());
            }
        });
    }
    
    // Cargar localidades por provincia
    public void cargarLocalidadesPorProvincia(String nombreProvincia) {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar localidades");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<Localidad>>> call = api.listarLocalidadesPorProvincia(token, nombreProvincia);
        
        call.enqueue(new Callback<ApiResponse<List<Localidad>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Localidad>>> call,
                                 @NonNull Response<ApiResponse<List<Localidad>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Localidad>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mLocalidades.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Localidades cargadas: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Localidad>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar localidades: " + t.getMessage());
            }
        });
    }
    
    // Cargar tipos de inmueble
    public void cargarTiposInmueble() {
        String token = ApiClient.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.d("CARGAR_INMUEBLE", "No hay token para cargar tipos");
            return;
        }
        
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
        Call<ApiResponse<List<TipoInmueble>>> call = api.listarTiposInmueble(token);
        
        call.enqueue(new Callback<ApiResponse<List<TipoInmueble>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<TipoInmueble>>> call,
                                 @NonNull Response<ApiResponse<List<TipoInmueble>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TipoInmueble>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mTiposInmueble.postValue(apiResponse.getData());
                        Log.d("CARGAR_INMUEBLE", "Tipos de inmueble cargados: " + apiResponse.getData().size());
                    }
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<TipoInmueble>>> call, @NonNull Throwable t) {
                Log.d("CARGAR_INMUEBLE", "Error al cargar tipos: " + t.getMessage());
            }
        });
    }
    
    /**
     * Procesa una imagen desde Uri y guarda el Base64 internamente
     * @param imageUri Uri de la imagen seleccionada
     * @return Bitmap redimensionado para preview, o null si hay error
     */
    public Bitmap procesarImagenDesdeUri(Uri imageUri, String nombreArchivo) {
        try {
            // Si no hay nombre, generar uno
            if (nombreArchivo == null || nombreArchivo.isEmpty()) {
                nombreArchivo = "inmueble_" + System.currentTimeMillis() + ".jpg";
            }
            
            // Cargar la imagen
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            
            if (bitmap == null) {
                mMensaje.postValue("Error al cargar la imagen");
                return null;
            }
            
            // Redimensionar si es muy grande
            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmap, 1024, 1024);
            
            // Convertir a Base64 y guardar internamente
            String base64 = convertirBitmapABase64(bitmapRedimensionado);
            
            // Aquí podrías guardar en variables del ViewModel si lo necesitas
            // Por ahora lo retornamos y el Fragment lo maneja
            
            Log.d("CARGAR_INMUEBLE_VM", "Imagen procesada: " + nombreArchivo + 
                  ", tamaño Base64: " + (base64 != null ? base64.length() : 0));
            
            return bitmapRedimensionado;
            
        } catch (Exception e) {
            Log.e("CARGAR_INMUEBLE_VM", "Error al procesar imagen: " + e.getMessage());
            mMensaje.postValue("Error al procesar la imagen");
            return null;
        }
    }
    
    /**
     * Obtiene el nombre de archivo desde un Uri
     */
    public String obtenerNombreArchivoDesdeUri(Uri imageUri) {
        String nombreArchivo = null;
        try {
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
            android.database.Cursor cursor = context.getContentResolver()
                .query(imageUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                nombreArchivo = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("CARGAR_INMUEBLE_VM", "Error al obtener nombre de archivo: " + e.getMessage());
        }
        
        if (nombreArchivo == null) {
            nombreArchivo = "inmueble_" + System.currentTimeMillis() + ".jpg";
        }
        
        return nombreArchivo;
    }
    
    /**
     * Redimensiona un Bitmap si excede el tamaño máximo
     */
    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }
        
        float aspectRatio = (float) width / height;
        
        if (width > height) {
            width = maxWidth;
            height = (int) (width / aspectRatio);
        } else {
            height = maxHeight;
            width = (int) (height * aspectRatio);
        }
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
    
    /**
     * Convierte un Bitmap a String Base64
     */
    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    
    /**
     * Convierte un Bitmap a Base64 directamente (método público para el Fragment)
     */
    public String bitmapABase64(Bitmap bitmap) {
        return convertirBitmapABase64(bitmap);
    }
}
