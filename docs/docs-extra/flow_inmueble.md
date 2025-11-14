# Flujo: Inmueble — Crear, Editar y Activar/Inactivar

Este documento describe el circuito para crear un nuevo inmueble, editar (vista/nota sobre editar) y activar/inactivar (cambiar estado) un inmueble. Incluye componentes involucrados, LiveData, endpoints utilizados y recomendaciones.

## Archivos relevantes
- `app/src/main/java/.../ui/inmuebles/CargarInmuebleFragment.java`
- `app/src/main/java/.../ui/inmuebles/CargarInmuebleViewModel.java`
- `app/src/main/java/.../ui/inmuebles/InmueblesFragment.java`
- `app/src/main/java/.../ui/inmuebles/InmueblesViewModel.java`
- `app/src/main/java/.../ui/inmuebles/DetalleInmuebleFragment.java`
- `app/src/main/java/.../ui/inmuebles/DetalleInmuebleViewModel.java`
- `app/src/main/java/.../request/ApiClient.java`
- Modelos: `CrearInmuebleRequest.java`, `Inmueble.java`, `ActualizarEstadoInmuebleRequest.java`

> Nota: en este repo la edición completa de un inmueble parece manejarse por una combinación de `CargarInmuebleFragment` (creación) y `DetalleInmuebleFragment` (visualización y cambio de estado). Si necesitás un flujo separado de "Editar Inmueble" se podría reutilizar `CargarInmuebleFragment` con pre-llenado.

## Resumen rápido
- Crear inmueble:
  - `CargarInmuebleFragment` recopila campos (dirección, provincia, localidad, tipo, ambientes, superficie, precio, lat/long, imagen) y llama `mv.crearInmueble(...)`.
  - `CargarInmuebleViewModel.crearInmueble(...)` valida campos, crea `CrearInmuebleRequest` y llama `api.crearInmueble(token, request)`.
  - En respuesta exitosa `mInmuebleCreado.postValue(true)` y el fragment navega hacia atrás (lista de inmuebles).
- Editar inmueble:
  - No hay un fragment explícitamente llamado `EditarInmuebleFragment` en el repo.
  - Estrategia recomendada: reutilizar `CargarInmuebleFragment` pasando `inmuebleId` y precargando los datos con `api.obtenerInmueble(token, id)` para llenar los campos. Luego usar `api.crearInmueble` para crear y `api.actualizarInmueble` (si existiera) o endpoint `PUT/PATCH` para editar.
- Activar / Inactivar (cambiar estado):
  - `DetalleInmuebleFragment` muestra un switch que delega a `DetalleInmuebleViewModel.cambiarEstadoInmueble(inmuebleId, estado, false)`.
  - `DetalleInmuebleViewModel` llama `api.actualizarEstadoInmueble(token, inmuebleId, request)` y en respuesta actualiza `mInmueble` y los LiveData preparados (mEstado, mDisponibilidad, mSwitchChecked, etc.).

## Flujo detallado: Crear inmueble
1. Usuario abre menú y navega a "Agregar Inmueble" (`InmueblesFragment` -> FAB -> `Navigation.findNavController(...).navigate(R.id.cargarInmuebleFragment)`).
2. `CargarInmuebleFragment.onCreateView()`:
   - `mv = new ViewModelProvider(this).get(CargarInmuebleViewModel.class);`
   - Configura spinners (uso/tipo/provincias/localidades) y listeners.
   - Observa `getMMensaje()`, `getMCargando()` y `getMInmuebleCreado()`.
   - Llama `mv.cargarProvincias()` y `mv.cargarTiposInmueble()` para poblar los spinners.
3. Usuario completa formulario, selecciona imagen (se procesa en `onActivityResult` usando `mv.procesarImagenDesdeUri(...)` y `mv.bitmapABase64(...)`) y pulsa guardar.
4. `CargarInmuebleFragment.guardarInmueble()` compila valores y llama `mv.crearInmueble(...)`.
5. `CargarInmuebleViewModel.crearInmueble(...)`:
   - Valida campos obligatorios y convierte tipos (ambientes, superficie, precio, coordenadas).
   - Si hay imagen Base64, la incluye en `CrearInmuebleRequest` (campos `imagenBase64`, `imagenNombre`).
   - `mCargando.postValue(true)` y lee token: `ApiClient.getToken(context)`.
   - Llama `api.crearInmueble(token, request)` (Retrofit enqueue).
   - En `onResponse` exitoso: `mMensaje.postValue("Inmueble creado exitosamente")` y `mInmuebleCreado.postValue(true)`.
   - En fallo o `onFailure`: `mMensaje.postValue(...)` con el error.
6. `CargarInmuebleFragment` observa `getMInmuebleCreado()` y, si `true`, realiza `Navigation.findNavController(root).navigateUp()` para volver al listado.

## Flujo detallado: Editar inmueble (recomendación)
- Opciones para implementar edición:
  - Reutilizar `CargarInmuebleFragment` y pasar `inmuebleId` como argumento. En `onCreateView` si existe `inmuebleId`, llamar `api.obtenerInmueble(token, inmuebleId)` para precargar campos y cambiar la acción del botón para `actualizarInmueble(...)`.
  - Si el backend tiene endpoint `PUT api/InmueblesApi/{id}` (no listado explícito en `ApiClient`, habría que agregarlo), usarlo para enviar `CrearInmuebleRequest` o `ActualizarInmuebleRequest` según contrato.
- Recomendación de UI/UX: mostrar spinner/loading durante la carga y deshabilitar el botón guardar mientras se ejecuta la llamada.

## Flujo detallado: Activar / Inactivar inmueble (cambiar estado)
1. `InmueblesFragment` muestra lista; click en item navega a `DetalleInmuebleFragment` pasando `inmuebleId`.
2. `DetalleInmuebleFragment` crea `DetalleInmuebleViewModel` y llama `mv.cargarInmueble(inmuebleId)`.
3. `DetalleInmuebleViewModel.cargarInmueble` -> `api.obtenerInmueble(token, id)` -> en `onResponse` `mInmueble.postValue(inmueble)` y `prepararDatos(inmueble)` (actualiza `mSwitchChecked`, `mSwitchEnabled`, `mEstado`, `mDisponibilidad`, etc.).
4. UI muestra un `SwitchCompat` que está ligado a `mv.getMSwitchChecked()` y escucha cambios de usuario.
5. Si usuario activa/desactiva el switch el listener en la vista llama `mv.cambiarEstadoInmueble(inmuebleId, estadoTexto, false)`.
6. `DetalleInmuebleViewModel.cambiarEstadoInmueble(...)`:
   - Verifica token; construye `ActualizarEstadoInmuebleRequest` y llama `api.actualizarEstadoInmueble(token, inmuebleId, request)`.
   - En `onResponse` exitoso actualiza `mInmueble.postValue(inmuebleActualizado)` y vuelve a `prepararDatos(inmuebleActualizado)` para refrescar la UI.
   - En fallo, postea `mError` y vuelve a cargar el inmueble para restaurar el estado previo.

## LiveData / MutableLiveData implicados
- `CargarInmuebleViewModel`:
  - `MutableLiveData<String> mMensaje` -> feedback para el usuario
  - `MutableLiveData<Boolean> mCargando` -> mostrar spinner
  - `MutableLiveData<Boolean> mInmuebleCreado` -> flag para navegación de regreso
  - `MutableLiveData<List<Provincia>> mProvincias`, `mLocalidades`, `mTiposInmueble` -> poblar spinners
- `DetalleInmuebleViewModel` (relevantes):
  - `MutableLiveData<Inmueble> mInmueble`
  - `MutableLiveData<Boolean> mActualizando` -> estado de la llamada de actualización
  - `MutableLiveData<Boolean> mSwitchChecked`, `mSwitchEnabled` -> controlar el switch
  - Otros LiveData para mostrar datos preparados: `mDireccion`, `mLocalidad`, `mTipo`, `mAmbientes`, `mSuperficie`, `mPrecio`, `mDisponibilidad`, `mEstado`, `mImagenes`, etc.

## Endpoints usados (en `ApiClient.MyApiInterface`)
- `@POST("api/InmueblesApi") Call<Inmueble> crearInmueble(@Header("Authorization") String token, @Body CrearInmuebleRequest request)`
- `@GET("api/InmueblesApi/{id}") Call<Inmueble> obtenerInmueble(@Header("Authorization") String token, @Path("id") int inmuebleId)`
- `@PATCH("api/InmueblesApi/{id}/estado") Call<Inmueble> actualizarEstadoInmueble(@Header("Authorization") String token, @Path("id") int inmuebleId, @Body ActualizarEstadoInmuebleRequest request)`

## Casos de esquina y validaciones ya implementadas
- Validaciones completas en `CargarInmuebleViewModel` (dirección obligatoria, ambientes > 0, superficie > 0, precio válido, lat/long en rango).
- Si no hay token, se notifica error y se bloquea la operación.
- Si la creación falla en el servidor, `mMensaje` informa el código de error.
- En `DetalleInmuebleViewModel`, si la actualización falla, se recarga el inmueble para restaurar el estado anterior.

## Flujo completo paso a paso

### **1. Listar Inmuebles**

```
Usuario toca "Inmuebles" en el NavigationDrawer
    ↓
MainActivity navega a InmueblesFragment
    ↓
InmueblesFragment.onCreateView()
    - Crea InmueblesViewModel
    - Configura RecyclerView con InmueblesAdapter
    - Configura observers (mInmuebles, mError, mCargando, etc.)
    - Llama mv.cargarInmuebles()
    ↓
InmueblesViewModel.cargarInmuebles()
    - mCargando.postValue(true) → Fragment muestra ProgressBar
    - Lee token: ApiClient.getToken(context)
    - Llama api.listarInmuebles(token)
    ↓
Retrofit ejecuta GET /api/InmueblesApi
    ↓
Backend responde con List<Inmueble> (JSON)
    ↓
InmueblesViewModel.onResponse()
    - mCargando.postValue(false) → Fragment oculta ProgressBar
    - mInmuebles.postValue(inmuebles)
    ↓
InmueblesFragment (observer de mInmuebles)
    - adapter.setInmuebles(items)
    - Muestra/oculta RecyclerView y mensaje según si hay items
    ↓
InmueblesAdapter renderiza cada Inmueble
    - Muestra imagen (normalizando URL localhost → DevTunnel)
    - Muestra dirección, tipo, precio, estado / disponibilidad
    - Muestra switch de estado (si aplica)
```

### **2. Ver Detalle de Inmueble**

```
Usuario toca un item en la lista de Inmuebles
    ↓
InmueblesAdapter.OnInmuebleClickListener
    ↓
InmueblesFragment navega a DetalleInmuebleFragment
    - Pasa inmuebleId (o el Inmueble serializado) en el Bundle
    ↓
DetalleInmuebleFragment.onCreateView()
    - Crea DetalleInmuebleViewModel
    - Configura observers para múltiples LiveData (mDireccion, mLocalidad, mTipo, mUso, mPrecio, mEstado, mDisponibilidad, mImagenes, mLatitud, mLongitud, mTituloMapa, etc.)
    - Lee argumentos del Bundle (id o Inmueble)
    - Llama mv.cargarInmueble(inmuebleId)
    ↓
DetalleInmuebleViewModel.cargarInmueble(inmuebleId)
    - Lee token: ApiClient.getToken(context)
    - Llama api.obtenerInmueble(token, inmuebleId)
    ↓
Retrofit ejecuta GET /api/InmueblesApi/{id}
    ↓
Backend responde con Inmueble (JSON)
    ↓
DetalleInmuebleViewModel.onResponse()
    - mInmueble.postValue(inmueble)
    - prepararDatos(inmueble)
    ↓
DetalleInmuebleViewModel.prepararDatos(inmueble)
    - Prepara textos: mDireccion, mLocalidad, mTipo, mUso, mPrecio
    - Prepara disponibilidad/estado y colores (mDisponibilidad, mDisponibilidadColor, mEstado, mSwitchChecked, mSwitchEnabled)
    - Prepara imágenes para el carrusel: mImagenes, mCantidadIndicadores, mIndicadorActivo
    - Prepara datos para el mapa: mBotonMapaEnabled, mLatitud, mLongitud, mTituloMapa
    ↓
DetalleInmuebleFragment (observers)
    - Cada observer actualiza su TextView / ImageView / Switch / botón
    - El Fragment NO formatea datos: solo muestra lo que viene del ViewModel
```

### **3. Crear nuevo Inmueble**

```
Usuario está en InmueblesFragment y toca el FAB "Agregar Inmueble"
    ↓
Navigation.findNavController().navigate(R.id.cargarInmuebleFragment)
    ↓
CargarInmuebleFragment.onCreateView()
    - Crea CargarInmuebleViewModel
    - Configura spinners (uso, tipo, provincia, localidad)
    - Configura listeners (botón guardar, botón obtener ubicación, botón seleccionar foto)
    - Observa mMensaje, mCargando, mInmuebleCreado
    - Llama mv.cargarProvincias() y mv.cargarTiposInmueble()
    ↓
Usuario completa formulario y selecciona imagen
    - Imagen se procesa vía mv.procesarImagenDesdeUri(...) → Base64
    ↓
Usuario toca "Guardar Inmueble"
    ↓
CargarInmuebleFragment.guardarInmueble()
    - Lee valores de los campos
    - Llama mv.crearInmueble(... parámetros ...)
    ↓
CargarInmuebleViewModel.crearInmueble(...)
    - Valida campos obligatorios (dirección, ambientes, precio, etc.)
    - Convierte strings a números (ambientes, superficie, precio, lat/long)
    - Crea objeto Inmueble y setea todos los campos (incluyendo uso como int, imagenBase64, imagenNombre)
    - mCargando.postValue(true)
    - Lee token: ApiClient.getToken(context)
    - Llama api.crearInmueble(token, inmueble)
    ↓
Retrofit ejecuta POST /api/InmueblesApi
    ↓
Backend responde con Inmueble creado (JSON) o error
    ↓
CargarInmuebleViewModel.onResponse()
    - mCargando.postValue(false)
    - Si éxito: mMensaje.postValue("Inmueble creado exitosamente")
              mInmuebleCreado.postValue(true)
    - Si error: mMensaje.postValue("Error al crear el inmueble: " + código)
    ↓
CargarInmuebleFragment (observer de mInmuebleCreado)
    - Si true → Navigation.navigateUp() (vuelve a la lista de Inmuebles)
```

### **4. Cambiar estado (Activo/Inactivo) de un Inmueble**

```
Usuario abre DetalleInmuebleFragment
    ↓
DetalleInmuebleViewModel.cargarInmueble(inmuebleId) → (flujo de detalle, ver arriba)
    ↓
UI muestra Switch de estado ligado a mv.getMSwitchChecked()
    - El ViewModel decide si el switch está habilitado (mSwitchEnabled)
    ↓
Usuario cambia el Switch
    ↓
Listener en DetalleInmuebleFragment
    - Llama mv.cambiarEstadoInmueble(inmuebleId, nuevoEstadoTexto, false)
    ↓
DetalleInmuebleViewModel.cambiarEstadoInmueble(...)
    - Verifica token: ApiClient.getToken(context)
    - Marca mActualizando.postValue(true)
    - Crea Inmueble con solo el campo estado
    - Llama api.actualizarEstadoInmueble(token, inmuebleId, inmueble)
    ↓
Retrofit ejecuta PATCH /api/InmueblesApi/{id}/estado
    ↓
Backend responde con Inmueble actualizado (JSON) o error
    ↓
DetalleInmuebleViewModel.onResponse()
    - mActualizando.postValue(false)
    - Si éxito:
        mInmueble.postValue(inmuebleActualizado)
        prepararDatos(inmuebleActualizado) → refresca todos los LiveData
    - Si error:
        mError.postValue("Error al actualizar estado: " + código)
        (Opcional) recargar inmueble para restaurar estado del switch
    ↓
DetalleInmuebleFragment (observers)
    - Actualiza switch, textos y colores según los nuevos LiveData
```

## Recomendaciones / mejoras
- Añadir endpoint PUT/PATCH para actualizar todos los campos de un inmueble si la API lo soporta; implementar flujo de edición (pre-llenado en `CargarInmuebleFragment`) y `actualizarInmueble(...)` en ViewModel.
- Usar `DiffUtil` en `InmueblesAdapter` en lugar de `notifyDataSetChanged()` para mejores actualizaciones.
- Considerar subir imágenes mediante multipart en vez de Base64 (más eficiente, menos uso de memoria). Si el backend no soporta, mantener Base64.
- Añadir `mCargando` y `mError` consistentes para todas las vistas que hacen llamadas a red y usar wrappers de estado (Success/Error/Loading) para manejar UI de forma más robusta.
- Tests: unit tests para `CargarInmuebleViewModel` y `DetalleInmuebleViewModel` (mockear `ApiClient`), UI tests para flujo crear inmueble.

---

> Verificado con: `CargarInmuebleFragment.java`, `CargarInmuebleViewModel.java`, `DetalleInmuebleFragment.java`, `DetalleInmuebleViewModel.java` y `ApiClient.java`.
