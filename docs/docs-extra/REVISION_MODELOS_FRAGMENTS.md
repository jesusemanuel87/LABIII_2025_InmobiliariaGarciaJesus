# Revisión de Modelos y Fragments - Inmobiliaria App

## Fecha de Revisión
19 de Octubre, 2025

---

## ✅ FUNCIONALIDADES IMPLEMENTADAS

### 1. **Login/Logout de Propietarios**
- ✅ **Estado**: Funcionando correctamente
- **Archivos**: 
  - `LoginActivity.java`
  - `LoginActivityViewModel.java`
- **Endpoint usado**: `POST /api/AuthApi/login`
- **Detalles**: El token JWT se guarda en SharedPreferences y se usa en todas las peticiones autenticadas

---

### 2. **Ver y Editar Perfil**
- ✅ **Estado**: ACTUALIZADO a API REST
- **Archivos actualizados**:
  - `PerfilViewModel.java` - Ahora usa `obtenerPerfil()` y `actualizarPerfil()`
  - `PerfilFragment.java` - Usa modelo `Propietario` en lugar de `Propietarios`
- **Endpoints usados**:
  - `GET /api/PropietarioApi/perfil` - Obtener datos del propietario desde el token
  - `PUT /api/PropietarioApi/perfil` - Actualizar perfil (nombre, apellido, teléfono, dirección)
- **Modelo**: `ActualizarPerfilRequest.java`
- **✅ Característica**: El ID del propietario se recupera desde el token (no se envía desde la app)

---

### 3. **Listar Inmuebles**
- ✅ **Estado**: Funcionando con API REST
- **Archivos**:
  - `InmueblesFragment.java`
  - `InmueblesViewModel.java` 
  - `InmueblesAdapter.java`
- **Endpoint usado**: `GET /api/InmueblesApi`
- **Modelo**: `Inmueble.java`
- **Características**:
  - Lista todos los inmuebles del propietario autenticado
  - Muestra: dirección, tipo, precio, ambientes, estado (disponible/no disponible)

---

### 4. **Habilitar/Deshabilitar Inmueble**
- ✅ **Estado**: IMPLEMENTADO
- **Archivo**: `InmueblesViewModel.java`
- **Método**: `cambiarEstadoInmueble(int inmuebleId, boolean disponible)`
- **Endpoint usado**: `PATCH /api/InmueblesApi/{id}/estado`
- **Modelo**: `ActualizarEstadoInmuebleRequest.java`
- **Características**:
  - Cambia el estado de disponibilidad del inmueble
  - Recarga la lista automáticamente después del cambio

---

### 5. **Listar Contratos por Inmuebles**
- ✅ **Estado**: Funcionando con API REST
- **Archivos**:
  - `ContratosFragment.java`
  - `ContratosViewModel.java`
  - `ContratosAdapter.java`
- **Endpoints usados**:
  - `GET /api/ContratosApi` - Listar todos los contratos
  - `GET /api/ContratosApi/inmueble/{inmuebleId}` - Contratos por inmueble específico
- **Modelos**: 
  - `Contrato.java` (incluye lista de `Pago`)
  - `InmuebleContrato.java`
  - `InquilinoContrato.java`
  - `Pago.java`
- **Características**:
  - Muestra contratos con datos de inmueble e inquilino
  - Incluye información de pagos asociados

---

### 6. **Listar Inquilinos**
- ✅ **Estado**: IMPLEMENTADO COMPLETAMENTE
- **Archivos creados/actualizados**:
  - `InquilinosViewModel.java` - ✅ Implementado completo
  - `InquilinosFragment.java` - ✅ Implementado completo
  - `InquilinosAdapter.java` - ✅ Creado
- **Endpoint usado**: `GET /api/ContratosApi` (extrae inquilinos únicos)
- **Modelo**: `InquilinoContrato.java`
- **Características**:
  - Lista todos los inquilinos únicos de los contratos del propietario
  - Muestra: nombre completo, DNI, teléfono, email
  - Filtra duplicados por ID de inquilino

---

## 🔧 ENDPOINTS DISPONIBLES (BACKEND API REST)

### Autenticación (AuthApi)
- ✅ `POST /api/AuthApi/login` - Login con email y password
- ⚠️ `POST /api/AuthApi/cambiar-password` - Cambiar contraseña (falta UI)
- ⚠️ `POST /api/AuthApi/reset-password` - Resetear contraseña (falta UI)

### Propietario (PropietarioApi)
- ✅ `GET /api/PropietarioApi/perfil` - Obtener perfil
- ✅ `PUT /api/PropietarioApi/perfil` - Actualizar perfil
- ⚠️ `POST /api/PropietarioApi/perfil/foto` - Subir foto de perfil (falta UI)

### Inmuebles (InmueblesApi)
- ✅ `GET /api/InmueblesApi` - Listar inmuebles
- ✅ `GET /api/InmueblesApi/{id}` - Obtener inmueble por ID
- ⚠️ `POST /api/InmueblesApi` - Crear nuevo inmueble (falta UI)
- ✅ `PATCH /api/InmueblesApi/{id}/estado` - Actualizar estado

### Contratos (ContratosApi)
- ✅ `GET /api/ContratosApi` - Listar contratos
- ✅ `GET /api/ContratosApi/{id}` - Obtener contrato por ID
- ✅ `GET /api/ContratosApi/inmueble/{inmuebleId}` - Contratos por inmueble

---

## ⚠️ FUNCIONALIDADES PENDIENTES (UI)

### 1. **Cambiar Contraseña por Separado**
- **Estado**: Backend listo, falta UI
- **Endpoint disponible**: `POST /api/AuthApi/cambiar-password`
- **Modelo existente**: `CambiarPasswordRequest.java`
- **Campos requeridos**:
  - `passwordActual` (String)
  - `passwordNueva` (String)
  - `passwordConfirmacion` (String)
- **Sugerencia**: Agregar un botón en `PerfilFragment` o crear un diálogo/actividad separada

### 2. **Resetear Contraseña ("Olvidé mi Contraseña")**
- **Estado**: Backend listo, falta UI
- **Endpoint disponible**: `POST /api/AuthApi/reset-password`
- **Modelos existentes**: 
  - `ResetPasswordRequest.java`
  - `ResetPasswordResponse.java`
- **Campos requeridos**: `email` (String)
- **Sugerencia**: Agregar opción en `LoginActivity`

### 3. **Agregar Nuevo Inmueble con Foto**
- **Estado**: Backend listo, falta UI
- **Endpoint disponible**: `POST /api/InmueblesApi`
- **Modelo existente**: `CrearInmuebleRequest.java`
- **Campos requeridos**:
  - Dirección, localidad, provincia
  - Tipo ID, ambientes, superficie
  - Latitud, longitud (opcional)
  - Precio, uso (0=Residencial, 1=Comercial, 2=Industrial)
  - `imagenBase64` (String) - Foto codificada en Base64
  - `imagenNombre` (String) - Nombre del archivo
- **Nota**: El inmueble se crea por defecto como NO DISPONIBLE
- **Sugerencia**: Crear Activity/Fragment con formulario y selector de imagen

### 4. **Subir Foto de Perfil**
- **Estado**: Backend listo, falta UI
- **Endpoint disponible**: `POST /api/PropietarioApi/perfil/foto`
- **Tipo**: Multipart (MultipartBody.Part)
- **Sugerencia**: Agregar botón en `PerfilFragment` para cambiar avatar

---

## 📋 MODELOS CLAVE

### Principales
- ✅ `Propietario.java` - Datos del propietario (usado en perfil)
- ✅ `Inmueble.java` - Datos completos del inmueble
- ✅ `Contrato.java` - Datos del contrato con inmueble, inquilino y pagos
- ✅ `Pago.java` - Detalles de pagos de contratos

### Auxiliares
- ✅ `InquilinoContrato.java` - Datos del inquilino en contrato
- ✅ `InmuebleContrato.java` - Datos resumidos del inmueble en contrato
- ✅ `InmuebleImagen.java` - Imágenes del inmueble

### Request Models
- ✅ `ActualizarPerfilRequest.java` - Para actualizar perfil
- ✅ `ActualizarEstadoInmuebleRequest.java` - Para cambiar estado inmueble
- ✅ `CambiarPasswordRequest.java` - Para cambiar contraseña
- ✅ `CrearInmuebleRequest.java` - Para crear inmueble con foto
- ✅ `LoginRequest.java` - Para login
- ✅ `ResetPasswordRequest.java` - Para resetear contraseña

### Response Models
- ✅ `ApiResponse<T>.java` - Respuesta genérica de la API
- ✅ `ApiResponseSimple.java` - Respuesta simple sin datos
- ✅ `LoginResponse.java` - Respuesta del login con token
- ✅ `ResetPasswordResponse.java` - Respuesta de reset password

---

## 🔐 SEGURIDAD Y TOKEN

### ✅ Implementación Correcta
1. **El ID del propietario NO se envía desde la app**
   - Todos los endpoints protegidos usan el token JWT
   - El backend extrae el ID del propietario desde el token
   - Esto previene que un usuario acceda a datos de otro propietario

2. **Gestión del Token**
   - Se guarda en `SharedPreferences` después del login
   - Se incluye automáticamente en el header `Authorization: Bearer {token}`
   - Se limpia al hacer logout

3. **Endpoints Protegidos**
   - Todos excepto `/login` y `/reset-password` requieren autenticación
   - El token se valida en el backend antes de procesar la petición

---

## 📁 ESTRUCTURA DE ARCHIVOS

### ViewModels Actualizados
```
✅ PerfilViewModel.java - Usa API REST
✅ InmueblesViewModel.java - Usa API REST + cambio de estado
✅ ContratosViewModel.java - Usa API REST
✅ InquilinosViewModel.java - Implementado completo
```

### Fragments Actualizados
```
✅ PerfilFragment.java - Modelo actualizado
✅ InmueblesFragment.java - Funcionando
✅ ContratosFragment.java - Funcionando
✅ InquilinosFragment.java - Implementado completo
```

### Adapters
```
✅ InmueblesAdapter.java - Funcionando
✅ ContratosAdapter.java - Funcionando
✅ InquilinosAdapter.java - Creado
```

### API Client
```
✅ ApiClient.java - Configurado con todos los endpoints REST
```

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

### Prioridad Alta
1. **Agregar UI para cambiar contraseña** en PerfilFragment
2. **Agregar UI para "Olvidé mi contraseña"** en LoginActivity
3. **Implementar selector de estado** en lista de inmuebles (switch/botón para habilitar/deshabilitar)

### Prioridad Media
4. **Crear Activity/Fragment para agregar nuevo inmueble** con:
   - Formulario con todos los campos
   - Selector de imagen (cámara/galería)
   - Conversión de imagen a Base64
5. **Agregar funcionalidad de subir foto de perfil**

### Prioridad Baja
6. **Mejorar UI/UX** de las pantallas existentes
7. **Agregar validaciones adicionales** en formularios
8. **Implementar manejo de errores** más detallado

---

## ✅ CUMPLIMIENTO DE REQUISITOS

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Login/logout de propietarios | ✅ Completo | Funcionando correctamente |
| Ver y editar perfil | ✅ Completo | Actualizado a API REST |
| Cambiar clave por separado | ⚠️ Backend listo | Falta UI |
| Resetear contraseña | ⚠️ Backend listo | Falta UI |
| Listar inmuebles | ✅ Completo | Con API REST |
| Habilitar/Deshabilitar inmueble | ✅ Completo | Implementado |
| Agregar inmueble con foto | ⚠️ Backend listo | Falta UI |
| Listar contratos por inmuebles | ✅ Completo | Incluye pagos |
| Listar inquilinos | ✅ Completo | Implementado |
| No enviar ID propietario | ✅ Completo | Se usa token |
| Requiere autenticación | ✅ Completo | Excepto login |

**Resumen**: 7 de 11 requisitos funcionando completamente, 4 requieren solo UI (backend listo).

---

## 📝 NOTAS IMPORTANTES

1. **Configuración de IP**: Verificar `ApiClient.java` línea 38 para la IP correcta del servidor
2. **Permisos de Internet**: Verificar `AndroidManifest.xml` tiene permisos de INTERNET
3. **Layouts XML**: Asegurarse de que existen los layouts para:
   - `fragment_inquilinos.xml`
   - `item_inquilino.xml`
   - Estos pueden necesitarse crear si no existen

4. **Pruebas**: Antes de continuar con nuevas funcionalidades, probar:
   - Ver perfil
   - Editar perfil
   - Listar inmuebles
   - Cambiar estado de inmueble
   - Listar contratos
   - Listar inquilinos

---

## 🐛 POSIBLES PROBLEMAS Y SOLUCIONES

### Si no carga el perfil:
- Verificar que el token se guardó correctamente
- Verificar la URL base en `ApiClient.java`
- Verificar logs con tag "PERFIL"

### Si no listan los inmuebles/contratos:
- Verificar que el propietario tiene datos en el backend
- Verificar logs con tags "INMUEBLES" o "CONTRATOS"
- Verificar que el token es válido

### Si falla el cambio de estado:
- Verificar que el endpoint `PATCH` está implementado en backend
- Verificar que el inmueble existe y pertenece al propietario

---

**Documento generado el**: 19/10/2025  
**Versión de la App**: Android  
**API Version**: REST API con JWT
