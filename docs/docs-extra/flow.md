Flujo (circuito) — desde login de un propietario hasta listar sus inmuebles
Resumen en 8 pasos (luego explico cada componente con referencias de código):
1.	Usuario escribe email/clave en la pantalla de login (activity_login.xml / LoginActivity).
2.	LoginActivity llama a LoginActivityViewModel.login(email, password).
3.	LoginActivityViewModel crea LoginRequest y usa ApiClient.getMyApiInterface(context).login(...) (Retrofit).
4.	Mientras espera, el ViewModel actualiza mCargando (MutableLiveData<Boolean>) para indicar loading.
5.	En onResponse: si es exitoso guarda token y Propietario en ApiClient.guardarToken(...) y ApiClient.guardarPropietario(...) (SharedPreferences) y lanza MainActivity.
6.	MainActivity / navegación muestra la sección "Inmuebles"; InmueblesFragment se instancia.
7.	InmueblesFragment obtiene InmueblesViewModel y observa:
o	getMInmuebles() (LiveData<List<Inmueble>>)
o	getMError() (LiveData<String>)
o	getMCargando() (LiveData<Boolean>)
8.	InmueblesViewModel.cargarInmuebles() lee token con ApiClient.getToken(context) y llama api.listarInmuebles(token); en respuesta hace mInmuebles.postValue(lista); el Fragment observa y actualiza InmueblesAdapter.setInmuebles(...), que actualiza el RecyclerView.
Detalle por componente y código (qué recibe / qué expone)
•	Vista (Activity / Fragment)
o	LoginActivity (archivo LoginActivity.java):
	Crea ViewModel: mv = new ViewModelProvider(this).get(LoginActivityViewModel.class);
	Llama a mv.login(email, pass) al pulsar botón.
	Observa mv.getMutable() (es un LiveData<String>) para mensajes de error u otros toasts:
	Código: mv.getMutable().observe(this, new Observer<String>() { ... });
	Nota: usa ViewBinding (ActivityLoginBinding) para acceder a vistas.
o	InmueblesFragment (archivo InmueblesFragment.java):
	Crea ViewModel: mv = new ViewModelProvider(this).get(InmueblesViewModel.class);
	Observa:
	mv.getMInmuebles().observe(getViewLifecycleOwner(), Observer<List<Inmueble>>) -> actualiza adapter.
	mv.getMError() -> muestra Toast.
	mv.getMCargando() -> muestra/oculta ProgressBar.
	Inicializa RecyclerView y InmueblesAdapter y le pasa listeners (click, cambio de estado).
	Llama mv.cargarInmuebles() en onCreateView y onResume para refrescar.
•	ViewModel (capa de presentación)
o	LoginActivityViewModel:
	Campos (internos, MutableLiveData):
	MutableLiveData<String> mutable — usado para mensajes/error (expuesto como LiveData<String> getMutable()).
	MutableLiveData<Boolean> mCargando — estado de carga (expuesto con getMCargando()).
	Método login(email,password):
	setea mCargando.postValue(true).
	Llama a API (Retrofit enqueue), en onResponse guarda token y Propietario con ApiClient y lanza MainActivity (usa Context de Application).
	En errores hace mutable.postValue("Error...") y mCargando.postValue(false).
	Observaciones: expone LiveData de solo lectura (buena práctica): la vista observa LiveData y no puede mutarlo directamente.
o	InmueblesViewModel:
	Campos (internos):
	MutableLiveData<List<Inmueble>> mInmuebles -> expuesto con LiveData<List<Inmueble>> getMInmuebles().
	MutableLiveData<String> mError -> expuesto con getMError().
	MutableLiveData<Boolean> mCargando -> expuesto con getMCargando().
	cargarInmuebles():
	Lee token: String token = ApiClient.getToken(context); (si no hay token postea error).
	Llama api.listarInmuebles(token) y en onResponse hace mInmuebles.postValue(response.body()).
	En falla hace mError.postValue(...).
	cambiarEstadoInmueble(int, String):
	Llama API actualizarEstadoInmueble y en success vuelve a cargarInmuebles() para refrescar la lista.
•	Repositorio / API client
o	ApiClient.java:
	Construye Retrofit (getMyApiInterface(context)).
	Métodos utilitarios:
	guardarToken(context, token) — guarda token en SharedPreferences (añade "Bearer " si falta).
	getToken(context) — retorna token.
	guardarPropietario(context, Propietario) — guarda Propietario serializado en SharedPreferences.
	getPropietario(context) — lee el Propietario guardado.
	Interfaz MyApiInterface con endpoints (p. ej. @GET("api/InmueblesApi") Call<List<Inmueble>> listarInmuebles(@Header("Authorization") String token)).
•	Modelos
o	Inmueble.java representa la entidad con campos (id, direccion, precio, estado, imagenes...). Retrofit + Gson mapean JSON -> objetos.
•	Adapter / RecyclerView
o	InmueblesAdapter (archivo InmueblesAdapter.java):
	Mantiene internamente List<Inmueble> inmuebles.
	setInmuebles(List<Inmueble>) reemplaza la lista y llama notifyDataSetChanged().
	onBindViewHolder llama holder.bind(inmueble, listener, estadoListener) para inflar datos en vistas (imagen con Glide, formateo de precio, switch de estado).
	Los listeners del adapter envían eventos de UI al Fragment (que a su vez llama métodos del ViewModel, p. ej. mv.cambiarEstadoInmueble(...)).
Observadores y ciclo de vida (por qué se usan así)
•	En Activities se usa observe(this, ...) (observa con Activity lifecycle). En Fragment se usa getViewLifecycleOwner() para que la observación se cancele cuando la vista del fragment se destruye (evita leaks y actualizaciones fuera de vista).
•	ViewModels sobreviven a cambios de configuración; LiveData notifica solo cuando el owner está activo.
•	MutableLiveData: solo el ViewModel lo modifica. Se expone públicamente como LiveData para que la vista no lo mutile.
Valores y threading: postValue vs setValue
•	El código usa postValue(...) dentro de callbacks de Retrofit (hilos de background); postValue asegura que la actualización llegue al hilo principal.
•	setValue(...) se usa cuando se está en el hilo principal.
Manejo de estados UI y errores
•	Patrón usado: tres LiveData por conjunto de datos:
o	lista de datos (mInmuebles), estado de carga (mCargando) y error (mError).
•	Ventaja: la vista puede reaccionar a combinaciones (mostrar progress, mostrar lista o mensaje, mostrar Toast).
•	Observación en InmueblesFragment:
o	Cuando mInmuebles cambia -> adapter.setInmuebles(items) y visibilidad de vistas se actualiza.
o	Cuando mError cambia -> Toast.
o	Cuando mCargando cambia -> toggle progressBar / recyclerView.
Navegación
•	InmueblesFragment usa Navigation component:
o	Navigation.findNavController(root).navigate(R.id.detalleInmuebleFragment, bundle).
o	Los argumentos (ej. inmuebleId) se pasan en Bundle; el fragment destino puede leerlos.
Ejemplo secuenciado con pseudo-calls (resumen)
•	Usuario press Login -> LoginActivity -> LoginActivityViewModel.login(email, pass)
•	ViewModel -> ApiClient.login(...) (Retrofit enqueue)
•	Retrofit resp OK -> ViewModel guarda token y propietario (ApiClient.guardarToken/guardarPropietario) -> inicia MainActivity
•	InmueblesFragment.onCreateView -> mv = new ViewModelProvider(this).get(InmueblesViewModel.class) -> mv.cargarInmuebles()
•	InmueblesViewModel.cargarInmuebles() -> api.listarInmuebles(token) -> en onResponse mInmuebles.postValue(lista)
•	InmueblesFragment (observador) recibe lista -> adapter.setInmuebles(lista) -> RecyclerView renderiza
Puntos concretos de código que ya vi (referencias)
•	LoginActivityViewModel:
o	private MutableLiveData<String> mutable y private MutableLiveData<Boolean> mCargando.
o	login(...) usa mCargando.postValue(true) y mutable.postValue(...) en errores.
o	Guarda token: ApiClient.guardarToken(context, loginResponse.getToken());
o	Guarda propietario: ApiClient.guardarPropietario(context, loginResponse.getPropietario());
o	Lanza MainActivity con context.startActivity(intent).
•	InmueblesViewModel.cargarInmuebles():
o	String token = ApiClient.getToken(context); y api.listarInmuebles(token) luego mInmuebles.postValue(inmuebles);
•	InmueblesFragment:
o	Observa mv.getMInmuebles(), mv.getMError(), mv.getMCargando().
o	Inicializa adapter: adapter = new InmueblesAdapter(new ArrayList<>(), getContext());
•	InmueblesAdapter.setInmuebles(List<Inmueble>) -> notifyDataSetChanged()
Edge cases y comportamientos ya cubiertos por el código
•	No hay token -> InmueblesViewModel postea error "No hay sesión activa".
•	Estado de carga gestionado por mCargando.
•	Errores HTTP y de conexión se muestran mediante mError o mutable (en login).
•	Imagenes con URLs relativas/localhost son normalizadas en InmueblesAdapter (reemplazo de localhost por baseUrl).
Recomendaciones / mejoras (prácticas actuales y mejoras pequeñas)
•	Mejor separar ApiClient en una capa Repository inyectable (p. ej. con Hilt) para facilitar testing.
•	Usar Retrofit con coroutines (suspend) y viewModelScope.launch en lugar de callbacks enqueue para código más limpio.
•	Considerar State/UIState sellado (sealed class) o SingleLiveEvent para eventos (navegación, toasts) para evitar re-emisión en rotaciones.
•	Reemplazar notifyDataSetChanged() por DiffUtil para mejor rendimiento en listas grandes.
•	Añadir manejo de refresh y caching (Room) si se quiere modo offline.
•	Tests:
o	Unit tests para ViewModels (mockear ApiClient/Retrofit).
o	UI tests para navegación y RecyclerView.
Resumen y cómo lo verifiqué
•	Cambios: ninguno (solo inspeccioné código).
•	Verificación: leí LoginActivity.java, LoginActivityViewModel.java, InmueblesFragment.java, InmueblesViewModel.java, InmueblesAdapter.java, ApiClient.java, Inmueble.java.
•	Resultado: descrito arriba — di el flujo completo, dónde se actualizan los LiveData, cómo la vista consume LiveData, y cómo el adapter se alimenta.
