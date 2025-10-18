# 📱 Modelos Kotlin para Android

Modelos de datos para consumir la API REST de Inmobiliaria García Jesús.

---

## 📦 Dependencias (build.gradle)

```gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Gson
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // OkHttp (logging)
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

---

## 🔐 Auth Models

### LoginRequest.kt
```kotlin
data class LoginRequest(
    val email: String,
    val password: String
)
```

### LoginResponse.kt
```kotlin
data class LoginResponse(
    val token: String,
    val propietario: Propietario,
    val expiracion: String
)
```

### CambiarPasswordRequest.kt
```kotlin
data class CambiarPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String,
    val passwordConfirmacion: String
)
```

### ResetPasswordRequest.kt
```kotlin
data class ResetPasswordRequest(
    val email: String,
    val dni: String
)
```

### ResetPasswordResponse.kt
```kotlin
data class ResetPasswordResponse(
    val success: Boolean,
    val message: String,
    val nuevaPassword: String?
)
```

---

## 👤 Propietario Models

### Propietario.kt
```kotlin
data class Propietario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val nombreCompleto: String,
    val dni: String,
    val telefono: String,
    val email: String,
    val direccion: String?,
    val estado: Boolean,
    val fotoPerfil: String?
)
```

### ActualizarPerfilRequest.kt
```kotlin
data class ActualizarPerfilRequest(
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val direccion: String?
)
```

---

## 🏠 Inmueble Models

### Inmueble.kt
```kotlin
data class Inmueble(
    val id: Int,
    val direccion: String,
    val localidad: String?,
    val provincia: String?,
    val tipoId: Int,
    val tipoNombre: String,
    val ambientes: Int,
    val superficie: Double?,
    val latitud: Double?,
    val longitud: Double?,
    val disponible: Boolean,
    val precio: Double?,
    val estado: String,
    val uso: String,
    val fechaCreacion: String,
    val imagenPortadaUrl: String?,
    val imagenes: List<InmuebleImagen>
)
```

### InmuebleImagen.kt
```kotlin
data class InmuebleImagen(
    val id: Int,
    val nombreArchivo: String,
    val rutaCompleta: String,
    val esPortada: Boolean,
    val descripcion: String?
)
```

### CrearInmuebleRequest.kt
```kotlin
data class CrearInmuebleRequest(
    val direccion: String,
    val localidad: String?,
    val provincia: String?,
    val tipoId: Int,
    val ambientes: Int,
    val superficie: Double?,
    val latitud: Double?,
    val longitud: Double?,
    val precio: Double,
    val uso: Int, // 0=Residencial, 1=Comercial, 2=Industrial
    val imagenBase64: String?,
    val imagenNombre: String?
)
```

### ActualizarEstadoInmuebleRequest.kt
```kotlin
data class ActualizarEstadoInmuebleRequest(
    val disponible: Boolean
)
```

---

## 📄 Contrato Models

### Contrato.kt
```kotlin
data class Contrato(
    val id: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val precio: Double,
    val estado: String,
    val fechaCreacion: String,
    val motivoCancelacion: String?,
    val fechaFinalizacionReal: String?,
    val multaFinalizacion: Double?,
    val mesesAdeudados: Int?,
    val importeAdeudado: Double?,
    val inmueble: InmuebleContrato,
    val inquilino: InquilinoContrato,
    val pagos: List<Pago>
)
```

### InmuebleContrato.kt
```kotlin
data class InmuebleContrato(
    val id: Int,
    val direccion: String,
    val localidad: String?,
    val provincia: String?,
    val ambientes: Int,
    val imagenPortadaUrl: String?
)
```

### InquilinoContrato.kt
```kotlin
data class InquilinoContrato(
    val id: Int,
    val nombreCompleto: String,
    val dni: String,
    val telefono: String,
    val email: String
)
```

---

## 💰 Pago Model

### Pago.kt
```kotlin
data class Pago(
    val id: Int,
    val numero: Int,
    val fechaPago: String?,
    val contratoId: Int,
    val importe: Double,
    val intereses: Double,
    val multas: Double,
    val totalAPagar: Double,
    val fechaVencimiento: String,
    val estado: String,
    val metodoPago: String?,
    val observaciones: String?,
    val fechaCreacion: String
)
```

---

## 📦 Generic Response Models

### ApiResponse.kt
```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val errors: List<String>
)
```

### ApiResponseSimple.kt
```kotlin
data class ApiResponseSimple(
    val success: Boolean,
    val message: String,
    val errors: List<String>
)
```

---

## 🎨 Enums Útiles

### EstadoInmueble.kt
```kotlin
enum class EstadoInmueble(val valor: String) {
    ACTIVO("Activo"),
    INACTIVO("Inactivo");
    
    companion object {
        fun fromString(valor: String): EstadoInmueble? {
            return values().find { it.valor.equals(valor, ignoreCase = true) }
        }
    }
}
```

### UsoInmueble.kt
```kotlin
enum class UsoInmueble(val valor: Int, val nombre: String) {
    RESIDENCIAL(0, "Residencial"),
    COMERCIAL(1, "Comercial"),
    INDUSTRIAL(2, "Industrial");
    
    companion object {
        fun fromInt(valor: Int): UsoInmueble? {
            return values().find { it.valor == valor }
        }
    }
}
```

### EstadoContrato.kt
```kotlin
enum class EstadoContrato(val valor: String) {
    RESERVADO("Reservado"),
    ACTIVO("Activo"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");
    
    companion object {
        fun fromString(valor: String): EstadoContrato? {
            return values().find { it.valor.equals(valor, ignoreCase = true) }
        }
    }
}
```

### EstadoPago.kt
```kotlin
enum class EstadoPago(val valor: String) {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    VENCIDO("Vencido");
    
    companion object {
        fun fromString(valor: String): EstadoPago? {
            return values().find { it.valor.equals(valor, ignoreCase = true) }
        }
    }
}
```

---

## 💾 SharedPreferences Helper

### AuthManager.kt
```kotlin
class AuthManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString("token", null)
    }
    
    fun savePropietario(propietario: Propietario) {
        val gson = Gson()
        val json = gson.toJson(propietario)
        prefs.edit().putString("propietario", json).apply()
    }
    
    fun getPropietario(): Propietario? {
        val json = prefs.getString("propietario", null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, Propietario::class.java)
        } else null
    }
    
    fun clearAuth() {
        prefs.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
```

---

## 📝 Notas Importantes

1. **Fechas**: La API devuelve fechas en formato ISO 8601 (`"2025-01-15T10:30:00"`)
2. **Imágenes Base64**: Para subir imágenes, convertir a Base64 sin el prefijo `data:image/...`
3. **URLs de imágenes**: Concatenar con la base URL (`http://localhost:5000/uploads/...`)
4. **Token JWT**: Incluir en header `Authorization: Bearer {token}`
5. **Propietario ID**: NO se envía en los requests, se extrae del token en el servidor

---

📖 **Ver también**: `ANDROID_RETROFIT_SETUP.md` para configuración de Retrofit
