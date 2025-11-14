# 🔍 Diagnóstico: Switch de Estado se Revierte

## 🐛 Problema Reportado

Al cambiar el switch de estado (Activo/Inactivo):
1. ✅ Aparece el Toast "Estado del inmueble actualizado a Activo"
2. ❌ Pero el switch vuelve a "Inactivo"

Esto indica que:
- La petición se envía correctamente al servidor
- El servidor responde con éxito
- **PERO** el servidor devuelve el inmueble con el estado sin cambiar
- Al actualizar la UI con la respuesta, el switch vuelve a su estado anterior

---

## 🔬 Logs Agregados para Diagnóstico

He agregado logs detallados en `DetalleInmuebleViewModel.java` para ver exactamente qué está pasando:

### **Logs que verás en Logcat:**

```
=== CAMBIO DE ESTADO ===
Inmueble ID: 5
Nuevo estado a enviar: Activo
Request JSON: {"estado":"Activo"}
Respuesta HTTP Code: 200
Response success: true
Response message: Estado actualizado correctamente
Estado devuelto por servidor: Inactivo  ← ⚠️ AQUÍ ESTÁ EL PROBLEMA
Disponibilidad devuelta: Disponible
```

---

## 📊 Flujo Actual del Problema

```
Usuario cambia switch a "Activo"
         ↓
App envía: {"estado":"Activo"} al servidor
         ↓
Servidor responde: HTTP 200 OK
         ↓
Servidor devuelve inmueble con estado: "Inactivo" ❌
         ↓
App actualiza UI con el estado del servidor
         ↓
Switch vuelve a "Inactivo"
```

---

## 🎯 Pasos para Diagnosticar

### **Paso 1: Ver los Logs en Logcat**

1. Ejecuta la app
2. Abre **Logcat** en Android Studio
3. Filtra por: `DETALLE_INMUEBLE`
4. Cambia el switch del inmueble
5. Copia todos los logs que aparecen

### **Paso 2: Identificar el Problema**

Busca estas líneas en los logs:

#### ✅ **Si el servidor actualiza correctamente:**
```
Estado devuelto por servidor: Activo
```
→ El problema está en el frontend (poco probable)

#### ❌ **Si el servidor NO actualiza:**
```
Estado devuelto por servidor: Inactivo
```
→ **El problema está en el servidor** (muy probable)

---

## 🛠️ Soluciones Según el Diagnóstico

### **Caso 1: El servidor devuelve el estado sin cambiar** ⚠️

#### Problema en el Backend:
El endpoint `PATCH /api/InmueblesApi/{id}/estado` no está actualizando el campo `estado` en la base de datos.

#### Verificar en el Backend (C# .NET):

1. **Endpoint correcto:**
```csharp
[HttpPatch("{id}/estado")]
public async Task<IActionResult> ActualizarEstado(int id, [FromBody] ActualizarEstadoRequest request)
{
    var inmueble = await _context.Inmuebles.FindAsync(id);
    
    if (inmueble == null)
        return NotFound();
    
    // ⚠️ VERIFICAR QUE ESTA LÍNEA EXISTA
    inmueble.Estado = request.Estado; // ← Debe actualizar el estado
    
    await _context.SaveChangesAsync();
    
    // Devolver el inmueble actualizado
    return Ok(new { success = true, data = inmueble });
}
```

2. **Modelo del Request:**
```csharp
public class ActualizarEstadoRequest
{
    public string Estado { get; set; } // ← Debe ser "Estado", no "Disponible"
}
```

3. **Verificar en la Base de Datos:**
Después de llamar al endpoint, revisar si el campo `Estado` se actualizó en la tabla `Inmuebles`.

---

### **Caso 2: El servidor recibe un campo diferente**

Si en el backend esperan `disponible` (boolean) pero estás enviando `estado` (string):

#### Solución A: Cambiar el Frontend (Android)
Volver a usar boolean si el backend no se puede cambiar:
```java
// En ActualizarEstadoInmuebleRequest.java
@SerializedName("disponible")
private boolean disponible;
```

#### Solución B: Cambiar el Backend (Recomendado)
Actualizar el backend para que reciba `estado` como String.

---

### **Caso 3: Endpoint incorrecto**

Verificar que el endpoint en el backend sea:
```
PATCH /api/InmueblesApi/{id}/estado
```

Y no:
```
PATCH /api/InmueblesApi/{id}/disponibilidad
```

---

## 🧪 Prueba Manual del Endpoint

### Usando Postman o Thunder Client:

**Request:**
```
PATCH http://10.226.44.156:5000/api/InmueblesApi/5/estado
Headers:
  Authorization: Bearer {tu_token}
  Content-Type: application/json

Body:
{
  "estado": "Activo"
}
```

**Response esperada:**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "direccion": "Pueyrredon 859",
    "estado": "Activo",  ← ⚠️ Debe reflejar el cambio
    "disponibilidad": "Disponible",
    ...
  }
}
```

Si en la respuesta `"estado"` sigue siendo `"Inactivo"`, **el problema está en el servidor**.

---

## 🔍 Qué Revisar en el Backend

### **Checklist del Backend:**

- [ ] El endpoint existe: `PATCH /api/InmueblesApi/{id}/estado`
- [ ] Recibe el parámetro `estado` (string) del body
- [ ] Actualiza el campo `Estado` del inmueble en la BD
- [ ] Llama a `SaveChangesAsync()` o similar
- [ ] Devuelve el inmueble actualizado en la respuesta
- [ ] El campo `Estado` en la respuesta tiene el nuevo valor

---

## 📝 Campos que se Envían desde Android

### **Request que envía la App:**

```json
{
  "estado": "Activo"
}
```

o

```json
{
  "estado": "Inactivo"
}
```

### **Endpoint de la App:**
```
PATCH http://10.226.44.156:5000/api/InmueblesApi/{id}/estado
```

---

## 🎯 Resumen

### **Problema Identificado:**
El servidor **NO está actualizando** el campo `Estado` del inmueble cuando recibe la petición PATCH.

### **Solución:**
Revisar y corregir el **endpoint del backend** para que:
1. Reciba correctamente el parámetro `estado` del request
2. Actualice el campo `Estado` del inmueble en la base de datos
3. Guarde los cambios
4. Devuelva el inmueble con el nuevo estado

---

## 📞 Próximos Pasos

1. **Ejecuta la app y cambia el switch**
2. **Copia los logs de Logcat** (filtro: `DETALLE_INMUEBLE`)
3. **Comparte los logs** para confirmar el diagnóstico
4. **Revisa el código del backend** del endpoint de actualización de estado
5. **Verifica en la base de datos** si el campo se actualiza

Con los logs podremos confirmar si el problema está en el backend o frontend.

---

**Fecha**: 20/10/2025  
**Issue**: Switch se revierte después de cambiar estado
