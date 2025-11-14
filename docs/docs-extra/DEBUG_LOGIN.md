# 🐛 Debugging del Login - Guía de Solución

## ❌ Problema Actual

La app se cierra después del login, aunque el backend responde exitosamente.

---

## ✅ Cambios Aplicados

He agregado **logs detallados** y **manejo de errores robusto** para identificar el problema exacto.

### **1. LoginActivityViewModel.java**
- ✅ Agregado try-catch para capturar excepciones
- ✅ Logs detallados de toda la respuesta del backend
- ✅ Validación de token y propietario antes de continuar
- ✅ Logs de cada campo del propietario
- ✅ Mensajes de error específicos

### **2. ApiClient.java**
- ✅ Cambiado logging level a `BODY` para ver respuestas completas
- ✅ Logs de URL y headers de cada request

---

## 🔍 Cómo Ver los Logs

### **1. Abrir Logcat en Android Studio**

1. En la parte inferior de Android Studio, click en **Logcat**
2. Selecciona tu dispositivo/emulador
3. En el filtro, busca por tag: **LOGIN** o **API_HTTP**

### **2. Filtrar por Tags**

Usa estos filtros en Logcat:

| **Tag** | **Qué muestra** |
|---------|-----------------|
| `LOGIN` | Flujo completo del login |
| `API_HTTP` | Body completo de requests/responses |
| `API_CLIENT` | URLs y headers de las peticiones |

### **3. Ejemplo de filtro en Logcat:**

```
tag:LOGIN OR tag:API_HTTP OR tag:API_CLIENT
```

---

## 📋 Qué Buscar en los Logs

### **Escenario 1: Error de parsing JSON**

**Log:**
```
E/LOGIN: EXCEPCIÓN en onResponse: Expected BEGIN_OBJECT but was STRING...
```

**Causa:** El backend NO está devolviendo un JSON válido.

**Solución:** 
1. Verifica la respuesta en Postman
2. Asegúrate que el Content-Type sea `application/json`
3. El backend debe devolver:
   ```json
   {
     "token": "...",
     "propietario": { ... }
   }
   ```

---

### **Escenario 2: Campo faltante**

**Log:**
```
D/LOGIN: Token: OK
D/LOGIN: Propietario: OK
D/LOGIN: NombreCompleto: null
```

**Causa:** El backend NO está enviando el campo `nombreCompleto`.

**Solución:** El backend debe incluir `nombreCompleto` en el objeto `propietario`:
```json
{
  "token": "eyJhbGc...",
  "propietario": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "nombreCompleto": "Juan Pérez",  // ← ESTE CAMPO
    "dni": "12345678",
    "email": "juan@example.com",
    "telefono": "123456",
    "direccion": "Calle Falsa 123",
    "estado": true
  }
}
```

---

### **Escenario 3: Token null o vacío**

**Log:**
```
E/LOGIN: ERROR: Token es null o vacío
```

**Causa:** El backend NO está generando o enviando el token.

**Solución:** Verifica que tu backend genere un JWT válido.

---

### **Escenario 4: Propietario null**

**Log:**
```
E/LOGIN: ERROR: Propietario es null
```

**Causa:** El backend NO está enviando el objeto `propietario`.

**Solución:** El backend debe incluir el propietario completo en la respuesta.

---

### **Escenario 5: NullPointerException**

**Log:**
```
E/LOGIN: EXCEPCIÓN en onResponse: Attempt to invoke virtual method '...' on a null object reference
```

**Causa:** Algún campo dentro de `propietario` es null y se está usando sin validar.

**Solución:** Ya está manejado con validaciones previas. Si persiste, envíame el stack trace completo.

---

## 🧪 Pasos para Debugging

### **Paso 1: Ejecutar la App**

1. Abre Android Studio
2. Click en **Run** (▶️)
3. Ingresa credenciales de login
4. Observa Logcat

### **Paso 2: Buscar el Tag LOGIN**

En Logcat, busca:

```
D/LOGIN: === RESPUESTA DEL BACKEND ===
D/LOGIN: Response completo: {...}
```

### **Paso 3: Copiar el JSON Completo**

Copia el JSON que aparece después de `Response completo:` y envíamelo.

**Ejemplo:**
```json
D/LOGIN: Response completo: {"token":"eyJhbGc...","propietario":{"id":1,"nombre":"Juan",...}}
```

### **Paso 4: Buscar Errores**

Si ves algo como:
```
E/LOGIN: EXCEPCIÓN en onResponse: ...
E/LOGIN: Stack trace: ...
```

Copia **TODO el stack trace** y envíamelo.

---

## 🔧 Verificación en Postman

Antes de probar en la app, verifica en Postman:

### **Request:**
```
POST https://g3kgc7hj-5000.brs.devtunnels.ms/api/AuthApi/login
Content-Type: application/json

{
  "email": "tu_email@example.com",
  "password": "tu_password"
}
```

### **Response esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "propietario": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "nombreCompleto": "Juan Pérez",
    "dni": "12345678",
    "telefono": "123456",
    "email": "juan@example.com",
    "direccion": "Calle Falsa 123",
    "estado": true,
    "fotoPerfil": null
  },
  "expiracion": "2024-12-31T23:59:59"
}
```

### ⚠️ **IMPORTANTE:**

El JSON del backend **DEBE** incluir:
- ✅ `token` (string, no vacío)
- ✅ `propietario` (objeto)
- ✅ `propietario.nombreCompleto` (string, puede ser generado como `nombre + " " + apellido`)

---

## 🚨 Posibles Problemas del Backend

### **Problema 1: Backend devuelve solo el token**

❌ **Mal:**
```json
{
  "token": "eyJhbGc..."
}
```

✅ **Bien:**
```json
{
  "token": "eyJhbGc...",
  "propietario": { ... }
}
```

### **Problema 2: Backend devuelve string en lugar de JSON**

❌ **Mal:**
```
"Login exitoso"
```

✅ **Bien:**
```json
{
  "token": "...",
  "propietario": { ... }
}
```

### **Problema 3: nombreCompleto no está en el backend**

Si tu backend **NO** tiene el campo `nombreCompleto`, puedo modificar la app para que lo genere a partir de `nombre + apellido`.

---

## 📝 Siguiente Paso

**Por favor:**

1. ✅ Ejecuta la app
2. ✅ Haz login
3. ✅ Abre Logcat
4. ✅ Busca los logs con tag `LOGIN`
5. ✅ Copia y envíame:
   - El JSON completo de `Response completo: {...}`
   - Cualquier mensaje de error que empiece con `E/LOGIN:`
   - El stack trace completo si hay una excepción

Con esa información podré decirte **exactamente qué está fallando** y cómo solucionarlo.

---

## 🛠️ Logs que Necesito

Envíame EXACTAMENTE esto de Logcat:

```
D/LOGIN: URL: https://...
D/LOGIN: === RESPUESTA DEL BACKEND ===
D/LOGIN: Response completo: {...}  ← ESTE JSON COMPLETO
D/LOGIN: Token: OK/NULL
D/LOGIN: Propietario: OK/NULL

// Si hay error:
E/LOGIN: EXCEPCIÓN en onResponse: ...
E/LOGIN: Stack trace: 
    at ...
    at ...  ← TODO EL STACK TRACE
```

---

## ✅ Una vez solucionado

Cuando el login funcione correctamente, verás en Logcat:

```
D/LOGIN: Login exitoso para: Juan Pérez
```

Y la app **NO se cerrará**, sino que navegará a MainActivity.
