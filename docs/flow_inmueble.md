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

## Recomendaciones / mejoras
- Añadir endpoint PUT/PATCH para actualizar todos los campos de un inmueble si la API lo soporta; implementar flujo de edición (pre-llenado en `CargarInmuebleFragment`) y `actualizarInmueble(...)` en ViewModel.
- Usar `DiffUtil` en `InmueblesAdapter` en lugar de `notifyDataSetChanged()` para mejores actualizaciones.
- Considerar subir imágenes mediante multipart en vez de Base64 (más eficiente, menos uso de memoria). Si el backend no soporta, mantener Base64.
- Añadir `mCargando` y `mError` consistentes para todas las vistas que hacen llamadas a red y usar wrappers de estado (Success/Error/Loading) para manejar UI de forma más robusta.
- Tests: unit tests para `CargarInmuebleViewModel` y `DetalleInmuebleViewModel` (mockear `ApiClient`), UI tests para flujo crear inmueble.

---

> Verificado con: `CargarInmuebleFragment.java`, `CargarInmuebleViewModel.java`, `DetalleInmuebleFragment.java`, `DetalleInmuebleViewModel.java` y `ApiClient.java`.
