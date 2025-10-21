# 📍 Ver Ubicación de Inmuebles en Mapa

## ✅ Implementación Completada

Se ha implementado la funcionalidad para ver la ubicación de cada inmueble en un mapa **reutilizando** el fragment de ubicación existente.

---

## 🎯 Funcionalidad

### **En el Detalle del Inmueble:**
- Botón **"📍 Ver Ubicación en Mapa"**
- Al hacer clic, navega al fragment de ubicación
- Muestra el marcador en la posición exacta del inmueble

### **En el Fragment de Ubicación:**
- **Sin parámetros**: Muestra mapa general de San Luis (zoom 10)
- **Con parámetros**: Muestra ubicación específica del inmueble (zoom 17)

---

## 🔧 Archivos Modificados

### **1. UbicacionViewModel.java**

#### Cambios:
- ✅ Agregado método `obtenerMapaInmueble(lat, lng, titulo)`
- ✅ Modificado `obtenerMapa()` para usar constructor parametrizado
- ✅ Clase `MapaActual` ahora acepta coordenadas personalizadas

#### Código clave:
```java
public void obtenerMapaInmueble(double latitud, double longitud, String titulo){
    // Mapa de un inmueble específico con zoom 17
    MapaActual mapaActual = new MapaActual(latitud, longitud, titulo, 17);
    mMapa.setValue(mapaActual);
}

public class MapaActual implements OnMapReadyCallback {
    private LatLng ubicacion;
    private String titulo;
    private float zoom;

    public MapaActual(double latitud, double longitud, String titulo, float zoom) {
        this.ubicacion = new LatLng(latitud, longitud);
        this.titulo = titulo;
        this.zoom = zoom;
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MarkerOptions marcador = new MarkerOptions();
        marcador.position(ubicacion);
        marcador.title(titulo);
        marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        
        googleMap.addMarker(marcador);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubicacion)
                .zoom(zoom)
                .bearing(45)
                .tilt(45)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
```

---

### **2. UbicacionFragment.java**

#### Cambios:
- ✅ Verifica si recibe argumentos (Bundle)
- ✅ Si recibe lat/lng → muestra mapa del inmueble
- ✅ Si NO recibe → muestra mapa general

#### Código clave:
```java
// Verificar si hay argumentos (inmueble específico)
if (getArguments() != null) {
    double latitud = getArguments().getDouble("latitud", 0);
    double longitud = getArguments().getDouble("longitud", 0);
    String titulo = getArguments().getString("titulo", "Ubicación del Inmueble");
    
    if (latitud != 0 && longitud != 0) {
        // Mostrar mapa del inmueble específico
        mv.obtenerMapaInmueble(latitud, longitud, titulo);
    } else {
        // Mostrar mapa por defecto
        mv.obtenerMapa();
    }
} else {
    // Mostrar mapa por defecto
    mv.obtenerMapa();
}
```

---

### **3. fragment_detalle_inmueble.xml**

#### Cambios:
- ✅ Agregado botón "Ver Ubicación en Mapa"

#### Código:
```xml
<Button
    android:id="@+id/btnVerEnMapa"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="16dp"
    android:text="📍 Ver Ubicación en Mapa"
    android:textSize="16sp"
    android:backgroundTint="@android:color/holo_blue_dark"
    android:textColor="@android:color/white"
    android:drawableLeft="@android:drawable/ic_menu_mylocation"
    android:drawablePadding="8dp" />
```

---

### **4. DetalleInmuebleFragment.java**

#### Cambios:
- ✅ Agregado `Button btnVerEnMapa`
- ✅ Agregado `Inmueble inmuebleActual` para guardar los datos
- ✅ Agregado listener del botón que navega con argumentos

#### Código clave:
```java
// Guardar el inmueble actual
inmuebleActual = inmueble;

// Listener del botón Ver en Mapa
btnVerEnMapa.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (inmuebleActual != null && 
            inmuebleActual.getLatitud() != null && 
            inmuebleActual.getLongitud() != null) {
            
            // Crear bundle con los datos del inmueble
            Bundle bundle = new Bundle();
            bundle.putDouble("latitud", inmuebleActual.getLatitud());
            bundle.putDouble("longitud", inmuebleActual.getLongitud());
            bundle.putString("titulo", inmuebleActual.getDireccion());
            
            // Navegar al fragment de ubicación
            Navigation.findNavController(v).navigate(R.id.nav_ubicacion, bundle);
        } else {
            Toast.makeText(getContext(), 
                "No hay coordenadas disponibles para este inmueble", 
                Toast.LENGTH_SHORT).show();
        }
    }
});
```

---

## 📊 Flujo de Navegación

```
DetalleInmuebleFragment
         ↓
Usuario hace clic en "Ver Ubicación en Mapa"
         ↓
Se crea Bundle con:
  - latitud: -33.67635087
  - longitud: -65.46774590
  - titulo: "Pueyrredón 859"
         ↓
Navigation.navigate(R.id.nav_ubicacion, bundle)
         ↓
UbicacionFragment recibe los argumentos
         ↓
UbicacionFragment llama a:
  mv.obtenerMapaInmueble(lat, lng, titulo)
         ↓
UbicacionViewModel crea MapaActual con:
  - Coordenadas del inmueble
  - Zoom 17 (cerca)
  - Marcador rojo
         ↓
Mapa se muestra centrado en el inmueble
```

---

## 🎨 UI Resultante

### **Pantalla de Detalle:**
```
┌─────────────────────────────────────┐
│  [Imagen del Inmueble]              │
├─────────────────────────────────────┤
│  Pueyrredón 859                     │
│  Villa Mercedes, San Luis           │
│  ...                                │
│  $ 400,000.00                       │
├─────────────────────────────────────┤
│  [Disponible]                       │
│  Estado: Inactivo  [Switch]         │
├─────────────────────────────────────┤
│  [📍 Ver Ubicación en Mapa]         │ ← Nuevo botón
└─────────────────────────────────────┘
```

### **Pantalla de Mapa (después de clic):**
```
┌─────────────────────────────────────┐
│           Google Maps               │
│                                     │
│         [📍 Marcador Rojo]          │
│         Pueyrredón 859              │
│                                     │
│  (Vista híbrida, zoom 17)           │
└─────────────────────────────────────┘
```

---

## 🔍 Características del Mapa del Inmueble

| Característica | Valor |
|----------------|-------|
| Tipo de mapa | Híbrido (satélite + calles) |
| Zoom | 17 (muy cerca) |
| Bearing (rotación) | 45° |
| Tilt (inclinación) | 45° |
| Color del marcador | Rojo |
| Título del marcador | Dirección del inmueble |

---

## 🧪 Cómo Probar

### **Paso 1: Ejecutar la App**
```bash
# Sincronizar Gradle
# Build → Make Project
# Run App
```

### **Paso 2: Navegar a un Inmueble**
1. Login
2. Ir a "Inmuebles"
3. Hacer clic en cualquier inmueble

### **Paso 3: Ver el Mapa**
1. En el detalle, hacer clic en **"📍 Ver Ubicación en Mapa"**
2. Se debe abrir el mapa centrado en el inmueble
3. Debe aparecer un marcador rojo en la ubicación exacta
4. El título del marcador debe ser la dirección

### **Paso 4: Volver al Menú Principal**
1. Ir a "Inicio" o "Ubicación" desde el menú
2. Debe mostrar el mapa general de San Luis

---

## ⚠️ Validaciones Implementadas

### **1. Si el inmueble NO tiene coordenadas:**
```java
if (inmuebleActual.getLatitud() != null && inmuebleActual.getLongitud() != null) {
    // Navegar al mapa
} else {
    Toast: "No hay coordenadas disponibles para este inmueble"
}
```

### **2. Si el JSON tiene latitud/longitud en 0:**
```java
if (latitud != 0 && longitud != 0) {
    // Mostrar mapa del inmueble
} else {
    // Mostrar mapa por defecto
}
```

---

## 📝 Datos del JSON Usados

El modelo `Inmueble` ya tenía los campos:
```java
@SerializedName("latitud")
private Double latitud;

@SerializedName("longitud")
private Double longitud;
```

Ejemplo del JSON:
```json
{
  "id": 5,
  "direccion": "Pueyrredón 859",
  "latitud": -33.67635087,
  "longitud": -65.46774590,
  ...
}
```

---

## 🎯 Ventajas de Reutilizar el Fragment

✅ **No duplicar código**  
✅ **Mismo diseño y comportamiento**  
✅ **Fácil mantenimiento**  
✅ **Los permisos ya están implementados**  
✅ **Fragment versátil (general o específico)**

---

## 🚀 Posibles Mejoras Futuras

### **1. Agregar botón en el Adapter de Inmuebles**
Mostrar un ícono de mapa en cada item del RecyclerView.

### **2. Mostrar múltiples inmuebles**
En el mapa general, mostrar marcadores de todos los inmuebles.

### **3. Abrir Google Maps externa**
```java
Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(Inmueble)");
Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
mapIntent.setPackage("com.google.android.apps.maps");
startActivity(mapIntent);
```

### **4. Agregar ruta desde ubicación actual**
Usar Directions API para mostrar cómo llegar al inmueble.

---

## 📄 Checklist de Implementación

- [✅] Modificado `UbicacionViewModel` para aceptar parámetros
- [✅] Modificado `UbicacionFragment` para leer argumentos
- [✅] Agregado botón en `fragment_detalle_inmueble.xml`
- [✅] Agregado listener en `DetalleInmuebleFragment.java`
- [✅] Validación de coordenadas nulas
- [✅] Navegación con Bundle
- [✅] Marcador personalizado (rojo)
- [✅] Zoom apropiado para inmuebles (17)
- [✅] Toast de error si no hay coordenadas

---

**Fecha**: 20/10/2025  
**Funcionalidad**: Ver ubicación de inmuebles en mapa
**Estado**: ✅ Implementado y funcionando
