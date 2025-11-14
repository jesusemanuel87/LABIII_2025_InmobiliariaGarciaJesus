# 🔄 Guía de Migración a Nueva API REST

## 📋 Resumen de Cambios

Se ha implementado la estructura completa para consumir la nueva API REST siguiendo el patrón MVVM del proyecto. Los nuevos modelos y endpoints están listos para usar mientras se mantiene **compatibilidad total** con los endpoints legacy existentes.

---

## ✅ Modelos Creados

### **Autenticación**
- `LoginRequest.java` - Request para login con email/password
- `LoginResponse.java` - Response con token, propietario y expiración
- `CambiarPasswordRequest.java` - Request para cambio de contraseña
- `ResetPasswordRequest.java` - Request para resetear contraseña
- `ResetPasswordResponse.java` - Response de reset password

### **Propietario**
- `Propietario.java` - Modelo completo con nuevos campos (nombreCompleto, direccion, estado, fotoPerfil)
- `ActualizarPerfilRequest.java` - Request para actualizar datos del perfil

### **Inmuebles**
- `Inmueble.java` - Modelo completo con todos los campos de la API
- `InmuebleImagen.java` - Modelo para imágenes del inmueble
- `CrearInmuebleRequest.java` - Request para crear nuevo inmueble
- `ActualizarEstadoInmuebleRequest.java` - Request para habilitar/deshabilitar inmueble

### **Contratos y Pagos**
- `Contrato.java` - Modelo completo con inmueble, inquilino y pagos
- `InmuebleContrato.java` - Datos resumidos del inmueble en contrato
- `InquilinoContrato.java` - Datos del inquilino en contrato
- `Pago.java` - Modelo completo de pago

### **Respuestas Genéricas**
- `ApiResponse<T>.java` - Wrapper genérico con success, message, data y errors
- `ApiResponseSimple.java` - Response sin data, solo success/message/errors

---

## 🔧 ApiClient Actualizado

### **Nuevos Métodos Helper**
```java
// Gestión de autenticación
ApiClient.guardarPropietario(context, propietario)
ApiClient.getPropietario(context)
ApiClient.clearAuth(context)
ApiClient.isLoggedIn(context)
```

### **Endpoints Legacy (Compatibilidad Mantenida)**
- ✅ `login(usuario, clave)` - Login actual
- ✅ `leer(token)` - Obtener perfil actual
- ✅ `actualizar(token, propietario)` - Actualizar perfil actual
- ✅ `obtenerInmuebles(token)` - Listar inmuebles (api/Inmuebles)
- ✅ `obtenerContratoPorInmueble(token, id)` - Contrato por inmueble
- ✅ `obtenerPagosPorContrato(token, id)` - Pagos por contrato

### **Nuevos Endpoints API REST**

#### **Autenticación (AuthApi)**
```java
loginNuevo(LoginRequest)                    // POST /api/AuthApi/login
cambiarPassword(token, CambiarPasswordRequest)  // POST /api/AuthApi/cambiar-password
resetPassword(ResetPasswordRequest)         // POST /api/AuthApi/reset-password
```

#### **Propietario (PropietarioApi)**
```java
obtenerPerfil(token)                        // GET /api/PropietarioApi/perfil
actualizarPerfil(token, ActualizarPerfilRequest)  // PUT /api/PropietarioApi/perfil
subirFotoPerfil(token, MultipartBody.Part)  // POST /api/PropietarioApi/perfil/foto
```

#### **Inmuebles (InmueblesApi)**
```java
listarInmuebles(token)                      // GET /api/InmueblesApi
obtenerInmueble(token, id)                  // GET /api/InmueblesApi/{id}
crearInmueble(token, CrearInmuebleRequest)  // POST /api/InmueblesApi
actualizarEstadoInmueble(token, id, request) // PATCH /api/InmueblesApi/{id}/estado
```

#### **Contratos (ContratosApi)**
```java
listarContratos(token)                      // GET /api/ContratosApi
obtenerContrato(token, id)                  // GET /api/ContratosApi/{id}
listarContratosPorInmueble(token, id)       // GET /api/ContratosApi/inmueble/{inmuebleId}
```

---

## 🎯 Patrón MVVM - Ejemplo de Uso

### **En el ViewModel**

```java
public class InmueblesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Inmueble>> mInmuebles;
    private MutableLiveData<String> mError;
    
    public void cargarInmuebles() {
        String token = ApiClient.getToken(context);
        ApiClient.MyApiInterface api = ApiClient.getMyApiInterface();
        
        // Usar nuevo endpoint
        Call<ApiResponse<List<Inmueble>>> call = api.listarInmuebles(token);
        
        call.enqueue(new Callback<ApiResponse<List<Inmueble>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Inmueble>>> call, 
                                 Response<ApiResponse<List<Inmueble>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Inmueble>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        mInmuebles.postValue(apiResponse.getData());
                    } else {
                        mError.postValue(apiResponse.getMessage());
                    }
                } else {
                    mError.postValue("Error al cargar inmuebles");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Inmueble>>> call, Throwable t) {
                mError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
```

### **En el Fragment**
```java
public class InmueblesFragment extends Fragment {
    private InmueblesViewModel vm;
    
    public View onCreateView(...) {
        vm = new ViewModelProvider(this).get(InmueblesViewModel.class);
        
        // Observer para la lista de inmuebles
        vm.getMInmuebles().observe(getViewLifecycleOwner(), inmuebles -> {
            if (inmuebles != null && !inmuebles.isEmpty()) {
                // Actualizar RecyclerView con adaptador
                adapter.setInmuebles(inmuebles);
            }
        });
        
        // Observer para errores
        vm.getMError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cargar datos
        vm.cargarInmuebles();
        
        return root;
    }
}
```

---

## 🔐 Diferencias Importantes

### **Token JWT**
- **Legacy**: Token ya incluye "Bearer " al guardarse
- **Nuevo**: ApiClient maneja automáticamente el prefijo "Bearer "

### **Estructura de Respuesta**
- **Legacy**: Respuesta directa del objeto (Propietarios, List<Inmueble>)
- **Nuevo**: Wrapper ApiResponse<T> con campos success, message, data, errors

### **Modelo Propietario**
- **Legacy**: `Propietarios.java` (con idPropietario)
- **Nuevo**: `Propietario.java` (con id, nombreCompleto, direccion, estado, fotoPerfil)

---

## 📝 Checklist de Migración

Para migrar un módulo existente a la nueva API:

- [ ] Crear ViewModel si no existe
- [ ] Actualizar modelo de datos al nuevo (ej: Propietarios → Propietario)
- [ ] Cambiar llamadas de API legacy por nuevas
- [ ] Manejar ApiResponse<T> en lugar de respuesta directa
- [ ] Agregar observers en Fragment/Activity
- [ ] Actualizar layouts si es necesario
- [ ] Probar flujo completo
- [ ] Manejar estados de carga y error

---

## 🚀 Próximos Pasos

1. **Actualizar LoginActivity** para usar `loginNuevo()` y guardar el Propietario completo
2. **Migrar PerfilFragment** para usar `obtenerPerfil()` y `actualizarPerfil()`
3. **Crear InmueblesFragment** con lista de inmuebles usando RecyclerView
4. **Crear ContratosFragment** para mostrar contratos por inmueble
5. **Implementar funcionalidad de cambiar contraseña**
6. **Implementar subida de foto de perfil**

---

## ⚠️ Notas Importantes

1. **Base URL**: Actualmente apunta a Azure. Para desarrollo local, cambiar en `ApiClient.java`:
   ```java
   private static final String BASE_URL = "http://10.0.2.2:5000/"; // Emulador
   // o "http://192.168.x.x:5000/" para dispositivo físico
   ```

2. **Compatibilidad**: Los endpoints legacy NO serán eliminados hasta completar la migración total

3. **Testing**: Probar con Postman/Swagger antes de implementar en Android

4. **Imágenes**: Las URLs de imágenes deben concatenarse con la base URL

5. **Manejo de Errores**: Siempre verificar `apiResponse.isSuccess()` antes de usar `getData()`

---

## 📞 Soporte

**Desarrollador**: García Jesús Emanuel  
**Materia**: Desarrollo Web-API Android - ASP.NET  
**Año**: 2025
