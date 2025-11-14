# Cambios Implementados: Disponibilidad y Estado en Inmuebles

## 🎯 Objetivo
Separar los conceptos de **Disponibilidad** y **Estado** en la lista de inmuebles:
- **Disponibilidad**: Campo del servidor (Disponible, No Disponible, Reservado) - Solo lectura
- **Estado**: Campo editable por el propietario (Activo/Inactivo) - Controlado por Switch

## 📝 Reglas de Negocio Implementadas

### ✅ Disponibilidad (Campo de Lectura)
- Muestra el campo `disponibilidad` recibido del servidor
- Valores posibles: **Disponible**, **No Disponible**, **Reservado**
- Se muestra en un TextView con colores distintivos:
  - 🟢 **Verde**: Disponible
  - 🔴 **Rojo**: No Disponible
  - 🟠 **Naranja**: Reservado

### ✅ Estado (Campo Editable)
- Muestra el campo `estado` recibido del servidor
- Valores: **Activo** / **Inactivo**
- Se muestra en un TextView con colores:
  - 🟢 **Verde**: Activo
  - 🔴 **Rojo**: Inactivo

### ✅ Switch de Estado
- **Habilitado SOLO si disponibilidad = "Disponible"**
- **Deshabilitado si disponibilidad = "No Disponible" o "Reservado"**
- Al cambiar el switch, envía el nuevo estado al servidor
- Recarga automáticamente la lista después de actualizar

---

## 🔧 Archivos Modificados

### 1. **Modelo Inmueble**
**Archivo**: `modelos/Inmueble.java`

**Cambios**:
- Agregado campo `disponibilidad` (String)
```java
@SerializedName("disponibilidad")
private String disponibilidad;

public String getDisponibilidad() {
    return disponibilidad;
}

public void setDisponibilidad(String disponibilidad) {
    this.disponibilidad = disponibilidad;
}
```

---

### 2. **Layout del Item**
**Archivo**: `res/layout/item_inmueble.xml`

**Cambios**:
- Renombrado `tvEstadoInmueble` → `tvDisponibilidadInmueble` (para mostrar disponibilidad)
- Agregado nuevo `tvEstadoInmueble` (para mostrar el estado Activo/Inactivo)
- Agregado `SwitchCompat` para cambiar el estado

**Estructura del Layout**:
```xml
<!-- Disponibilidad (Solo lectura) -->
<TextView
    android:id="@+id/tvDisponibilidadInmueble"
    android:text="Disponible" />

<!-- Estado con Switch -->
<LinearLayout>
    <TextView
        android:text="Estado:" />
    
    <TextView
        android:id="@+id/tvEstadoInmueble"
        android:text="Activo" />
    
    <SwitchCompat
        android:id="@+id/switchEstadoInmueble" />
</LinearLayout>
```

---

### 3. **Adapter de Inmuebles**
**Archivo**: `ui/inmuebles/InmueblesAdapter.java`

**Cambios**:

#### a) Nuevos atributos en ViewHolder:
```java
private TextView tvDisponibilidad;
private TextView tvEstado;
private SwitchCompat switchEstado;
```

#### b) Nueva interfaz para cambio de estado:
```java
public interface OnEstadoChangeListener {
    void onEstadoChange(Inmueble inmueble, boolean nuevoEstado);
}
```

#### c) Lógica en el método `bind()`:

**Disponibilidad**:
```java
String disponibilidad = inmueble.getDisponibilidad();
tvDisponibilidad.setText(disponibilidad);

// Colores según disponibilidad
if ("Disponible".equals(disponibilidad)) {
    tvDisponibilidad.setBackgroundColor(0xFF4CAF50); // Verde
} else if ("No Disponible".equals(disponibilidad)) {
    tvDisponibilidad.setBackgroundColor(0xFFF44336); // Rojo
} else if ("Reservado".equals(disponibilidad)) {
    tvDisponibilidad.setBackgroundColor(0xFFFF9800); // Naranja
}
```

**Estado**:
```java
String estado = inmueble.getEstado();
tvEstado.setText(estado);

// Colores según estado
if ("Activo".equals(estado)) {
    tvEstado.setTextColor(0xFF4CAF50); // Verde
} else {
    tvEstado.setTextColor(0xFFF44336); // Rojo
}
```

**Switch**:
```java
// Configurar switch según estado actual
boolean esActivo = "Activo".equals(estado);
switchEstado.setChecked(esActivo);

// ⚠️ Habilitar switch SOLO si disponibilidad = "Disponible"
boolean puedeModificar = "Disponible".equals(disponibilidad);
switchEstado.setEnabled(puedeModificar);

// Listener para cambio de estado
switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
    if (estadoListener != null && buttonView.isPressed()) {
        estadoListener.onEstadoChange(inmueble, isChecked);
    }
});
```

---

### 4. **Fragment de Inmuebles**
**Archivo**: `ui/inmuebles/InmueblesFragment.java`

**Cambios**:
- Agregado listener para cambios de estado del switch

```java
adapter.setOnEstadoChangeListener(new InmueblesAdapter.OnEstadoChangeListener() {
    @Override
    public void onEstadoChange(Inmueble inmueble, boolean nuevoEstado) {
        String estadoTexto = nuevoEstado ? "Activo" : "Inactivo";
        mv.cambiarEstadoInmueble(inmueble.getId(), estadoTexto);
    }
});
```

---

### 5. **ViewModel de Inmuebles**
**Archivo**: `ui/inmuebles/InmueblesViewModel.java`

**Cambios**:
- Actualizado método `cambiarEstadoInmueble` para recibir `String estado` en lugar de `boolean`

```java
public void cambiarEstadoInmueble(int inmuebleId, String estado) {
    Log.d("INMUEBLES", "Cambiar estado inmueble " + inmuebleId + " a: " + estado);
    
    ActualizarEstadoInmuebleRequest request = new ActualizarEstadoInmuebleRequest(estado);
    ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
    Call<ApiResponse<Inmueble>> call = api.actualizarEstadoInmueble(token, inmuebleId, request);
    
    // ... llamada al servidor y recarga de lista
}
```

---

### 6. **Modelo de Request**
**Archivo**: `modelos/ActualizarEstadoInmuebleRequest.java`

**Cambios**:
- Cambiado de `boolean disponible` a `String estado`

**Antes**:
```java
@SerializedName("disponible")
private boolean disponible;
```

**Después**:
```java
@SerializedName("estado")
private String estado;
```

---

## 📊 Flujo de Datos

### Carga de Inmuebles:
```
API → JSON → Inmueble (con disponibilidad y estado)
                ↓
         InmueblesViewModel
                ↓
         InmueblesAdapter
                ↓
         UI (muestra ambos campos)
```

### Cambio de Estado:
```
Usuario cambia Switch (si está habilitado)
                ↓
         InmueblesAdapter.OnEstadoChangeListener
                ↓
         InmueblesFragment
                ↓
         InmueblesViewModel.cambiarEstadoInmueble()
                ↓
         API (PUT con estado: "Activo" o "Inactivo")
                ↓
         Recarga lista automáticamente
```

---

## 🎨 UI Resultante

Para cada inmueble se mostrará:

```
┌─────────────────────────────────────┐
│  [Imagen del Inmueble]              │
├─────────────────────────────────────┤
│  Pueyrredón 859                     │
│  Quinta                             │
│  $ 400,000.00    8 ambientes        │
│                                     │
│  [Disponible]  ← Verde/Rojo/Naranja │
│                                     │
│  Estado: Activo  [●─────]  ← Switch │
└─────────────────────────────────────┘
```

---

## ⚠️ Casos de Uso

### Caso 1: Inmueble Disponible ✅
```json
{
  "disponibilidad": "Disponible",
  "estado": "Activo"
}
```
- Badge **verde** "Disponible"
- Estado muestra **"Activo"** en verde
- Switch **habilitado** y marcado
- Usuario **puede cambiar** el estado

---

### Caso 2: Inmueble No Disponible 🚫
```json
{
  "disponibilidad": "No Disponible",
  "estado": "Activo"
}
```
- Badge **rojo** "No Disponible"
- Estado muestra **"Activo"** en verde
- Switch **deshabilitado** (gris)
- Usuario **NO puede cambiar** el estado

---

### Caso 3: Inmueble Reservado 🔒
```json
{
  "disponibilidad": "Reservado",
  "estado": "Activo"
}
```
- Badge **naranja** "Reservado"
- Estado muestra **"Activo"** en verde
- Switch **deshabilitado** (gris)
- Usuario **NO puede cambiar** el estado

---

### Caso 4: Inmueble Inactivo pero Disponible
```json
{
  "disponibilidad": "Disponible",
  "estado": "Inactivo"
}
```
- Badge **verde** "Disponible"
- Estado muestra **"Inactivo"** en rojo
- Switch **habilitado** pero desmarcado
- Usuario **puede cambiar** a Activo

---

## 🔍 Logs para Debug

Al cargar inmuebles, el ViewModel imprime:
```
INMUEBLES: Inmuebles cargados: 3
INMUEBLES: Inmueble ID 5 - imagenPortadaUrl: http://...
```

Al cambiar estado:
```
INMUEBLES: Cambiar estado inmueble 5 a: Activo
INMUEBLES: Estado actualizado correctamente
```

---

## 📋 Checklist de Verificación

- [✅] Modelo `Inmueble` tiene campo `disponibilidad`
- [✅] Layout tiene TextView para disponibilidad
- [✅] Layout tiene TextView para estado
- [✅] Layout tiene Switch para cambiar estado
- [✅] Adapter muestra disponibilidad con colores
- [✅] Adapter muestra estado con colores
- [✅] Switch solo habilitado si disponibilidad = "Disponible"
- [✅] Switch cambia entre Activo/Inactivo
- [✅] Fragment maneja listener del switch
- [✅] ViewModel envía estado al servidor
- [✅] Recarga automática después de cambiar

---

## 🚀 Próximos Pasos

1. **Sincronizar Gradle** (si aún no lo has hecho)
2. **Ejecutar la app**
3. **Navegar a Inmuebles**
4. **Verificar**:
   - Se muestran correctamente disponibilidad y estado
   - Switch solo habilitado en inmuebles "Disponibles"
   - Al cambiar el switch, se actualiza el estado
   - La lista se recarga automáticamente

---

**Fecha**: 20/10/2025  
**Versión**: Separación Disponibilidad/Estado
