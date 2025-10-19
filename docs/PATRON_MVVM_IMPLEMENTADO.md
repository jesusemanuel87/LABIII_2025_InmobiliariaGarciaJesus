# 📐 Patrón MVVM Implementado - API REST

## ✅ Implementación Completa

Se ha implementado el patrón MVVM siguiendo **exactamente** la estructura utilizada en `PerfilFragment`/`PerfilViewModel` y `LoginActivity`/`LoginActivityViewModel`.

---

## 🎯 Componentes Implementados

### **1. InmueblesViewModel + InmueblesFragment**

#### **InmueblesViewModel.java**
```java
public class InmueblesViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Inmueble>> mInmuebles;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;
    
    // Métodos:
    - LiveData<List<Inmueble>> getMInmuebles()
    - LiveData<String> getMError()
    - LiveData<Boolean> getMCargando()
    - void cargarInmuebles()
    - void cambiarEstadoInmueble(int inmuebleId, boolean disponible)
}
```

**Características**:
- ✅ Extiende `AndroidViewModel` (igual que PerfilViewModel)
- ✅ Usa `MutableLiveData` para datos observables
- ✅ Método `getMInmuebles()` con patrón lazy initialization
- ✅ Manejo de errores con `getMError()`
- ✅ Estado de carga con `getMCargando()`
- ✅ Usa `ApiClient.getToken()` y `ApiClient.getMyApiInterface()`
- ✅ Callback con `enqueue()` para llamadas asíncronas
- ✅ Logging con `Log.d()` para debugging

#### **InmueblesFragment.java**
```java
public class InmueblesFragment extends Fragment {
    private InmueblesViewModel mv;
    private RecyclerView recyclerView;
    private InmueblesAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvMensaje;
    
    // Observers configurados en onCreateView
}
```

**Características**:
- ✅ ViewModelProvider para obtener ViewModel (igual que PerfilFragment)
- ✅ Observers con `observe(getViewLifecycleOwner(), ...)`
- ✅ RecyclerView con LinearLayoutManager
- ✅ Adapter pattern con `InmueblesAdapter`
- ✅ ProgressBar para estado de carga
- ✅ TextView para mensajes cuando no hay datos
- ✅ Toast para mostrar errores

#### **InmueblesAdapter.java**
```java
public class InmueblesAdapter extends RecyclerView.Adapter<ViewHolder> {
    - Constructor con List<Inmueble> y Context
    - setInmuebles() para actualizar datos
    - Interface OnInmuebleClickListener para clicks
    - ViewHolder pattern con bind()
}
```

---

### **2. ContratosViewModel + ContratosFragment**

#### **ContratosViewModel.java**
```java
public class ContratosViewModel extends AndroidViewModel {
    private final Context context;
    private MutableLiveData<List<Contrato>> mContratos;
    private MutableLiveData<String> mError;
    private MutableLiveData<Boolean> mCargando;
    
    // Métodos:
    - LiveData<List<Contrato>> getMContratos()
    - LiveData<String> getMError()
    - LiveData<Boolean> getMCargando()
    - void cargarContratos()
    - void cargarContratosPorInmueble(int inmuebleId)
}
```

**Mismas características** que InmueblesViewModel siguiendo el patrón MVVM.

#### **ContratosFragment.java**
Implementación **idéntica** a InmueblesFragment:
- Observers configurados
- RecyclerView con ContratosAdapter
- ProgressBar y TextView de mensaje
- Toast para errores

#### **ContratosAdapter.java**
Adapter pattern igual que InmueblesAdapter con:
- Constructor estándar
- setContratos() method
- Interface OnContratoClickListener
- ViewHolder con bind()

---

### **3. LoginActivityViewModel Actualizado**

**Cambios implementados**:
```java
// NUEVO: Método con nueva API REST
public void loginNuevo(String email, String password) {
    // Usa LoginRequest y LoginResponse
    // Guarda token Y propietario completo
    ApiClient.guardarToken(context, loginResponse.getToken());
    ApiClient.guardarPropietario(context, loginResponse.getPropietario());
}

// LEGACY: Mantiene compatibilidad
public void Login(String usuario, String clave) {
    loginLegacy(usuario, clave);
}
```

**Características**:
- ✅ Método `loginNuevo()` usa nueva API REST
- ✅ Maneja `ApiResponse<LoginResponse>`
- ✅ Guarda `Propietario` completo (no solo token)
- ✅ LiveData para estado de carga agregado
- ✅ Compatibilidad total con código legacy
- ✅ Mismo patrón de Observers y manejo de errores

---

### **4. PerfilViewModel Actualizado**

**Refactorización**:
```java
public void cargarPerfil() {
    cargarPerfilLegacy(); // Por ahora usa API legacy
}

// Método legacy mantiene funcionalidad actual
private void cargarPerfilLegacy() {
    // Implementación actual sin cambios
}
```

**Preparado para migración futura** a:
```java
public void cargarPerfilNuevo() {
    // Usará api.obtenerPerfil(token)
    // Retornará ApiResponse<Propietario>
}
```

---

## 📁 Estructura de Archivos Creados/Modificados

### **ViewModels (4 archivos)**
```
ui/
├── inmuebles/
│   └── InmueblesViewModel.java ✨ NUEVO (113 líneas)
├── contratos/
│   └── ContratosViewModel.java ✨ NUEVO (144 líneas)
├── LoginActivityViewModel.java 🔄 ACTUALIZADO (134 líneas)
└── perfil/
    └── PerfilViewModel.java 🔄 ACTUALIZADO (220 líneas)
```

### **Fragments (2 archivos)**
```
ui/
├── inmuebles/
│   └── InmueblesFragment.java 🔄 ACTUALIZADO (100 líneas)
└── contratos/
    └── ContratosFragment.java 🔄 ACTUALIZADO (100 líneas)
```

### **Adapters (2 archivos)**
```
ui/
├── inmuebles/
│   └── InmueblesAdapter.java ✨ NUEVO (101 líneas)
└── contratos/
    └── ContratosAdapter.java ✨ NUEVO (101 líneas)
```

### **Layouts (4 archivos)**
```
res/layout/
├── fragment_inmuebles.xml 🔄 ACTUALIZADO
├── fragment_contratos.xml 🔄 ACTUALIZADO
├── item_inmueble.xml ✨ NUEVO
└── item_contrato.xml ✨ NUEVO
```

**Total: 12 archivos** (6 nuevos + 6 actualizados)

---

## 🎨 Patrón MVVM - Comparación

### **Antes (Perfil)**
```java
PerfilFragment
├── PerfilViewModel mv
├── Observers para:
│   ├── mPropietario
│   ├── mError
│   ├── mModoEdicion
│   └── otros LiveData de UI
└── binding para views
```

### **Ahora (Inmuebles/Contratos)**
```java
InmueblesFragment / ContratosFragment
├── ViewModel mv
├── RecyclerView + Adapter
├── Observers para:
│   ├── mInmuebles / mContratos
│   ├── mError
│   └── mCargando
└── findViewById para views
```

**Patrón consistente aplicado** ✅

---

## 🔄 Flujo de Datos MVVM

```
┌─────────────────┐
│   Fragment      │
│  (Vista)        │
└────────┬────────┘
         │ observe()
         ↓
┌─────────────────┐
│   ViewModel     │
│  (Lógica)       │
└────────┬────────┘
         │ ApiClient
         ↓
┌─────────────────┐
│   Retrofit      │
│  (Red)          │
└────────┬────────┘
         │ HTTP
         ↓
┌─────────────────┐
│   API REST      │
│  (Servidor)     │
└─────────────────┘
```

---

## ✨ Características del Patrón Implementado

### **1. AndroidViewModel**
- ✅ Acceso al `Application` context
- ✅ Sobrevive a cambios de configuración
- ✅ Manejo correcto del ciclo de vida

### **2. LiveData**
- ✅ Observable data holders
- ✅ Lifecycle-aware
- ✅ Lazy initialization pattern
- ✅ postValue() para hilos background

### **3. Observers**
- ✅ Registrados con `getViewLifecycleOwner()`
- ✅ Se limpian automáticamente
- ✅ Actualizan UI reactivamente

### **4. Retrofit + Callbacks**
- ✅ Llamadas asíncronas con `enqueue()`
- ✅ onResponse() y onFailure() handlers
- ✅ Manejo de ApiResponse<T> wrapper

### **5. RecyclerView Pattern**
- ✅ Adapter con ViewHolder
- ✅ Método setData() para actualizar
- ✅ Interface para clicks
- ✅ bind() method en ViewHolder

---

## 📊 Comparación con Ejemplos del Proyecto

| Característica | PerfilViewModel | InmueblesViewModel | ContratosViewModel |
|----------------|-----------------|--------------------|--------------------|
| **Extiende** | AndroidViewModel ✅ | AndroidViewModel ✅ | AndroidViewModel ✅ |
| **Context** | ✅ | ✅ | ✅ |
| **LiveData** | 7 LiveData | 3 LiveData | 3 LiveData |
| **ApiClient** | ✅ | ✅ | ✅ |
| **Token** | SharedPreferences ✅ | ApiClient.getToken() ✅ | ApiClient.getToken() ✅ |
| **Logging** | Log.d("PERFIL") ✅ | Log.d("INMUEBLES") ✅ | Log.d("CONTRATOS") ✅ |
| **Callbacks** | enqueue() ✅ | enqueue() ✅ | enqueue() ✅ |
| **Error Handling** | postValue() ✅ | postValue() ✅ | postValue() ✅ |

| Característica | PerfilFragment | InmueblesFragment | ContratosFragment |
|----------------|----------------|-------------------|-------------------|
| **ViewModelProvider** | ✅ | ✅ | ✅ |
| **Observers** | 7 observers | 3 observers | 3 observers |
| **Toast errors** | ✅ | ✅ | ✅ |
| **Lifecycle owner** | getViewLifecycleOwner() ✅ | getViewLifecycleOwner() ✅ | getViewLifecycleOwner() ✅ |
| **UI Updates** | binding ✅ | findViewById ✅ | findViewById ✅ |
| **Lista datos** | - | RecyclerView ✅ | RecyclerView ✅ |

---

## 🚀 Cómo Usar

### **Ver Inmuebles**
```java
// En MainActivity o Navigation
navController.navigate(R.id.nav_inmuebles);

// El fragment automáticamente:
// 1. Crea el ViewModel
// 2. Configura observers
// 3. Llama a cargarInmuebles()
// 4. Muestra ProgressBar
// 5. Actualiza RecyclerView con datos
// 6. Oculta ProgressBar
```

### **Ver Contratos**
```java
// Similar a inmuebles
navController.navigate(R.id.nav_contratos);
```

### **Login con nueva API**
```java
// Para migrar a nueva API, en LoginActivity:
// Cambiar: mv.Login(usuario, clave);
// Por:     mv.loginNuevo(email, password);
```

---

## 📝 Ventajas del Patrón MVVM Implementado

1. **Separación de Responsabilidades**: Fragment solo maneja UI, ViewModel maneja lógica
2. **Testeable**: ViewModels pueden testearse sin UI
3. **Mantenible**: Código organizado y predecible
4. **Reutilizable**: Adapters y ViewModels reutilizables
5. **Lifecycle-aware**: No memory leaks por observers
6. **Consistente**: Mismo patrón en todo el proyecto

---

## ✅ Checklist de Cumplimiento

- [x] AndroidViewModel con Context
- [x] MutableLiveData para datos observables
- [x] Getters con lazy initialization
- [x] ApiClient para networking
- [x] Retrofit con enqueue() callbacks
- [x] Observers en Fragment
- [x] RecyclerView + Adapter pattern
- [x] ProgressBar para carga
- [x] Toast para errores
- [x] Logging para debug
- [x] Manejo de ApiResponse<T>
- [x] Layouts con CardView
- [x] ViewHolder con bind()
- [x] Interface para clicks

---

## 🎉 Resultado

**Patrón MVVM implementado correctamente** siguiendo **exactamente** los ejemplos de `PerfilFragment`/`PerfilViewModel` y `LoginActivity`/`LoginActivityViewModel` del proyecto.

La implementación está **lista para usar** y **100% consistente** con el código existente.

---

**Desarrollador**: García Jesús Emanuel  
**Fecha**: Octubre 2025  
**Patrón**: MVVM (Model-View-ViewModel)
