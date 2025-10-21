# Diagnóstico de Problema de Carga de Imágenes

## 🔍 Pasos para Diagnosticar

He agregado **logs de debug** en el código para identificar si el problema está en el proyecto Android o en el servidor API.

---

## 📊 Cómo Verificar los Logs

### 1. **Abrir Logcat en Android Studio**
- Ir a la pestaña "Logcat" en la parte inferior
- Filtrar por los siguientes tags:
  - `INMUEBLES`
  - `INMUEBLES_ADAPTER`
  - `DETALLE_INMUEBLE`

### 2. **Ejecutar la App**
- Navegar a la sección **Inmuebles**
- Observar los logs que aparecen

---

## 🔎 Qué Buscar en los Logs

### **Log 1: Carga de Inmuebles desde API**
```
Tag: INMUEBLES
Mensaje: "Inmuebles cargados: 3"
```
✅ **Si aparece**: La API está respondiendo correctamente

❌ **Si no aparece**: Problema de conexión con la API

### **Log 2: URLs de Imágenes Recibidas**
```
Tag: INMUEBLES
Mensaje: "Inmueble ID 1 - imagenPortadaUrl: /uploads/inmuebles/imagen1.jpg"
```

#### Posibles escenarios:

#### ✅ **Escenario A: URL Relativa** (más común)
```
imagenPortadaUrl: "/uploads/inmuebles/imagen1.jpg"
```
o
```
imagenPortadaUrl: "uploads/inmuebles/imagen1.jpg"
```

**Diagnóstico**: El servidor envía rutas relativas
**Solución**: ✅ **YA IMPLEMENTADA** - El código construye la URL completa automáticamente

#### ✅ **Escenario B: URL Completa**
```
imagenPortadaUrl: "http://10.226.44.156:5000/uploads/inmuebles/imagen1.jpg"
```

**Diagnóstico**: El servidor envía URLs completas
**Solución**: ✅ El código las usa directamente

#### ❌ **Escenario C: URL Vacía o Null**
```
imagenPortadaUrl: null
```
o
```
imagenPortadaUrl: ""
```

**Diagnóstico**: **PROBLEMA EN EL SERVIDOR** - No está enviando la URL de la imagen
**Solución**: Verificar en el servidor que:
- El inmueble tiene una imagen asignada
- El campo `imagenPortadaUrl` se está serializando correctamente en el JSON

### **Log 3: URL Final Usada por Glide**
```
Tag: INMUEBLES_ADAPTER
Mensaje: "URL original de imagen: /uploads/inmuebles/imagen1.jpg"
Mensaje: "URL completa construida: http://10.226.44.156:5000/uploads/inmuebles/imagen1.jpg"
```

✅ **Si aparece "URL completa construida"**: El código está construyendo la URL correctamente

### **Log 4: Si No Hay Imagen**
```
Tag: INMUEBLES_ADAPTER
Mensaje: "No hay URL de imagen para inmueble ID: 1"
```

❌ **Diagnóstico**: **PROBLEMA EN EL SERVIDOR** - No está enviando `imagenPortadaUrl`

---

## 🛠️ Soluciones Según Diagnóstico

### **Problema 1: El servidor NO envía `imagenPortadaUrl`**

#### Verificar en el Backend (API):
1. El modelo `Inmueble` en el servidor tiene el campo `imagenPortadaUrl`
2. Al serializar a JSON, se incluye este campo
3. La base de datos tiene URLs válidas guardadas

#### Ejemplo de JSON esperado del servidor:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "direccion": "Pueyrredón 859",
      "tipoNombre": "Casa",
      "precio": 400000.00,
      "ambientes": 3,
      "disponible": true,
      "imagenPortadaUrl": "/uploads/inmuebles/imagen1.jpg"  ← IMPORTANTE
    }
  ]
}
```

---

### **Problema 2: La URL es correcta pero la imagen no carga**

#### Posibles causas:

#### A. **Problema de Permisos de Internet**
Verificar en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

#### B. **Problema de Seguridad HTTP (Cleartext)**
Si la URL es HTTP (no HTTPS), agregar en `AndroidManifest.xml`:
```xml
<application
    android:usesCleartextTraffic="true"
    ... >
```

#### C. **La imagen no existe en el servidor**
Verificar manualmente abriendo la URL en un navegador:
```
http://10.226.44.156:5000/uploads/inmuebles/imagen1.jpg
```

Si no se ve la imagen en el navegador → **Problema en el servidor**

#### D. **IP incorrecta**
Verificar que la IP `10.226.44.156` sea la correcta:
- En Windows: Abrir CMD y ejecutar `ipconfig`
- Buscar "Dirección IPv4"
- Actualizar en `ApiClient.java` línea 38:
```java
private static final String BASE_URL = "http://TU_IP:5000/";
```

---

## 📝 Checklist de Verificación

### En el Servidor (Backend):
- [ ] El campo `imagenPortadaUrl` existe en el modelo
- [ ] Se serializa correctamente en el JSON de respuesta
- [ ] Las imágenes físicamente existen en la carpeta del servidor
- [ ] La ruta de las imágenes es accesible públicamente
- [ ] El servidor devuelve URLs correctas (relativas o absolutas)

### En el Proyecto Android:
- [✅] Glide está agregado en `build.gradle.kts`
- [✅] Se construye URL completa si es relativa
- [✅] Permisos de Internet en `AndroidManifest.xml`
- [✅] `usesCleartextTraffic="true"` para HTTP
- [ ] La IP del servidor es correcta en `ApiClient.java`
- [ ] El dispositivo/emulador puede alcanzar el servidor

---

## 🧪 Prueba Manual

### Paso 1: Verificar JSON de la API
Usar un cliente REST (Postman, Thunder Client, etc.) para llamar:
```
GET http://10.226.44.156:5000/api/InmueblesApi
Headers: Authorization: Bearer {tu_token}
```

Ver si la respuesta incluye `imagenPortadaUrl` en cada inmueble.

### Paso 2: Verificar Acceso a Imagen
Copiar una URL de imagen del JSON y abrirla en el navegador:
```
http://10.226.44.156:5000/uploads/inmuebles/imagen1.jpg
```

Si se ve → Servidor OK
Si no se ve → Problema en el servidor

### Paso 3: Ver Logs de Glide
Si Glide falla al cargar, mostrará errores en Logcat con tag `Glide`.

---

## 🎯 Conclusión Rápida

### Ejecuta la app y revisa los logs:

#### Si ves en los logs:
```
INMUEBLES: Inmueble ID 1 - imagenPortadaUrl: null
```
→ **PROBLEMA EN EL SERVIDOR** (no envía URLs)

#### Si ves:
```
INMUEBLES: Inmueble ID 1 - imagenPortadaUrl: /uploads/inmuebles/imagen.jpg
INMUEBLES_ADAPTER: URL completa construida: http://10.226.44.156:5000/uploads/inmuebles/imagen.jpg
```
→ **Código Android OK**, revisar si la imagen existe en el servidor

#### Si ves:
```
No hay token guardado
```
→ Hacer logout y login nuevamente

---

## 📞 Próximos Pasos

1. **Ejecutar la app**
2. **Abrir Logcat** y filtrar por `INMUEBLES`
3. **Navegar a Inmuebles**
4. **Copiar los logs** y compartir qué mensajes aparecen
5. Con esos logs podré determinar exactamente dónde está el problema

---

**Fecha**: 20/10/2025  
**Versión**: Debug
