# Flujo: Perfil — Ver, Editar y Cambiar Contraseña

Este documento describe el circuito desde que el propietario entra a la sección "Perfil" hasta ver los datos, editar su perfil y cambiar la contraseña. Incluye las piezas que participan (Fragment, ViewModel, ApiClient), los LiveData implicados y casos de error.

## Archivos relevantes
- `app/src/main/java/.../ui/perfil/PerfilFragment.java`
- `app/src/main/java/.../ui/perfil/PerfilViewModel.java`
- `app/src/main/java/.../ui/perfil/CambiarPasswordDialog.java`
- `app/src/main/java/.../request/ApiClient.java`
- Modelos: `app/src/main/java/.../modelos/Propietario.java`, `ActualizarPerfilRequest.java`

## Resumen rápido
1. `PerfilFragment` crea/obtiene `PerfilViewModel` (ViewModelProvider).
2. `PerfilFragment` observa varios `LiveData` expuestos por `PerfilViewModel`:
   - `getMPropietario()` -> `LiveData<Propietario>`: datos del propietario.
   - `getMError()` -> `LiveData<String>`: mensajes de error o información.
   - `getMModoEdicion()` -> `LiveData<Boolean>`: si la UI debe entrar en modo edición.
   - `getMTextoBoton()` -> `LiveData<String>`: texto dinámico del botón editar/guardar.
   - `getMIconoBoton()` -> `LiveData<Integer>`: icono del botón.
   - `getMSolicitarFoco()` -> `LiveData<Boolean>`: para solicitar foco en un campo.
   - `getMFondoCampos()` -> `LiveData<Integer>`: recurso de fondo para campos editables.
3. Al abrir, `PerfilFragment` llama `mv.cargarPerfil()`.
4. `PerfilViewModel.cargarPerfil()` usa `ApiClient.getMyApiInterface(context).obtenerPerfil(token)` y en `onResponse` hace `mPropietario.postValue(propietario)` y guarda el propietario en `SharedPreferences` (`ApiClient.guardarPropietario(...)`).
5. Para editar: `binding.btnEditar` llama `mv.onBotonEditarClick(nombre, apellido, email, telefono)`. Si no está en modo edición, el ViewModel activa el modo edición (postea `mModoEdicion=true`, cambia `mTextoBoton` a "Guardar", `mIconoBoton` a guardar y `mFondoCampos` a fondo editable).
6. Si ya está en modo edición, `onBotonEditarClick` llama `guardarCambios(...)`. Este método valida campos, construye `ActualizarPerfilRequest` y llama `api.actualizarPerfil(token, request)`. En `onResponse` exitoso, hace `mPropietario.postValue(propietarioActualizado)`, cambia `mModoEdicion` a `false`, resetea `mTextoBoton` y `mIconoBoton`, y guarda nuevo propietario en `SharedPreferences`.
7. Para cambiar contraseña: `PerfilFragment` abre `CambiarPasswordDialog`. El diálogo valida inputs y llama `ApiClient.getMyApiInterface(requireContext()).cambiarPassword(token, currentPassword, newPassword)`. Según la respuesta muestra Toasts apropiados y cierra el diálogo en caso de éxito.

## Flujo detallado (paso a paso)
### Ver perfil
- Usuario navega a la sección Perfil.
- `PerfilFragment.onCreateView()`:
  - `mv = new ViewModelProvider(this).get(PerfilViewModel.class);`
  - Observadores se registran (mPropietario, mError, etc.).
  - Llama `mv.cargarPerfil()`.
- `PerfilViewModel.cargarPerfil()`:
  - Lee token: `String token = ApiClient.getToken(context)`.
  - Llama `api.obtenerPerfil(token)` (Retrofit enqueue).
  - En `onResponse` exitoso: `mPropietario.postValue(response.body())` y `ApiClient.guardarPropietario(context, propietario)`.
  - En error: `mError.postValue("Error al cargar el perfil: " + response.code())`.
- `PerfilFragment` recibe `onChanged(Propietario)` y rellena vistas (nombre, email, dni, teléfono).

### Editar perfil
- Usuario pulsa el botón Editar en `PerfilFragment`.
- `PerfilFragment` lee los campos y llama `mv.onBotonEditarClick(nombre, apellido, email, telefono)`.
- `PerfilViewModel.onBotonEditarClick`:
  - Si `mModoEdicion` es `false`: activa modo edición con `mModoEdicion.postValue(true)` y cambia texto/icono/fondo.
  - Si `mModoEdicion` es `true`: llama `guardarCambios(...)`.
- `PerfilViewModel.guardarCambios(...)`:
  - Valida campos (nombre y apellido obligatorios).
  - Lee token y propietario actual (`mPropietario.getValue()`), crea `ActualizarPerfilRequest` y llama `api.actualizarPerfil(token, request)`.
  - En `onResponse` exitoso: actualiza `mPropietario.postValue(propietarioActualizado)`, guarda en SharedPreferences, desactiva modo edición (`mModoEdicion.postValue(false)`), y muestra mensaje `mError.postValue("Perfil actualizado correctamente")`.
  - En fallo: `mError.postValue("Error al actualizar el perfil: " + response.code())`.

### Cambiar contraseña
- En `PerfilFragment`, botón `btnCambiarPassword` abre `CambiarPasswordDialog`.
- `CambiarPasswordDialog` valida:
  - Campos no vacíos.
  - Nueva contraseña >= 6 caracteres.
  - Nueva contraseña y confirmación coinciden.
- Si válido, obtiene token (`ApiClient.getToken(requireContext())`) y llama `api.cambiarPassword(token, passwordActual, passwordNueva)` (PUT form-url-encoded).
- Manejo de resultado:
  - `200 OK` -> Toast "Contraseña cambiada exitosamente" y `dismiss()`.
  - `400` -> Toast "Contraseña actual incorrecta".
  - `401` -> Toast "Sesión expirada".
  - `500` -> Toast "Error en el servidor".
  - En `onFailure` -> Toast de conexión.

## LiveData / MutableLiveData implicados
- `PerfilViewModel`:
  - `MutableLiveData<Propietario> mPropietario` -> expuesto como `getMPropietario()`.
  - `MutableLiveData<String> mError` -> `getMError()` (usado también para mensajes informativos).
  - `MutableLiveData<Boolean> mModoEdicion` -> controla si campos son editables.
  - `MutableLiveData<String> mTextoBoton` -> texto del botón editar/guardar.
  - `MutableLiveData<Integer> mIconoBoton` -> icono del botón.
  - `MutableLiveData<Boolean> mSolicitarFoco` -> para forzar foco en un campo.
  - `MutableLiveData<Integer> mFondoCampos` -> recurso de fondo cuando está en edición.
- `CambiarPasswordDialog` no usa LiveData — usa callbacks Retrofit directamente y muestra Toasts.

## Casos de esquina / errores ya contemplados
- Sin token: `mError.postValue("No hay sesión activa")` o Toast en diálogo de contraseña.
- Validaciones en `guardarCambios`: nombre y apellido obligatorios.
- Manejo de errores HTTP y errores de conexión con mensajes.

## Recomendaciones / mejoras pequeñas
- Usar un `SingleLiveEvent` o `Event` wrapper para eventos únicos (toasts, navegación) en lugar de `mError`, para evitar reprocesamiento en rotaciones.
- Agregar `LiveData<Boolean> mCargando` en `PerfilViewModel` para mostrar spinner durante llamadas largas (actualmente no está presente para guardar perfil).
- Separar responsabilidades: introducir una capa Repository para aislar `ApiClient` y facilitar tests unitarios del ViewModel.
- Agregar unit tests al `PerfilViewModel` (mockear `ApiClient` / Retrofit) y UI tests para el diálogo de cambio de contraseña.

---

> Lugar de verificación: revisado `PerfilFragment.java`, `PerfilViewModel.java` y `CambiarPasswordDialog.java`.

