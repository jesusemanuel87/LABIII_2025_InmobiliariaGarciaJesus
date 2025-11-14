# Flujo de Contratos — Arquitectura MVVM

## Resumen en 6 pasos

1. **Usuario navega a Contratos**: El `NavigationDrawer` de `MainActivity` permite acceder a `ContratosFragment`.
2. **ContratosFragment crea ViewModel**: `mv = new ViewModelProvider(this).get(ContratosViewModel.class);`
3. **Fragment observa LiveData**: Configura observers para `getMContratos()`, `getMError()`, `getMCargando()`.
4. **Fragment llama `cargarContratos()`**: El ViewModel ejecuta la llamada API con token del propietario.
5. **API responde**: `api.listarContratos(token)` devuelve `List<Contrato>` → `mContratos.postValue(lista)`.
6. **RecyclerView se actualiza**: El observer recibe la lista → `adapter.setContratos(lista)` → renderiza contratos con inquilinos.

---

## Arquitectura y componentes

### **Vista (Fragment)**

#### `ContratosFragment.java`

**Responsabilidades:**
- Crear ViewModel
- Configurar RecyclerView con `ContratosAdapter`
- Observar LiveData del ViewModel
- Gestionar navegación a detalle

**Código clave:**

```java
// Crear ViewModel
mv = new ViewModelProvider(this).get(ContratosViewModel.class);

// Configurar RecyclerView
recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
adapter = new ContratosAdapter(new ArrayList<>(), getContext());
recyclerView.setAdapter(adapter);

// Listener para navegación
adapter.setOnContratoClickListener(contrato -> {
    Bundle bundle = new Bundle();
    bundle.putSerializable("contrato", contrato);
    Navigation.findNavController(root).navigate(
        R.id.action_contratosFragment_to_detalleContratoFragment, 
        bundle
    );
});
```

**Observers configurados:**

```java
// Lista de contratos
mv.getMContratos().observe(getViewLifecycleOwner(), contratos -> {
    List<Contrato> items = contratos != null ? contratos : Collections.emptyList();
    adapter.setContratos(items);
    boolean hasItems = !items.isEmpty();
    recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    tvMensaje.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    tvMensaje.setText(hasItems ? "" : "No hay contratos registrados");
});

// Errores
mv.getMError().observe(getViewLifecycleOwner(), error -> {
    if (error != null && !error.isEmpty()) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }
});

// Estado de carga
mv.getMCargando().observe(getViewLifecycleOwner(), cargando -> {
    boolean isLoading = Boolean.TRUE.equals(cargando);
    progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    recyclerView.setVisibility(isLoading ? View.GONE : recyclerView.getVisibility());
    tvMensaje.setVisibility(isLoading ? View.GONE : tvMensaje.getVisibility());
});
```

**Llamada inicial:**
```java
mv.cargarContratos(); // En onCreateView
```

---

#### `DetalleContratoFragment.java`

**Responsabilidades:**
- Mostrar detalles completos de un contrato
- **NO contiene lógica de formateo o validaciones** (patrón MVVM puro)
- Solo observa LiveData y actualiza vistas

**Patrón único - Un LiveData por campo:**

```java
// ViewModel expone datos ya formateados
mv.getMDireccionInmueble().observe(getViewLifecycleOwner(), direccion -> {
    tvDireccionInmueble.setText(direccion);
});

mv.getMInquilino().observe(getViewLifecycleOwner(), inquilino -> {
    tvInquilino.setText(inquilino);
});

mv.getMPrecio().observe(getViewLifecycleOwner(), precio -> {
    tvPrecio.setText(precio); // Ya viene con formato "$ 50000.00/mes"
});

mv.getMFechaInicio().observe(getViewLifecycleOwner(), fechaInicio -> {
    tvFechaInicio.setText(fechaInicio); // Ya viene con formato "Inicio: 2025-01-01"
});

// Observer para visibilidad condicional
mv.getMMesesAdeudadosVisibility().observe(getViewLifecycleOwner(), visibility -> {
    tvMesesAdeudados.setVisibility(visibility); // View.VISIBLE o View.GONE
});
```

**Recibe contrato del Bundle:**
```java
if (getArguments() != null) {
    Contrato contrato = (Contrato) getArguments().getSerializable("contrato");
    if (contrato != null) {
        mv.setContratoSeleccionado(contrato); // ViewModel procesa todo
    }
}
```

**Navegación a Pagos:**
```java
mv.getMContratoSeleccionado().observe(getViewLifecycleOwner(), contrato -> {
    if (contrato != null) {
        btnPagos.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("contrato", contrato);
            Navigation.findNavController(root).navigate(
                R.id.action_detalleContratoFragment_to_detallePagosFragment, 
                bundle
            );
        });
    }
});
```

---

### **ViewModel (Lógica de presentación)**

#### `ContratosViewModel.java`

**Responsabilidades:**
- Gestionar estado de la UI (LiveData)
- Realizar llamadas API (Retrofit)
- Formatear y validar datos (toda la lógica de negocio)
- Preparar datos para mostrar (sin que Fragment tenga lógica)

**LiveData expuestos:**

```java
// Para lista de contratos
private MutableLiveData<List<Contrato>> mContratos;
private MutableLiveData<String> mError;
private MutableLiveData<Boolean> mCargando;

// Para detalle de contrato (datos ya formateados)
private MutableLiveData<Contrato> mContratoSeleccionado;
private MutableLiveData<String> mDireccionInmueble;
private MutableLiveData<String> mInquilino;
private MutableLiveData<String> mPrecio;
private MutableLiveData<String> mFechaInicio;
private MutableLiveData<String> mFechaFin;
private MutableLiveData<String> mEstado;
private MutableLiveData<String> mFechaCreacion;
private MutableLiveData<String> mMesesAdeudados;
private MutableLiveData<Integer> mMesesAdeudadosVisibility;
private MutableLiveData<String> mImporteAdeudado;
private MutableLiveData<Integer> mImporteAdeudadoVisibility;
```

**Método principal: `cargarContratos()`**

```java
public void cargarContratos() {
    mCargando.postValue(true);
    String token = ApiClient.getToken(context);

    if (token == null || token.isEmpty()) {
        mError.postValue("No hay sesión activa");
        mCargando.postValue(false);
        Log.d("CONTRATOS", "No hay token guardado");
        return;
    }

    ApiClient.MyApiInterface api = ApiClient.getMyApiInterface(context);
    Call<List<Contrato>> call = api.listarContratos(token);

    call.enqueue(new Callback<List<Contrato>>() {
        @Override
        public void onResponse(@NonNull Call<List<Contrato>> call,
                             @NonNull Response<List<Contrato>> response) {
            mCargando.postValue(false);
            if (response.isSuccessful() && response.body() != null) {
                List<Contrato> contratos = response.body();
                Log.d("CONTRATOS", "Contratos cargados: " + contratos.size());
                mContratos.postValue(contratos);
            } else {
                Log.d("CONTRATOS", "Error HTTP: " + response.code());
                mError.postValue("Error al cargar contratos: " + response.code());
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Contrato>> call, 
                            @NonNull Throwable t) {
            mCargando.postValue(false);
            Log.d("CONTRATOS", "Error de conexión: " + t.getMessage());
            mError.postValue("Error de conexión: " + t.getMessage());
        }
    });
}
```

**Método alternativo: `cargarContratosPorInmueble(int inmuebleId)`**

```java
public void cargarContratosPorInmueble(int inmuebleId) {
    // Similar a cargarContratos() pero llama:
    Call<List<Contrato>> call = api.listarContratosPorInmueble(token, inmuebleId);
    // Usado desde DetalleInmuebleFragment para mostrar contratos de un inmueble específico
}
```

**Método de preparación de datos: `setContratoSeleccionado(Contrato contrato)`**

Este método es **clave en el patrón MVVM**. Recibe el contrato crudo y prepara TODOS los datos formateados:

```java
public void setContratoSeleccionado(Contrato contrato) {
    mContratoSeleccionado.setValue(contrato);
    prepararDatosDetalle(contrato); // Toda la lógica aquí
}

private void prepararDatosDetalle(Contrato contrato) {
    if (contrato == null) return;
    
    // Validar y preparar dirección del inmueble
    if (contrato.getInmueble() != null) {
        mDireccionInmueble.postValue(contrato.getInmueble().getDireccion());
    } else {
        mDireccionInmueble.postValue("Inmueble no disponible");
    }
    
    // Validar y preparar datos del inquilino
    if (contrato.getInquilino() != null) {
        InquilinoContrato inquilino = contrato.getInquilino();
        mInquilino.postValue(inquilino.getNombreCompleto() != null ? 
            inquilino.getNombreCompleto() : "No especificado");
    } else {
        mInquilino.postValue("Inquilino no disponible");
    }
    
    // Formatear precio con símbolo y decimales
    mPrecio.postValue(String.format("$ %.2f/mes", contrato.getPrecio()));
    
    // Formatear fechas (eliminar hora si viene en formato ISO)
    mFechaInicio.postValue("Inicio: " + formatearSoloFecha(contrato.getFechaInicio()));
    mFechaFin.postValue("Fin: " + formatearSoloFecha(contrato.getFechaFin()));
    mEstado.postValue("Estado: " + (contrato.getEstado() != null ? 
        contrato.getEstado() : "No especificado"));
    mFechaCreacion.postValue("Creado: " + formatearSoloFecha(contrato.getFechaCreacion()));
    
    // Lógica condicional para mostrar/ocultar información de deuda
    if (contrato.getMesesAdeudados() != null && contrato.getMesesAdeudados() > 0) {
        mMesesAdeudados.postValue("Meses adeudados: " + contrato.getMesesAdeudados());
        mMesesAdeudadosVisibility.postValue(View.VISIBLE);
    } else {
        mMesesAdeudadosVisibility.postValue(View.GONE);
    }
    
    if (contrato.getImporteAdeudado() != null && contrato.getImporteAdeudado() > 0) {
        mImporteAdeudado.postValue(String.format("Importe adeudado: $ %.2f", 
            contrato.getImporteAdeudado()));
        mImporteAdeudadoVisibility.postValue(View.VISIBLE);
    } else {
        mImporteAdeudadoVisibility.postValue(View.GONE);
    }
}
```

**Método helper de formateo:**

```java
private String formatearSoloFecha(String fechaISO) {
    if (fechaISO == null || fechaISO.isEmpty()) {
        return "No especificado";
    }
    
    // Si tiene formato ISO con 'T', extraer solo la fecha
    if (fechaISO.contains("T")) {
        return fechaISO.split("T")[0]; // "2025-06-11T00:00:00" -> "2025-06-11"
    }
    
    // Si tiene espacio (otro formato con hora)
    if (fechaISO.contains(" ")) {
        return fechaISO.split(" ")[0];
    }
    
    return fechaISO;
}
```

---

### **Adapter (Presentación de lista)**

#### `ContratosAdapter.java`

**Responsabilidades:**
- Inflar vistas (`item_inmueble.xml` - reutilizado)
- Vincular datos del modelo Contrato a las vistas
- Gestionar clicks en items

**Características:**
- Reutiliza el layout de Inmuebles (`item_inmueble.xml`)
- Oculta el switch de estado (no aplica a contratos)
- Muestra imagen del inmueble del contrato

**Código clave:**

```java
public void setContratos(List<Contrato> contratos) {
    this.contratos = contratos;
    notifyDataSetChanged();
}

public void bind(Contrato contrato, OnContratoClickListener listener) {
    // Mostrar dirección del inmueble asociado
    if (contrato.getInmueble() != null) {
        tvDireccion.setText(contrato.getInmueble().getDireccion());
        
        // Cargar imagen del inmueble (con normalización de URL)
        String imageUrl = contrato.getInmueble().getImagenPortadaUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Reemplazar localhost con DevTunnel URL
            if (imageUrl.contains("localhost:5000") || imageUrl.contains("127.0.0.1:5000")) {
                String baseUrl = ApiClient.getBaseUrl(itemView.getContext());
                imageUrl = imageUrl.replace("http://localhost:5000/", baseUrl);
                // ... más reemplazos ...
            }
            
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.ic_home)
                    .into(ivImagen);
        }
        
        // Mostrar nombre del inquilino
        if (contrato.getInquilino() != null) {
            tvTipo.setText("Inquilino: " + contrato.getInquilino().getNombreCompleto());
        }
    }
    
    // Mostrar precio del contrato
    tvPrecio.setText(String.format("$ %.2f", contrato.getPrecio()));
    
    // Mostrar estado del contrato
    tvEstado.setText(contrato.getEstado() != null ? contrato.getEstado() : "Sin estado");
    
    // Listener de click
    itemView.setOnClickListener(v -> {
        if (listener != null) {
            listener.onContratoClick(contrato);
        }
    });
}
```

**Interface de listener:**
```java
public interface OnContratoClickListener {
    void onContratoClick(Contrato contrato);
}
```

---

### **Modelo de datos**

#### `Contrato.java`

**Campos principales:**

```java
@SerializedName("id")
private int id;

@SerializedName("fechaInicio")
private String fechaInicio; // Formato ISO: "2025-06-11T00:00:00"

@SerializedName("fechaFin")
private String fechaFin;

@SerializedName("precio")
private double precio;

@SerializedName("estado")
private String estado; // Ej: "Activo", "Finalizado", "Cancelado"

@SerializedName("fechaCreacion")
private String fechaCreacion;

@SerializedName("motivoCancelacion")
private String motivoCancelacion;

@SerializedName("fechaFinalizacionReal")
private String fechaFinalizacionReal;

@SerializedName("multaFinalizacion")
private Double multaFinalizacion;

@SerializedName("mesesAdeudados")
private Integer mesesAdeudados; // nullable

@SerializedName("importeAdeudado")
private Double importeAdeudado; // nullable

@SerializedName("inmueble")
private InmuebleContrato inmueble; // Objeto anidado

@SerializedName("inquilino")
private InquilinoContrato inquilino; // Objeto anidado

@SerializedName("pagos")
private List<Pago> pagos; // Lista de pagos asociados
```

**Nota importante:**
- Implementa `Serializable` para pasar entre Fragments via Bundle
- Usa `@SerializedName` para mapeo Gson/Retrofit
- Campos de deuda son `nullable` (Integer/Double, no int/double)

---

### **API Client**

#### Endpoints en `ApiClient.MyApiInterface`

```java
// Listar todos los contratos del propietario autenticado
@GET("api/ContratosApi")
Call<List<Contrato>> listarContratos(@Header("Authorization") String token);

// Listar contratos de un inmueble específico
@GET("api/ContratosApi/inmueble/{inmuebleId}")
Call<List<Contrato>> listarContratosPorInmueble(
    @Header("Authorization") String token,
    @Path("inmuebleId") int inmuebleId
);
```

**Token management:**
```java
String token = ApiClient.getToken(context); // Lee de SharedPreferences
```

---

## Flujo completo paso a paso

### **1. Listar Contratos**

```
Usuario toca "Contratos" en NavigationDrawer
    ↓
MainActivity navega a ContratosFragment
    ↓
ContratosFragment.onCreateView()
    - Crea ContratosViewModel
    - Configura RecyclerView con ContratosAdapter
    - Configura observers (mContratos, mError, mCargando)
    - Llama mv.cargarContratos()
    ↓
ContratosViewModel.cargarContratos()
    - mCargando.postValue(true) → Fragment muestra ProgressBar
    - Lee token: ApiClient.getToken(context)
    - Llama api.listarContratos(token)
    ↓
Retrofit ejecuta GET /api/ContratosApi
    ↓
Backend responde con List<Contrato> (JSON)
    ↓
ContratosViewModel.onResponse()
    - mCargando.postValue(false) → Fragment oculta ProgressBar
    - mContratos.postValue(contratos)
    ↓
ContratosFragment (observer de mContratos)
    - adapter.setContratos(items)
    - Muestra/oculta RecyclerView y mensaje según si hay items
    ↓
ContratosAdapter renderiza cada Contrato
    - Muestra imagen del inmueble (con Glide)
    - Muestra dirección, inquilino, precio, estado
```

### **2. Ver Detalle de Contrato**

```
Usuario toca un item en la lista
    ↓
ContratosAdapter.OnContratoClickListener
    ↓
ContratosFragment navega a DetalleContratoFragment
    - Bundle con contrato serializado
    ↓
DetalleContratoFragment.onCreateView()
    - Crea ContratosViewModel
    - Configura observers para 10+ LiveData individuales
    - Lee contrato del Bundle
    - Llama mv.setContratoSeleccionado(contrato)
    ↓
ContratosViewModel.setContratoSeleccionado(contrato)
    - Llama prepararDatosDetalle(contrato)
    ↓
ContratosViewModel.prepararDatosDetalle(contrato)
    - Valida inmueble → mDireccionInmueble.postValue(...)
    - Valida inquilino → mInquilino.postValue(...)
    - Formatea precio → mPrecio.postValue("$ 50000.00/mes")
    - Formatea fechas → mFechaInicio.postValue("Inicio: 2025-01-01")
    - Evalúa deuda → mMesesAdeudadosVisibility.postValue(View.VISIBLE o GONE)
    - etc. (10 LiveData actualizados)
    ↓
DetalleContratoFragment (observers)
    - Cada observer actualiza su TextView correspondiente
    - Visibilidad de campos de deuda controlada por LiveData
    - NO hay lógica de formateo en el Fragment
```

### **3. Ver Pagos del Contrato**

```
Usuario toca botón "Ver Pagos" en DetalleContratoFragment
    ↓
Observer de mContratoSeleccionado configura listener del botón
    ↓
Navigation.findNavController().navigate()
    - action_detalleContratoFragment_to_detallePagosFragment
    - Bundle con contrato serializado
    ↓
DetallePagosFragment (no mostrado en este flow)
```

---

## Patrón MVVM aplicado

### **Separación de responsabilidades**

| Componente | Responsabilidad | NO debe hacer |
|------------|----------------|---------------|
| **Fragment** | Observar LiveData, actualizar vistas, gestionar navegación | Formatear datos, validar, hacer lógica de negocio |
| **ViewModel** | Llamadas API, formateo de datos, validaciones, lógica de negocio | Referenciar vistas, usar Context para UI |
| **Adapter** | Vincular datos a vistas, gestionar clicks | Llamar API, tener lógica de negocio |
| **Modelo** | Representar datos, serialización | Tener lógica de negocio |

### **Ejemplo de separación correcta**

❌ **Incorrecto (lógica en Fragment):**
```java
// Fragment
mv.getMContratoSeleccionado().observe(this, contrato -> {
    if (contrato.getInquilino() != null) {
        String nombre = contrato.getInquilino().getNombreCompleto();
        tvInquilino.setText(nombre != null ? nombre : "No especificado");
    } else {
        tvInquilino.setText("Inquilino no disponible");
    }
});
```

✅ **Correcto (lógica en ViewModel):**
```java
// ViewModel
private void prepararDatosDetalle(Contrato contrato) {
    if (contrato.getInquilino() != null) {
        InquilinoContrato inquilino = contrato.getInquilino();
        mInquilino.postValue(inquilino.getNombreCompleto() != null ? 
            inquilino.getNombreCompleto() : "No especificado");
    } else {
        mInquilino.postValue("Inquilino no disponible");
    }
}

// Fragment (sin lógica)
mv.getMInquilino().observe(getViewLifecycleOwner(), inquilino -> {
    tvInquilino.setText(inquilino); // Solo actualiza
});
```

---

## Valores y threading

### **postValue vs setValue**

```java
// Desde background thread (Retrofit callback)
mContratos.postValue(contratos); // ✅ Correcto

// Desde main thread
mContratoSeleccionado.setValue(contrato); // ✅ Correcto
```

### **Observers y lifecycle**

```java
// En Fragment - usa getViewLifecycleOwner()
mv.getMContratos().observe(getViewLifecycleOwner(), contratos -> {
    // Se cancela automáticamente cuando la vista del Fragment se destruye
});

// En Activity - usa this
mv.getMContratos().observe(this, contratos -> {
    // Se cancela automáticamente cuando la Activity se destruye
});
```

---

## Estados de UI

### **Patrón de tres LiveData**

Para cada conjunto de datos:
1. `LiveData<T>` - Los datos en sí
2. `LiveData<Boolean>` - Estado de carga
3. `LiveData<String>` - Mensaje de error

```java
// ViewModel
private MutableLiveData<List<Contrato>> mContratos;
private MutableLiveData<Boolean> mCargando;
private MutableLiveData<String> mError;

// Fragment observa los 3
mv.getMContratos().observe(...); // Actualiza RecyclerView
mv.getMCargando().observe(...);  // Muestra/oculta ProgressBar
mv.getMError().observe(...);     // Muestra Toast
```

### **Estados posibles**

| Estado | mCargando | mContratos | mError | UI |
|--------|-----------|------------|--------|-----|
| **Inicial** | `false` | `null` | `null` | Mensaje "No hay contratos" |
| **Cargando** | `true` | `null` | `null` | ProgressBar visible |
| **Éxito** | `false` | `[...]` | `null` | RecyclerView con items |
| **Éxito vacío** | `false` | `[]` | `null` | Mensaje "No hay contratos" |
| **Error** | `false` | `null` | `"Error..."` | Toast + mensaje |

---

## Navegación

### **Navigation Component**

```java
// ContratosFragment → DetalleContratoFragment
Bundle bundle = new Bundle();
bundle.putSerializable("contrato", contrato);
Navigation.findNavController(root).navigate(
    R.id.action_contratosFragment_to_detalleContratoFragment, 
    bundle
);

// DetalleContratoFragment → DetallePagosFragment
Bundle bundle = new Bundle();
bundle.putSerializable("contrato", contrato);
Navigation.findNavController(root).navigate(
    R.id.action_detalleContratoFragment_to_detallePagosFragment, 
    bundle
);
```

**Importante:** El modelo `Contrato` implementa `Serializable` para permitir esto.

---

## Puntos clave del código

### **En `ContratosViewModel`:**

- **10+ LiveData para detalle**: Cada campo de la UI tiene su propio LiveData
- **Método `prepararDatosDetalle()`**: Centraliza toda la lógica de formateo y validación
- **Visibilidad condicional**: `mMesesAdeudadosVisibility` (View.VISIBLE/GONE) controlada por ViewModel
- **postValue() en callbacks**: Siempre usa `postValue()` desde Retrofit callbacks (background thread)

### **En `DetalleContratoFragment`:**

- **Sin lógica de presentación**: Solo observa y actualiza vistas
- **Un observer por campo**: Máxima granularidad, fácil mantenimiento
- **No formatea nada**: Los datos ya vienen listos desde ViewModel

### **En `ContratosAdapter`:**

- **Reutiliza layout**: Usa `item_inmueble.xml` (oculta switch)
- **Normalización de URLs**: Reemplaza localhost por DevTunnel URL
- **Glide para imágenes**: Carga asíncrona con placeholders
- **Muestra datos del contrato Y su inmueble/inquilino**: Relaciones anidadas

---

## Edge cases cubiertos

1. ✅ **Sin token**: `mError.postValue("No hay sesión activa")`
2. ✅ **Lista vacía**: Muestra mensaje "No hay contratos registrados"
3. ✅ **Inmueble null**: Muestra "Inmueble no disponible"
4. ✅ **Inquilino null**: Muestra "Inquilino no disponible"
5. ✅ **Nombre inquilino null**: Muestra "No especificado"
6. ✅ **Sin deuda**: Oculta campos de deuda (View.GONE)
7. ✅ **Fechas con hora**: Formatea para mostrar solo fecha
8. ✅ **Errores HTTP**: Muestra código de error
9. ✅ **Errores de red**: Muestra mensaje de conexión

---

## Mejoras recomendadas

1. **DiffUtil en Adapter**: Reemplazar `notifyDataSetChanged()` por `DiffUtil` para mejor performance
2. **ViewBinding en Adapter**: Usar ViewBinding en lugar de `findViewById()`
3. **Coroutines**: Migrar de callbacks a `suspend fun` con `viewModelScope`
4. **Repository pattern**: Abstraer ApiClient en un Repository inyectable
5. **SingleLiveEvent para navegación**: Evitar re-navegación en rotaciones
6. **Paginación**: Si hay muchos contratos, implementar paginación
7. **Pull-to-refresh**: Agregar SwipeRefreshLayout
8. **Cache offline**: Usar Room para acceso sin conexión
9. **Tests unitarios**: Mockear ApiClient y testear ViewModel

---

## Resumen de verificación

✅ **Patrón MVVM estricto aplicado**
✅ **Separación de responsabilidades clara**
✅ **LiveData para todos los estados**
✅ **Formateo y validaciones en ViewModel**
✅ **Fragment sin lógica de negocio**
✅ **Lifecycle-aware observers**
✅ **Navigation Component para navegación**
✅ **Serialización de modelos para Bundle**
✅ **Loading states con ProgressBar**
✅ **Manejo de errores con LiveData**
