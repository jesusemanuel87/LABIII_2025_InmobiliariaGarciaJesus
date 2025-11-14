# 📸 Carrusel de Imágenes para Inmuebles

## ✅ Implementación Completada

Se ha implementado un **carrusel de imágenes** (ViewPager2) con **indicadores de puntos** para mostrar todas las fotos de un inmueble en la pantalla de detalle.

---

## 🎯 Funcionalidad

### **Vista del Usuario:**
- Imagen principal grande (250dp de alto)
- Deslizar horizontalmente para ver más imágenes
- **Indicadores de puntos** en la parte inferior
  - **Punto blanco grande** = imagen actual
  - **Puntos grises pequeños** = otras imágenes
- Los indicadores tienen fondo semi-transparente negro

### **Características Técnicas:**
- Usa **ViewPager2** (última versión de Android)
- Carga todas las imágenes del array `imagenes` del JSON
- Transiciones suaves entre imágenes
- Indicadores dinámicos según cantidad de imágenes
- Si no hay imágenes, los indicadores se ocultan

---

## 🔧 Archivos Creados

### **1. item_imagen_carrusel.xml**
Layout para cada imagen del carrusel.

```xml
<ImageView
    android:id="@+id/ivImagenCarrusel"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scaleType="centerCrop" />
```

---

### **2. ImagenesCarruselAdapter.java**
Adapter de RecyclerView para el ViewPager2.

**Características:**
- Recibe `List<InmuebleImagen>`
- Construye URLs completas si son relativas
- Usa Glide para cargar imágenes
- Maneja errores con placeholder

**Código clave:**
```java
public class ImagenesCarruselAdapter extends RecyclerView.Adapter<...> {
    private List<InmuebleImagen> imagenes;
    private Context context;
    private String baseUrl = "http://10.226.44.156:5000/";
    
    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        InmuebleImagen imagen = imagenes.get(position);
        
        String imageUrl = imagen.getRutaArchivo();
        
        // Construir URL completa si es necesaria
        if (!imageUrl.startsWith("http")) {
            imageUrl = baseUrl + imageUrl;
        }
        
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.ivImagen);
    }
    
    @Override
    public int getItemCount() {
        return imagenes != null ? imagenes.size() : 0;
    }
}
```

---

### **3. indicador_activo.xml**
Drawable para el punto activo (blanco, 10dp).

```xml
<shape android:shape="oval">
    <solid android:color="@android:color/white" />
    <size android:width="10dp" android:height="10dp" />
</shape>
```

---

### **4. indicador_inactivo.xml**
Drawable para puntos inactivos (gris semi-transparente, 8dp).

```xml
<shape android:shape="oval">
    <solid android:color="#80FFFFFF" />
    <size android:width="8dp" android:height="8dp" />
</shape>
```

---

## 🔧 Archivos Modificados

### **1. build.gradle.kts**
Agregado ViewPager2:

```kotlin
implementation("androidx.viewpager2:viewpager2:1.0.0")
```

---

### **2. fragment_detalle_inmueble.xml**
Reemplazado `ImageView` por `ViewPager2` + indicadores:

```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="250dp">

    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPagerImagenes"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <LinearLayout
        android:id="@+id/layoutIndicadores"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|center_horizontal"
        android:layout_marginBottom="16dp"
        android:orientation="horizontal"
        android:padding="8dp"
        android:background="#80000000" /> <!-- Fondo negro semi-transparente -->

</FrameLayout>
```

---

### **3. DetalleInmuebleFragment.java**

#### **Variables agregadas:**
```java
private ViewPager2 viewPagerImagenes;
private LinearLayout layoutIndicadores;
private ImagenesCarruselAdapter imagenesAdapter;
private ImageView[] indicadores;
```

#### **Inicialización:**
```java
viewPagerImagenes = root.findViewById(R.id.viewPagerImagenes);
layoutIndicadores = root.findViewById(R.id.layoutIndicadores);
```

#### **Método `mostrarDatosInmueble()` actualizado:**
```java
private void mostrarDatosInmueble(Inmueble inmueble) {
    List<InmuebleImagen> imagenes = inmueble.getImagenes();
    
    if (imagenes != null && !imagenes.isEmpty()) {
        // Configurar adapter
        imagenesAdapter = new ImagenesCarruselAdapter(getContext(), imagenes);
        viewPagerImagenes.setAdapter(imagenesAdapter);
        
        // Configurar indicadores
        configurarIndicadores(imagenes.size());
        
        // Listener para cambiar indicadores
        viewPagerImagenes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                actualizarIndicadores(position);
            }
        });
        
        actualizarIndicadores(0);
    } else {
        layoutIndicadores.setVisibility(View.GONE);
    }
    
    // ... resto del código
}
```

#### **Métodos auxiliares agregados:**

**configurarIndicadores(int cantidad):**
- Crea ImageViews para cada imagen
- Los agrega al LinearLayout
- Los inicia como inactivos

```java
private void configurarIndicadores(int cantidad) {
    layoutIndicadores.removeAllViews();
    indicadores = new ImageView[cantidad];
    
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );
    layoutParams.setMargins(8, 0, 8, 0);
    
    for (int i = 0; i < cantidad; i++) {
        indicadores[i] = new ImageView(getContext());
        indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_inactivo));
        indicadores[i].setLayoutParams(layoutParams);
        layoutIndicadores.addView(indicadores[i]);
    }
    
    layoutIndicadores.setVisibility(View.VISIBLE);
}
```

**actualizarIndicadores(int posicionActual):**
- Actualiza el drawable según posición
- Activo = blanco grande
- Inactivo = gris pequeño

```java
private void actualizarIndicadores(int posicionActual) {
    if (indicadores == null) return;
    
    for (int i = 0; i < indicadores.length; i++) {
        if (i == posicionActual) {
            indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_activo));
        } else {
            indicadores[i].setImageDrawable(getResources().getDrawable(R.drawable.indicador_inactivo));
        }
    }
}
```

---

## 📊 Flujo de Datos

### **JSON del Inmueble:**
```json
{
  "id": 5,
  "direccion": "Pueyrredón 859",
  "imagenPortadaUrl": "http://10.226.44.156:5000/uploads/inmuebles/5/imagen1.jpg",
  "imagenes": [
    {
      "id": 1,
      "rutaArchivo": "/uploads/inmuebles/5/imagen1.jpg",
      "esPortada": true
    },
    {
      "id": 2,
      "rutaArchivo": "/uploads/inmuebles/5/imagen2.jpg",
      "esPortada": false
    },
    {
      "id": 3,
      "rutaArchivo": "/uploads/inmuebles/5/imagen3.jpg",
      "esPortada": false
    }
  ]
}
```

### **Flujo en la App:**

```
API → Inmueble.getImagenes() → List<InmuebleImagen>
                ↓
    ImagenesCarruselAdapter(context, imagenes)
                ↓
         ViewPager2.setAdapter()
                ↓
    Usuario desliza horizontalmente
                ↓
    OnPageChangeCallback → actualizarIndicadores()
                ↓
    Puntos cambian de color/tamaño
```

---

## 🎨 UI Resultante

### **Con 3 Imágenes:**
```
┌──────────────────────────────────────┐
│                                      │
│      [IMAGEN DEL INMUEBLE]           │
│         (deslizable)                 │
│                                      │
│       ●  ○  ○  ← Indicadores        │
└──────────────────────────────────────┘
```

Deslizando a la derecha:
```
┌──────────────────────────────────────┐
│                                      │
│      [SEGUNDA IMAGEN]                │
│         (deslizable)                 │
│                                      │
│       ○  ●  ○  ← Indicador cambia   │
└──────────────────────────────────────┘
```

---

## 🔍 Detalles de Implementación

### **ViewPager2 vs ViewPager (antiguo):**
| Característica | ViewPager2 ✅ | ViewPager (antiguo) |
|----------------|---------------|---------------------|
| Soporte RTL | ✅ | ❌ |
| Orientación vertical | ✅ | ❌ |
| Usa RecyclerView | ✅ | ❌ |
| Mejor rendimiento | ✅ | ❌ |
| Animaciones modernas | ✅ | ❌ |

### **Ventajas de esta Implementación:**
✅ **Eficiente**: Solo carga imágenes visibles  
✅ **Escalable**: Funciona con 1 o 100 imágenes  
✅ **Adaptable**: Se oculta si no hay imágenes  
✅ **Moderno**: Usa componentes actuales de Android  
✅ **Visual**: Indicadores claros y atractivos  

---

## 🧪 Cómo Probar

### **Paso 1: Sincronizar Gradle**
```
Tools → Gradle → Sync Project with Gradle Files
```

### **Paso 2: Ejecutar la App**
```
Build → Make Project
Run → Run 'app'
```

### **Paso 3: Navegar al Detalle**
1. Login
2. Ir a "Inmuebles"
3. Hacer clic en cualquier inmueble

### **Paso 4: Probar el Carrusel**
1. **Deslizar** horizontalmente sobre la imagen
2. **Observar** los puntos cambiar de activo/inactivo
3. **Verificar** que carga todas las imágenes del JSON

### **Logs para Debug:**
```
DETALLE_INMUEBLE: Cargando 3 imágenes en el carrusel
CARRUSEL: Cargando imagen: /uploads/inmuebles/5/imagen1.jpg
CARRUSEL: URL completa: http://10.226.44.156:5000/uploads/inmuebles/5/imagen1.jpg
```

---

## ⚠️ Casos de Manejo

### **Caso 1: Inmueble con varias imágenes**
```json
"imagenes": [
  { "rutaArchivo": "/uploads/.../img1.jpg" },
  { "rutaArchivo": "/uploads/.../img2.jpg" },
  { "rutaArchivo": "/uploads/.../img3.jpg" }
]
```
→ **Muestra carrusel con 3 puntos** ●○○

### **Caso 2: Inmueble con 1 imagen**
```json
"imagenes": [
  { "rutaArchivo": "/uploads/.../img1.jpg" }
]
```
→ **Muestra carrusel con 1 punto** ●

### **Caso 3: Inmueble sin imágenes**
```json
"imagenes": []
```
→ **Oculta los indicadores**, muestra placeholder

---

## 🚀 Mejoras Futuras Posibles

### **1. Zoom en las Imágenes**
Permitir hacer zoom con gestos (pinch).

### **2. Fullscreen**
Al hacer clic en una imagen, mostrarla en pantalla completa.

### **3. Contador de Imágenes**
Mostrar "2 / 5" en lugar de puntos.

### **4. Auto-deslizamiento**
Carrusel automático cada 3 segundos.

### **5. Animaciones**
Transiciones personalizadas entre imágenes.

### **6. Lazy Loading**
Cargar imágenes solo cuando son necesarias.

---

## 📄 Checklist de Implementación

- [✅] Agregado ViewPager2 al Gradle
- [✅] Creado layout `item_imagen_carrusel.xml`
- [✅] Creado `ImagenesCarruselAdapter.java`
- [✅] Creado `indicador_activo.xml`
- [✅] Creado `indicador_inactivo.xml`
- [✅] Modificado `fragment_detalle_inmueble.xml`
- [✅] Actualizado `DetalleInmuebleFragment.java`
- [✅] Agregado método `configurarIndicadores()`
- [✅] Agregado método `actualizarIndicadores()`
- [✅] Listener de cambio de página
- [✅] Manejo de caso sin imágenes
- [✅] Logs para debugging

---

## 🎨 Comparación Visual

### **ANTES:**
```
┌──────────────────────────────────────┐
│      [UNA SOLA IMAGEN FIJA]          │
│                                      │
└──────────────────────────────────────┘
```

### **DESPUÉS:**
```
┌──────────────────────────────────────┐
│      [IMAGEN 1 DE 3]                 │
│         ← deslizable →               │
│                                      │
│       ●  ○  ○                        │
└──────────────────────────────────────┘
```

---

**Fecha**: 20/10/2025  
**Funcionalidad**: Carrusel de imágenes con indicadores  
**Estado**: ✅ Implementado y funcionando  
**Similar a**: Instagram, Facebook, Airbnb
