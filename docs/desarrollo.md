Desarrollo web-api Android – ASP.NET
Desarrollar una aplicación móvil para los propietarios de la inmobiliaria.

Funcionalidad mínima requerida de la App:
•	Login/logout de propietarios
•	Ver y editar su perfil. Cambiar la clave por separado.
•	Resetear la contraseña ("me olvidé la contraseña").
•	Listar sus inmuebles
•	Habilitar/Deshabilitar un inmueble de este propietario.
•	Agregar un nuevo inmueble con foto y por defecto deshabilitado.
•	Listar contratos por Inmuebles y sus pagos.
La aplicación no debe enviar el id del propietario. Este debe ser recuperado a través del token.
Todas las funcionalidades, salvo el Login, deben requerir estar autenticado.
REST API - Principios  
➢ Transferencia del Estado Representacional 
○ Protocolo cliente/servidor sin estado 
○ Conjunto de operaciones bien definidas 
○ Sintaxis universal para identificar recursos 
○ Uso de hipermedia (HTTP) 
➢ Interfaz Uniforme. Acceso por URI. 
➢ Cliente-Servidor. Ambos débilmente acoplados. 
➢ Sin Estado. Sin necesidad de sesiones. 
➢ Cacheable. Mejora el rendimiento. 
➢ Sistema de Capas. Mejora rendimiento y escalabilidad
REST API - Fallas comunes
➢ Semántica asociada a HTTP. Uso incorrecto de métodos HTTP. POST como un método "catch-all". 
➢ Ausencia de estado. Sesiones guardadas en el servidor en lugar de usar tokens como JWT. 
➢ Diseño centrado en recursos. Diseñadas como RPC en lugar de modelar recursos con URIs claras. 
➢ HATEOAS (Hypermedia as the Engine of Application State). Pocas APIs incluyen enlaces en las respuestas para guiar al cliente sobre las acciones posibles, lo que limita la navegabilidad.
Métodos o Verbos HTTP
➢ GET (cRud) 
➢ POST (Crud o Abm) 
➢ PUT y PATCH (crUd o abM) 
➢ DELETE (cruD o aBm) 
➢ HEAD, CONNECT, OPTIONS, TRACE

Códigos de Estado de Respuesta HTTP
➢ 1xx Respuestas informativas 
➢ 2xx Respuestas satisfactorias ○ 200 OK ○ 201 Creado 
➢ 3xx Redireccionamiento 
➢ 4xx Error en el cliente 
○ 400 Bad Request 
○ 401 Sin Autorización - 403 Prohibido 
○ 404 No encontrado - 405 Método no permitido 
➢ 5xx Error en el servidor 
○ 500 Error interno - 501 No implementado 
○ 503 No disponible
Cross Origin Resource Sharing
➢ Intercambio de Recursos de Origen Cruzado 
➢ Política del mismo origen 
➢ Sirve para prevenir ataques (fraudes) 
➢ La coincidencia se basa en protocolo, dominio y puerto
ASP.Net Core - Web API
➢ ASP.Net Core soporta la creación de servicios RESTFul conocidos también como Web API 
➢ Para manejar las peticiones se utilizan Controladores que derivan de la clase ControllerBase 
➢ La clase base Controller deriva de ControllerBase y añade soporte para Vistas
ControllerBase  
➢ La clase ControllerBase proveé métodos auxiliares para manejar peticiones HTTP: 
○ BadRequest (400) 
○ NotFound (404) 
○ Unauthorized (401) 
○ Forbid (403) 
○ PhysicalFile (retorna un archivo) 
○ Ok (200) 
○ CreatedAtAction (201) 
Atributos - ApiController
➢ Habilita comportamientos específicos de API 
○ Obliga el enrutamiento por atributos 
○ Errores de validación del modelo activan automáticamente una respuesta HTTP 400 
○ Permite binding definido en cada parámetro ([FromBody], [FromForm], [FromHeader], [FromQuery], [FromRoute], [FromServices]) 
○ Inferencia de petición Multipart/form-data (IFormFile) cuando se especifica [FromForm] 
○ Detalles del problema para códigos de estado de error
Atributos - Route 
➢ El decorador/atributo Route permite especificar la plantilla para mapear acciones 
➢ Se puede combinar ruteo por atributos [Route] + [“Verbo HTTP”] 
➢ Posibles verbos: [HttpGet], [HttpPost], [HttpPut], [HttpPatch], [HttpDelete] y [HttpHead] 
➢ Todos los atributos de verbos permiten también especificar ruteo complementado aquello especificado en el atributo Route

Autenticación con JsonWebToken
¿Qué es JWT?  
➢ Estándar abierto (RFC 7519) que define una forma compacta y autónoma de transmitir información de forma segura entre las partes 
➢ Trabaja en formato JSON 
➢ Se puede verificar y confiar porque está firmada digitalmente 
➢ Se utilizan tanto autenticación como otros intercambios de información 

Estructura JWT 3 
➢ Encabezado. Generalmente 2 partes: 
○ “typ”: especifica el tipo (JWT) 
○ “alg”: especifica el algoritmo 
➢ Carga útil. Contiene las afirmaciones sobre la entidad: 
○ “Iss”: emisor 
○ “Exp”: vencimiento 
○ “Sub”: asunto 
○ Otros datos registrados o propios 
➢ Firma. Partes codificadas + secreto + algoritmo 
➢ Formato: xxxxx.yyyyy.zzzzz 
Ejemplo 
{ "alg": "HS256", "typ": "JWT" } { "sub": "asunto", "nombre": "Juan Perez", "admin": true } HMACSHA256( base64UrlEncode(header) + "." + base64UrlEncode(payload), secret) eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 .eyJzdWIiOiJhc 3VudG8iLCJub21icmUiOiJKdWFuIFBlcmV6IiwiYWRtaW4iOnR ydWV9.g8eysEjHn2Ee7maylqAH2mOgz12ePbczp8zOcGn69-w 


Uso 
➢ Al iniciar sesión, el servidor verifica los datos, crea las afirmaciones (claim) y se devuelve el token 	
➢ El cliente guarda el token y lo envía en peticiones que requieren autorización 
➢ Debe añadirse en el encabezado de la petición: Authorization: Bearer
Configurar el servicio (ConfigureServices)
 
Configurar app (Configure)
 
Crear políticas de autorización (ConfigureServices)
 
Crear token de autenticación (Login)
 
Crear token de autenticación (Login)
 
Uso de Authorize y AllowAnonymous
 

User.Identity
➢ User.Identity.Name devuelve el valor de la claim tipo ClaimTypes.Name 
➢ User.IsInRole(rol) pregunta si tiene la claim tipo ClaimTypes.Rol con valor “rol” 
➢ User.Identity.IsAuthenticated dice si el usuario está autenticado 
➢ User.Claims devuelve las claims del usuario


Entity Framework
➢ Entity Framework es un ORM (Object-Relational Mapping) 
➢ Reduce la cantidad de código y mantenimiento que se necesita para las aplicaciones orientadas a datos ➢ EF trabaja con un modelo (Código o diseño) 
➢ Sin errores de tipo 
➢ Patrón repositorio 
➢ Su clase principal es System.Data.Entity.DbContext 
➢ Crear un contexto que hereda de DbContext

 

DbContext  
➢ Representa una sesión con una base de datos y proporciona una API para comunicarse con la BD 
➢ Es responsable de abrir y administrar las conexiones a la BD 
➢ Tiene operaciones para ABM 
➢ Posee seguimiento de cambios 
➢ Encargada del mapeo de datos 
➢ Caché de objetos 
➢ Gestión de transacciones 
Alta de entidades  
➢ Utilizando Add de DbContext var autor = new Autor{ Nombre = "Juan", Apellido = "Perez" }; contexto.Add(autor); contexto.SaveChanges(); 
➢ Alta de entidades relacionadas var autor = new Autor { Nombre = "Juan", Apellido = "Perez", Libros = new List { new Libro { Titulo = "Caperuza"}, new Libro { Titulo = "Negranieves" }, new Libro { Titulo = "Los 3 caranchos" } } }; contexto.Add(autor); contexto.SaveChanges(); 
Alta de múltiples entidades 
➢ Utilizando AddRange de DbContext var lista = new List { new Libro { Titulo = "Caperuza"}, new Libro { Titulo = "Negranieves" }, new Libro { Titulo = "Los 3 caranchos" } }; contexto.AddRange(lista); contexto.SaveChanges(); 
Modificación de entidades 
➢ Varía de acuerdo a si el contexto está realizando un seguimiento (tracking) de la entidad a modificar 
➢ Si obtenemos la entidad del contexto, el seguimiento es inmediato: var autor = contexto.Autores.First(a => a.AutorId == 1); autor.Nombre = “Juancito”; contexto.SaveChanges(); 
➢ Caso contrario, se debe informar al contexto de las modificaciones mediante: 
○ Cambio del estado de la entidad 
○ El método Update del contexto 
○ El método Attach del contexto y marcando las modificaciones 
Modificación por Estado 
➢ Cambiar el estado de la entidad a “modificado” usando el método Entry: public void Guardar(Autor autor) { context.Entry(autor).State = EntityState.Modified; context.SaveChanges(); } 
➢ Actualiza todas las propiedades de la entidad, pero no sus relaciones (Autor.Libros) 
Modificación con Update  
➢ El contexto posee métodos Update y UpdateRange para modificar entidades: public void Guardar(Autor autor) { context.Update(autor); context.SaveChanges(); } 
➢ Actualiza todas las propiedades de la entidad y sus relaciones (Autor.Libros) 
➢ Las entidades relacionadas sin Id serán insertadas 
Modificación con Attach 
➢ El contexto posee un método Attach para hacer el seguimiento de la entidad 
➢ Luego se deben marcar las modificaciones con Entry similar a la actualización por estado: public void Guardar(Autor autor) { context.Attach(autor); context.Entry(autor).Property("Nombre").IsModified = true; context.SaveChanges(); } 
➢ Las entidades relacionadas sin Id serán insertadas 
Eliminación de Entidades 
➢ Varía de acuerdo a si el contexto está realizando un seguimiento (tracking) de la entidad a eliminar 
➢ Si obtenemos la entidad del contexto, el seguimiento es inmediato: var autor = contexto.Autores.Single(a => a.AutorId == 1); context.Remove(autor); contexto.SaveChanges(); 
➢ Se puede simplificar usando un stub: var autor = new Autor {AutorId = 1}; context.Remove(autor); contexto.SaveChanges(); 
Eliminación por Estado 
➢ Cambiar el estado de la entidad a “eliminado” usando el método Entry: public void Eliminar(Autor autor) { context.Entry(autor).State = EntityState.Deleted; context.SaveChanges(); } 
➢ Actualiza todas las propiedades de la entidad, pero no sus relaciones (Autor.Libros) 
Eliminación de Relaciones 
➢ El método varía de acuerdo a como se hayan definido las relaciones 
➢ Si la relación fue definida como eliminar en cascada o “set null”, la BD se encarga de las relaciones 
➢ Si no es ese el caso, primero se debe trabajar con las relaciones y luego con la entidad en cuestión 
DbSet 
➢ Representa una colección de entidades 
➢ Es el punto de entrada para la tabla en la BD 
➢ Se crean propiedades DbSet en el DbContext 
➢ DbSet es una implementación del patrón repositorio 
➢ Provee métodos para ABM y recuperación de datos 
Consulta de Datos  
➢ First y FirstOrDefault: cuando muchos cumplen el criterio pero se quiere solo el primero. Similar a TOP(1) o LIMIT 1 
➢ Single y SingleOrDefault: cuando un solo elemento debería cumplir el criterio 
➢ Find: similar al anterior pero buscando por Id context.Autores.FirstOrDefault(a => a.Nombre == “Juan”); context.Autores.SingleOrDefault(a => a.AutorId == 7); context.Autores.Find(7); 
Filtrado y Ordenamiento 
➢ Where permite filtrar con expresiones lambda: context.Autores.Where(a => a.Nombre == “Juan”); context.Autores.Where(a => a.Nombre == “Juan” && a.Apellido == “Perez”); 
➢ OrderBy, OrderByDescending, ThenOrderBy y ThenOrderByDescending permiten ordenar resultados: context.Autores.OrderBy(a => a.Apellido) .ThenOrderBy(a => a.Nombre); 
Agrupamiento  
➢ GroupBy permite agrupar resultados: var grupos = context.Libros.GroupBy(l => l.AutorId); foreach(var g in grupos) { //g.Key es el autor id y g son los libros de ese autor } 
➢ Para agrupar por múltiples datos usando tipos anónimos: var grupos = context.Libros.GroupBy(l => new {Autor = l.AutorId, Editorial = l.EditorialId}); foreach(var g in grupos) { //g.Key.AutorId es el autor id //g.Key.EditorialId es el editorial id } 
Selección 
➢ Select permite seleccionar determinados campos: var autores = context.Libros.Where(l => l.EditorialId == 3) .Select(l.Autor); var datos = context.Libros.Select( new { Nombre = l.Nombre, Editorial = l.Editorial.Nombre }); 
➢ Con Include y ThenInclude se pueden anexar (join) relaciones: var libros = context.Libros.Include(l => l.Autor).ToList(); var libros = context.Libros .Include(l => l.Autor) .Include(l => l.Editorial) .ToList(); var libros = context.Libros .Include(l => l.Autor) .Include(l => l.Editorial) .ThenInclude(e => e.Pais) .ToList(); 
Join 
➢ Todas las entidades devueltas por consultas tienen “tracking” automáticamente 
➢ En caso de necesitar que no lleven tracking se puede explicitar: var libros = context.Libros.AsNoTracking().ToList(); 
Tracking 
➢ Llamando al método ToQueryString() en un objeto IQueryable podemos revisar el sql generado var query = context.Libros .Where(l => l.Precio > 100) .OrderBy(l => l.Nombre); var sql = query.ToQueryString(); 
Revisión 
➢ Si por cualquier razón, la consulta no se puede hacer vía EF, se puede ejecutar una consulta “cruda” 
➢ Usar formateo o string interpolado para evitar inyecciones de sql var libros = context.Libros.FromSqlRaw("SELECT LibroId, Nombre, AutorId, Isbn FROM Libros").ToList(); var libro = context.Libros.FromSqlRaw("SELECT LibroId, Nombre, AutorId, Isbn FROM Libros WHERE LibroId = {0}", id).Single(); var libro = context.Libros.FromSqlRawInterpolated($"SELECT LibroId, Nombre, AutorId, Isbn FROM Libros WHERE LibroId = {id}").Single();
 FromSqlRaw 
➢ Siguiendo el uso de FromSqlRaw se pueden ejecutar procedimientos almacenados: var libros = context.Libros .FromSqlRaw("EXEC ObtenerLibros").ToList(); var id = new SqlParameter("@id", 1); var libro = context.Libros .FromSqlRaw("EXEC ObtenerLibroPorId", id) .Single(); 
Procedimientos Almacenados 
➢ El objeto DbContext provee una propiedad Database que permite hacer operaciones de ADO.Net directamente using (var command = context.Database.GetDbConnection().CreateCommand()) { command.CommandText = "SELECT * From Tabla"; context.Database.OpenConnection(); using (var result = command.ExecuteReader()) { // hacer algo con el resultado } } 
Aprovechando ADO.Net 
➢ DbSet también provee métodos similares a los ya vistos para hacer operaciones de ABM: context.Libros.Add(libro); context.Libros.Update(libro); context.Libros.Remove(libro); context.SaveChanges();

Definición (DataContext.cs)
 
Servicio (Startup.cs)
 
Uso en controlador
 

Convenciones 
➢ Clave primaria 
○ Id 
➢ Claves foráneas 
○ <nbre_entidad>Id 
➢ Tablas 
○ DbSet <Entidad> <nbre_tabla>
➢ Columnas 
○ Propiedades igual que columnas 
Data Annotations 
➢ EF basado en “Convention over Configuration” 
➢ Las anotaciones añaden configuración 
➢ Se utilizan como atributos de las propiedades y la clase 
➢ System.ComponentModel.DataAnnotations 
➢ Sirven para ASP.Net MVC también 
➢ Key - para clave compuesta usar Column(Order=n) 
➢ Required, MaxLength y MinLength 
➢ NotMapped 
➢ Table y Column 
➢ DatabaseGenerated 
Data Annotations 
➢ ForeingKey es utilizado para configurar relaciones 
a. En la propiedad de la clase dependiente, pasando el nombre de la propiedad de navegación 
b. En la propiedad de navegación de la clase dependiente, pasando el nombre de la prop FK 
c. En la propiedad de navegación en la clase principal, pasando el nombre de la prop FK en la clase dependiente 
Data Annotations  
➢ a) public class Libro { public int LibroId { get; set; } public string Nombre { get; set; } public Autor Autor { get; set; } [ForeignKey("Autor")] public int AutorFK { get; set; } } 
➢ b) public class Libro { public int LibroId { get; set; } public string Nombre { get; set; } [ForeignKey("AutorFK")] public Autor Autor { get; set; } public int AutorFK { get; set; } } 
c) public class Autor { public int AutorId { get; set; } public string Nombre { get; set; } public string Apellido { get; set; } [ForeignKey("AutorFK")] public IList Libros { get; set; } }
