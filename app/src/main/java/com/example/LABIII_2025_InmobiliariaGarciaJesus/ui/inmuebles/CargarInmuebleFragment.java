package com.example.LABIII_2025_InmobiliariaGarciaJesus.ui.inmuebles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.LABIII_2025_InmobiliariaGarciaJesus.databinding.FragmentCargarInmuebleBinding;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Localidad;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.Provincia;
import com.example.LABIII_2025_InmobiliariaGarciaJesus.modelos.TipoInmueble;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.widget.AdapterView;

public class CargarInmuebleFragment extends Fragment {

    private FragmentCargarInmuebleBinding binding;
    private CargarInmuebleViewModel mv;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 200;
    
    private String imagenBase64 = null;
    private String imagenNombre = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCargarInmuebleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mv = new ViewModelProvider(this).get(CargarInmuebleViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Configurar Spinner de Uso (hardcodeado)
        String[] usos = {"Residencial", "Comercial", "Industrial"};
        ArrayAdapter<String> adapterUso = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, usos);
        adapterUso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerUso.setAdapter(adapterUso);
        
        // Listener para cambio de Provincia - cargar localidades
        binding.spinnerProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.btnObtenerUbicacion.setOnClickListener(v -> obtenerUbicacionActual());
        
        // Listener del botón seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        // Listener del botón guardar
        binding.btnGuardar.setOnClickListener(v -> guardarInmueble());

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
                binding.progressBarCargar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.btnGuardar.setEnabled(!isLoading);
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
                    binding.spinnerProvincia.setAdapter(adapter);
                    
                    // Seleccionar "San Luis" por defecto
                    for (int i = 0; i < provincias.size(); i++) {
                        if (provincias.get(i).getNombre().equalsIgnoreCase("San Luis")) {
                            binding.spinnerProvincia.setSelection(i);
                            break;
                        }
                    }
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
                    binding.spinnerLocalidad.setAdapter(adapter);
                    
                    // Seleccionar "San Luis" por defecto
                    for (int i = 0; i < localidades.size(); i++) {
                        if (localidades.get(i).getNombre().equalsIgnoreCase("San Luis")) {
                            binding.spinnerLocalidad.setSelection(i);
                            break;
                        }
                    }
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
                    binding.spinnerTipo.setAdapter(adapter);
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
                            binding.etLatitud.setText(String.valueOf(location.getLatitude()));
                            binding.etLongitud.setText(String.valueOf(location.getLongitude()));
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
        // Obtener valores de campos - el ViewModel hará todas las validaciones
        String direccion = binding.etDireccion.getText() != null ? binding.etDireccion.getText().toString() : "";
        String ambientes = binding.etAmbientes.getText() != null ? binding.etAmbientes.getText().toString() : "";
        String superficie = binding.etSuperficie.getText() != null ? binding.etSuperficie.getText().toString() : "";
        String precio = binding.etPrecio.getText() != null ? binding.etPrecio.getText().toString() : "";
        String latitud = binding.etLatitud.getText() != null ? binding.etLatitud.getText().toString() : "";
        String longitud = binding.etLongitud.getText() != null ? binding.etLongitud.getText().toString() : "";
        
        // Obtener objetos completos de los spinners (sin lógica)
        Provincia provincia = (Provincia) binding.spinnerProvincia.getSelectedItem();
        Localidad localidad = (Localidad) binding.spinnerLocalidad.getSelectedItem();
        TipoInmueble tipo = (TipoInmueble) binding.spinnerTipo.getSelectedItem();
        int uso = binding.spinnerUso.getSelectedItemPosition();
        
        // El ViewModel valida y procesa todo
        mv.crearInmueble(direccion, localidad, provincia, tipo, ambientes, 
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
            
            // Obtener nombre del archivo usando el ViewModel
            imagenNombre = mv.obtenerNombreArchivoDesdeUri(imageUri);
            
            // Procesar imagen usando el ViewModel
            Bitmap bitmapRedimensionado = mv.procesarImagenDesdeUri(imageUri, imagenNombre);
            
            if (bitmapRedimensionado != null) {
                // Convertir a Base64 usando el ViewModel
                imagenBase64 = mv.bitmapABase64(bitmapRedimensionado);
                
                // Mostrar preview
                binding.ivPreviewImagen.setImageBitmap(bitmapRedimensionado);
                binding.ivPreviewImagen.setVisibility(View.VISIBLE);
                binding.btnSeleccionarImagen.setText("Cambiar Foto");
                
                Toast.makeText(getContext(), "Imagen seleccionada: " + imagenNombre, 
                             Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
