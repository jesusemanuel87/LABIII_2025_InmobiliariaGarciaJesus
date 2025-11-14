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

## Flujo completo paso a paso

### **1. Ver Perfil**

```
Usuario toca "Perfil" en el NavigationDrawer
    ↓
MainActivity navega a PerfilFragment
    ↓
PerfilFragment.onCreateView()
    - Crea PerfilViewModel
    - Configura observers (mPropietario, mError, mModoEdicion, mTextoBoton, mIconoBoton, etc.)
    - Llama mv.cargarPerfil()
    ↓
PerfilViewModel.cargarPerfil()
    - Lee token: ApiClient.getToken(context)
    - Llama api.obtenerPerfil(token)
    ↓
Retrofit ejecuta GET /api/PropietariosApi/perfil (o similar)
    ↓
Backend responde con Propietario (JSON)
    ↓
PerfilViewModel.onResponse()
    - mPropietario.postValue(propietario)
    - ApiClient.guardarPropietario(context, propietario) en SharedPreferences
    - Si error: mError.postValue("Error al cargar el perfil: " + código)
    ↓
PerfilFragment (observer de mPropietario)
    - Rellena vistas con nombre, apellido, email, teléfono, etc.
```

### **2. Editar Perfil**

```
Usuario toca botón "Editar" en PerfilFragment
    ↓
PerfilFragment.onClickEditar()
    - Lee textos actuales de los campos
    - Llama mv.onBotonEditarClick(nombre, apellido, email, telefono)
    ↓
PerfilViewModel.onBotonEditarClick(...)
    - Si mModoEdicion es false:
        - Activa modo edición:
            mModoEdicion.postValue(true)
            mTextoBoton.postValue("Guardar")
            mIconoBoton.postValue(icono_guardar)
            mFondoCampos.postValue(fondo_editable)
        - Opcional: mSolicitarFoco.postValue(true) para enfocar primer campo
    - Si mModoEdicion es true:
        - Llama guardarCambios(nombre, apellido, email, telefono)
    ↓
PerfilViewModel.guardarCambios(...)
    - Valida campos (nombre/apellido obligatorios, email válido, etc.)
    - Si falla validación: mError.postValue("Mensaje de validación") y retorna
    - Lee token + propietario actual
    - Crea ActualizarPerfilRequest con los nuevos datos
    - Llama api.actualizarPerfil(token, request)
    ↓
Retrofit ejecuta PUT /api/PropietariosApi/perfil (o similar)
    ↓
Backend responde con Propietario actualizado (JSON) o error
    ↓
PerfilViewModel.onResponse()
    - Si éxito:
        mPropietario.postValue(propietarioActualizado)
        ApiClient.guardarPropietario(context, propietarioActualizado)
        mModoEdicion.postValue(false)
        mTextoBoton.postValue("Editar")
        mIconoBoton.postValue(icono_editar)
        mFondoCampos.postValue(fondo_no_editable)
        mError.postValue("Perfil actualizado correctamente")
    - Si error:
        mError.postValue("Error al actualizar el perfil: " + código)
    ↓
PerfilFragment (observers)
    - Actualiza UI según mModoEdicion, mTextoBoton, mIconoBoton, mFondoCampos
    - Muestra mensajes de mError como Toast o en TextView
```

### **3. Cambiar Contraseña**

```
Usuario toca botón "Cambiar contraseña" en PerfilFragment
    ↓
PerfilFragment abre CambiarPasswordDialog
    ↓
CambiarPasswordDialog
    - Usuario ingresa contraseña actual, nueva y confirmación
    - Al tocar "Aceptar", valida:
        - Campos no vacíos
        - Nueva contraseña >= 6 caracteres
        - Nueva contraseña y confirmación coinciden
    - Si validación falla: muestra Toast y no llama a la API
    - Si validación pasa:
        - Lee token: ApiClient.getToken(requireContext())
        - Llama api.cambiarPassword(token, passwordActual, passwordNueva)
    ↓
Retrofit ejecuta PUT /api/PropietariosApi/cambiar-password (form-url-encoded)
    ↓
Backend responde con 200/400/401/500, etc.
    ↓
CambiarPasswordDialog.onResponse()
    - 200 → Toast "Contraseña cambiada exitosamente" y dismiss()
    - 400 → Toast "Contraseña actual incorrecta"
    - 401 → Toast "Sesión expirada"
    - 500 → Toast "Error en el servidor"
    - onFailure → Toast "Error de conexión"
```

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

