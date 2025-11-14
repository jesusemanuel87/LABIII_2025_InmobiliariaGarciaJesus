# Configuración de Red para Desarrollo

## 🔧 Problema: `127.0.0.1` NO funciona en Android

En Android, `127.0.0.1` y `localhost` se refieren al **propio dispositivo/emulador**, NO a tu PC.

---

## ✅ Soluciones según tu escenario:

### **1️⃣ Usando Emulador de Android Studio**

**IP a usar:** `10.0.2.2:5000`

```java
// En ApiClient.java
private static final String BASE_URL = "http://10.0.2.2:5000/";
```

✅ **Ya está configurado por defecto**

---

### **2️⃣ Usando Dispositivo Físico (mismo WiFi)**

**Necesitas tu IP local de la PC:**

#### **Windows - Obtener IP:**
```bash
ipconfig
```
Busca `IPv4 Address` en tu adaptador WiFi (ej: `192.168.1.10`)

#### **Linux/Mac - Obtener IP:**
```bash
ifconfig
# o
ip addr
```

#### **Luego actualiza ApiClient:**
```java
// En ApiClient.java
private static final String BASE_URL = "http://192.168.1.10:5000/"; // TU IP aquí
```

⚠️ **Importante:** 
- Tu PC y teléfono deben estar en la **misma red WiFi**
- El firewall de Windows debe permitir conexiones al puerto 5000

---

### **3️⃣ Usando Dispositivo Físico (cualquier red)**

**Usa DevTunnel (recomendado para producción/testing):**

```java
// En ApiClient.java
private static final String BASE_URL = "https://g3kgc7hj-5000.brs.devtunnels.ms/";
```

✅ Funciona desde cualquier lugar con internet
✅ HTTPS seguro
❌ Requiere Visual Studio o VS Code con DevTunnel

---

## 🔒 Seguridad de Red

El archivo `network_security_config.xml` ya está configurado para:

- ✅ **Permitir HTTP** en IPs locales (solo desarrollo)
- ✅ **Requerir HTTPS** para DevTunnel (producción)
- ✅ **Bloquear HTTP** en otros dominios (seguridad)

```xml
<!-- Permitir HTTP solo para desarrollo local -->
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">10.0.2.2</domain>
    <domain includeSubdomains="true">192.168.0.0/16</domain>
    <domain includeSubdomains="true">192.168.1.0/24</domain>
</domain-config>

<!-- DevTunnel con HTTPS -->
<domain-config cleartextTrafficPermitted="false">
    <domain includeSubdomains="true">devtunnels.ms</domain>
</domain-config>
```

---

## 🐛 Solución de Problemas

### **Error: "Failed to connect to /10.0.2.2:5000"**

#### **Causa 1: Backend no está corriendo**
```bash
# Verifica que tu backend esté corriendo en el puerto 5000
# En tu terminal de backend deberías ver algo como:
# "Server running on http://127.0.0.1:5000"
```

#### **Causa 2: Puerto bloqueado por firewall (Windows)**
```bash
# Abrir firewall de Windows
# 1. Buscar "Firewall de Windows Defender"
# 2. Configuración avanzada
# 3. Reglas de entrada > Nueva regla
# 4. Puerto > TCP > 5000 > Permitir conexión
```

#### **Causa 3: Backend escuchando solo en localhost**

Tu backend debe escuchar en `0.0.0.0`, NO solo en `127.0.0.1`:

```python
# Python/Flask
app.run(host='0.0.0.0', port=5000)  # ✅ Correcto

# Node.js/Express
app.listen(5000, '0.0.0.0')  // ✅ Correcto

# ASP.NET Core
builder.WebHost.UseUrls("http://0.0.0.0:5000");  // ✅ Correcto
```

---

### **Error: "Cleartext HTTP traffic not permitted"**

✅ **Ya está resuelto** en `network_security_config.xml`

Si persiste, verifica que el dominio esté en la lista:
```xml
<domain includeSubdomains="true">10.0.2.2</domain>
```

---

### **Error: "Unable to resolve host"**

Verifica conectividad:

```bash
# Desde terminal de Android (adb shell)
adb shell
ping 10.0.2.2
```

---

## 📱 Verificación Rápida

### **Checklist antes de ejecutar:**

- [ ] ✅ Backend corriendo en puerto 5000
- [ ] ✅ Backend escuchando en `0.0.0.0` (no solo `127.0.0.1`)
- [ ] ✅ Firewall permite conexiones al puerto 5000
- [ ] ✅ `BASE_URL` correcta en `ApiClient.java`
- [ ] ✅ Permisos de INTERNET en `AndroidManifest.xml`
- [ ] ✅ `network_security_config.xml` permite HTTP local

---

## 🚀 Recomendaciones

### **Para Desarrollo:**
```java
private static final String BASE_URL = "http://10.0.2.2:5000/"; // Emulador
```

### **Para Testing en Dispositivo Físico:**
```java
private static final String BASE_URL = "http://192.168.1.10:5000/"; // Tu IP local
```

### **Para Producción/Demo:**
```java
private static final String BASE_URL = "https://tu-devtunnel.brs.devtunnels.ms/";
```

---

## 📝 Notas Adicionales

### **¿Por qué 10.0.2.2?**

Es una IP especial del emulador de Android:
- `10.0.2.1` = Router del emulador
- **`10.0.2.2` = Host machine (tu PC)** ← La que necesitas
- `10.0.2.3` = DNS del emulador

### **Rango de IPs privadas comunes:**
- `192.168.0.x` - `192.168.255.x` (más común en routers domésticos)
- `10.0.0.x` - `10.255.255.x`
- `172.16.0.x` - `172.31.255.x`

---

## ✅ Estado Actual

**Configuración aplicada:**
- ✅ `BASE_URL = "http://10.0.2.2:5000/"` (emulador)
- ✅ HTTP permitido para desarrollo local
- ✅ Network security config actualizado

**Para cambiar a dispositivo físico:**
1. Obtén tu IP local: `ipconfig` (Windows) o `ifconfig` (Linux/Mac)
2. Actualiza `BASE_URL` en `ApiClient.java`
3. Verifica que tu PC y teléfono estén en la misma WiFi
4. Reinicia la app
