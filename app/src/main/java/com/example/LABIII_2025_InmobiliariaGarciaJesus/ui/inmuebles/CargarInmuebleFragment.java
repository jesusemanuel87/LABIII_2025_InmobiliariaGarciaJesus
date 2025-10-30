package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.R;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Localidad;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Provincia;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.TipoInmueble;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.AdapterView;

public class CargarInmuebleFragment extends Fragment {

    private CargarInmuebleViewModel mv;
    private TextInputEditText etDireccion, etAmbientes;
    private TextInputEditText etSuperficie, etPrecio, etLatitud, etLongitud;
    private Spinner spinnerProvincia, spinnerLocalidad, spinnerTipo, spinnerUso;
    private Button btnGuardar, btnObtenerUbicacion, btnSeleccionarImagen;
    private ImageView ivPreviewImagen;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 200;
    
    private String imagenBase64 = null;
    private String imagenNombre = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cargar_inmueble, container, false);

        mv = new ViewModelProvider(this).get(CargarInmuebleViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Inicializar vistas
        etDireccion = root.findViewById(R.id.etDireccion);
        etAmbientes = root.findViewById(R.id.etAmbientes);
        etSuperficie = root.findViewById(R.id.etSuperficie);
        etPrecio = root.findViewById(R.id.etPrecio);
        etLatitud = root.findViewById(R.id.etLatitud);
        etLongitud = root.findViewById(R.id.etLongitud);
        spinnerProvincia = root.findViewById(R.id.spinnerProvincia);
        spinnerLocalidad = root.findViewById(R.id.spinnerLocalidad);
        spinnerTipo = root.findViewById(R.id.spinnerTipo);
        spinnerUso = root.findViewById(R.id.spinnerUso);
        btnGuardar = root.findViewById(R.id.btnGuardar);
        btnObtenerUbicacion = root.findViewById(R.id.btnObtenerUbicacion);
        btnSeleccionarImagen = root.findViewById(R.id.btnSeleccionarImagen);
        ivPreviewImagen = root.findViewById(R.id.ivPreviewImagen);
        progressBar = root.findViewById(R.id.progressBarCargar);

        // Configurar Spinner de Uso (hardcodeado)
        String[] usos = {"Residencial", "Comercial", "Industrial"};
        ArrayAdapter<String> adapterUso = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, usos);
        adapterUso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUso.setAdapter(adapterUso);
        
        // Listener para cambio de Provincia - cargar localidades
        spinnerProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Provincia provinciaSeleccionada = (Provincia) parent.getItemAtPosition(position);
                if (provinciaSeleccionada != null) {
                    mv.cargarLocalidadesPorProvincia(provinciaSeleccionada.getNombre());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Listener del botón obtener ubicación
        btnObtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbicacionActual();
            }
        });
        
        // Listener del botón seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });

        // Listener del botón guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarInmueble();
            }
        });

        // Observer para mensajes
        mv.getMMensaje().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                Toast.makeText(getContext(), mensaje == null ? "" : mensaje, Toast.LENGTH_LONG).show();
            }
        });

        // Observer para carga
        mv.getMCargando().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean cargando) {
                boolean isLoading = Boolean.TRUE.equals(cargando);
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnGuardar.setEnabled(!isLoading);
            }
        });

        // Observer para inmueble creado
        mv.getMInmuebleCreado().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean creado) {
                if (creado != null && creado) {
                    // Volver al listado de inmuebles
                    Navigation.findNavController(root).navigateUp();
                }
            }
        });
        
        // Observer para provincias
        mv.getMProvincias().observe(getViewLifecycleOwner(), new Observer<List<Provincia>>() {
            @Override
            public void onChanged(List<Provincia> provincias) {
                if (provincias != null && !provincias.isEmpty()) {
                    ArrayAdapter<Provincia> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, provincias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvincia.setAdapter(adapter);
                }
            }
        });
        
        // Observer para localidades
        mv.getMLocalidades().observe(getViewLifecycleOwner(), new Observer<List<Localidad>>() {
            @Override
            public void onChanged(List<Localidad> localidades) {
                if (localidades != null && !localidades.isEmpty()) {
                    ArrayAdapter<Localidad> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, localidades);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLocalidad.setAdapter(adapter);
                }
            }
        });
        
        // Observer para tipos de inmueble
        mv.getMTiposInmueble().observe(getViewLifecycleOwner(), new Observer<List<TipoInmueble>>() {
            @Override
            public void onChanged(List<TipoInmueble> tipos) {
                if (tipos != null && !tipos.isEmpty()) {
                    ArrayAdapter<TipoInmueble> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, tipos);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTipo.setAdapter(adapter);
                }
            }
        });
        
        // Cargar datos desde API
        mv.cargarProvincias();
        mv.cargarTiposInmueble();

        return root;
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                             LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            etLatitud.setText(String.valueOf(location.getLatitude()));
                            etLongitud.setText(String.valueOf(location.getLongitude()));
                            Toast.makeText(getContext(), "Ubicación obtenida", 
                                         Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), 
                                         "No se pudo obtener la ubicación. Intenta nuevamente.", 
                                         Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                Toast.makeText(getContext(), 
                             "Permiso de ubicación denegado", 
                             Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarInmueble() {
        // Obtener dirección del TextInput
        String direccion = etDireccion.getText() != null ? etDireccion.getText().toString() : "";
        
        // Obtener provincia y localidad de los spinners
        String provincia = "";
        String localidad = "";
        if (spinnerProvincia.getSelectedItem() instanceof Provincia) {
            provincia = ((Provincia) spinnerProvincia.getSelectedItem()).getNombre();
        }
        if (spinnerLocalidad.getSelectedItem() instanceof Localidad) {
            localidad = ((Localidad) spinnerLocalidad.getSelectedItem()).getNombre();
        }
        
        // Obtener tipo de inmueble del spinner
        int tipoId = 0;
        if (spinnerTipo.getSelectedItem() instanceof TipoInmueble) {
            tipoId = ((TipoInmueble) spinnerTipo.getSelectedItem()).getId();
        }
        
        // Obtener uso del spinner (hardcodeado)
        int uso = spinnerUso.getSelectedItemPosition();
        
        // Obtener otros valores de campos de texto
        String ambientes = etAmbientes.getText() != null ? etAmbientes.getText().toString() : "";
        String superficie = etSuperficie.getText() != null ? etSuperficie.getText().toString() : "";
        String precio = etPrecio.getText() != null ? etPrecio.getText().toString() : "";
        String latitud = etLatitud.getText() != null ? etLatitud.getText().toString() : "";
        String longitud = etLongitud.getText() != null ? etLongitud.getText().toString() : "";
        
        // El ViewModel se encarga de todas las validaciones y conversiones
        mv.crearInmueble(direccion, localidad, provincia, tipoId, ambientes, 
                        superficie, uso, precio, latitud, longitud, imagenBase64, imagenNombre);
    }
    
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK 
            && data != null && data.getData() != null) {
            
            Uri imageUri = data.getData();
            try {
                // Obtener el nombre del archivo
                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
                android.database.Cursor cursor = getActivity().getContentResolver()
                    .query(imageUri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    imagenNombre = cursor.getString(columnIndex);
                    cursor.close();
                }
                
                if (imagenNombre == null) {
                    imagenNombre = "inmueble_" + System.currentTimeMillis() + ".jpg";
                }
                
                // Cargar la imagen
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                
                // Redimensionar si es muy grande
                Bitmap bitmapRedimensionado = redimensionarBitmap(bitmap, 1024, 1024);
                
                // Convertir a Base64
                imagenBase64 = convertirBitmapABase64(bitmapRedimensionado);
                
                // Mostrar preview
                ivPreviewImagen.setImageBitmap(bitmapRedimensionado);
                ivPreviewImagen.setVisibility(View.VISIBLE);
                btnSeleccionarImagen.setText("Cambiar Foto");
                
                Toast.makeText(getContext(), "Imagen seleccionada: " + imagenNombre, 
                             Toast.LENGTH_SHORT).show();
                
                Log.d("CARGAR_INMUEBLE", "Imagen cargada: " + imagenNombre + 
                      ", tamaño Base64: " + (imagenBase64 != null ? imagenBase64.length() : 0));
                
            } catch (Exception e) {
                Log.e("CARGAR_INMUEBLE", "Error al cargar imagen: " + e.getMessage());
                Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
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
    
    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
