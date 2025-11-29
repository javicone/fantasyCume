# 🔧 DEPURACIÓN: Error al Cargar Detalles del Partido

## 🐛 Error Actual

Al hacer click en un partido aparece: **"Error al cargar los detalles del partido"**

## 🔍 Pasos para Depurar

### 1. Abrir Console del Navegador (F12)

1. Presiona **F12** en tu navegador
2. Ve a la pestaña **Console**
3. Recarga la página de "Agregar Resultados"
4. Haz click en un partido

### 2. Observar los Logs

Deberías ver algo como:

```
DOM Cargado - Inicializando event listeners
Tarjetas de partido encontradas: 2
Tarjeta encontrada con ID: 1
Tarjeta encontrada con ID: 2
=== Abriendo modal para partido ID: 1 ===
URL de petición: /liga/1/admin/partido/1/detalles
Respuesta recibida, status: XXX
```

### 3. Identificar el Problema

#### ✅ Si ves "status: 200"
- Significa que el servidor responde correctamente
- El problema está en el frontend (JavaScript)

#### ❌ Si ves "status: 404"
- Significa que la URL no existe
- Problema en el routing del backend

#### ❌ Si ves "status: 500"
- Error en el servidor
- Problema en el código Java

#### ❌ Si no ves ningún log
- El JavaScript no se está ejecutando
- Problema con la carga del script

## 🔧 Soluciones Según el Error

### Error 404 (Not Found)

**Verifica que el controlador esté compilado:**
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### Error 500 (Internal Server Error)

**Mira los logs de Spring Boot en la consola donde ejecutaste la app.**
Busca líneas que digan `ERROR` o `Exception`.

### No se ejecuta el JavaScript

**Verifica que la página haya cargado completamente:**
1. Mira el código fuente de la página (Click derecho → Ver código fuente)
2. Busca `function abrirModalPartido`
3. Verifica que exista

## 🧪 Test Manual del Endpoint

Abre una nueva pestaña y ve a:
```
http://localhost:8080/liga/1/admin/partido/1/detalles
```

**Deberías ver un JSON con:**
```json
{
  "partidoId": 1,
  "equipoLocal": "Real Madrid",
  "equipoVisitante": "FC Barcelona",
  "jugadoresLocal": [...],
  "jugadoresVisitante": [...]
}
```

### Si NO ves el JSON:
El problema es el backend → Ve a la sección "Solución Backend"

### Si SÍ ves el JSON:
El problema es el frontend → Ve a la sección "Solución Frontend"

---

## 🔧 SOLUCIÓN BACKEND

Si el endpoint `/liga/1/admin/partido/1/detalles` no funciona:

### 1. Verificar que el controlador esté registrado

Busca en los logs al iniciar la app:
```
Mapped "{[/liga/{ligaId}/admin/partido/{partidoId}/detalles]}"
```

### 2. Recompilar el proyecto
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### 3. Verificar que existan partidos en la BD

Abre MySQL:
```sql
SELECT * FROM partido LIMIT 5;
```

Si no hay partidos, el DataInitializer no se ejecutó.

---

## 🔧 SOLUCIÓN FRONTEND

Si el endpoint funciona pero el modal no se abre:

### 1. Verificar que Bootstrap esté cargado

En la console (F12), escribe:
```javascript
typeof bootstrap
```

Debería decir: `"object"`

Si dice `"undefined"`, Bootstrap no está cargado.

### 2. Verificar que el modal existe en el DOM

En la console (F12), escribe:
```javascript
document.getElementById('modalEditarPartido')
```

Debería devolver el elemento HTML.

Si dice `null`, el HTML del modal no está en la página.

### 3. Probar abrir el modal manualmente

En la console (F12), escribe:
```javascript
abrirModalPartido(1)
```

Y observa qué pasa.

---

## 📝 Reporte de Error

Después de seguir estos pasos, anota:

1. **¿Qué status code viste en la console?** (200, 404, 500, etc.)
2. **¿El endpoint manual funciona?** (Sí/No)
3. **¿Qué logs aparecen en la console?**
4. **¿Hay errores en rojo en la console?**

Con esta información podré darte una solución específica.

---

## 🚀 SOLUCIÓN RÁPIDA (Reiniciar Todo)

Si nada funciona, prueba esto:

```bash
# 1. Detener la aplicación (Ctrl+C)

# 2. Limpiar y recompilar
./mvnw clean package -DskipTests

# 3. Reiniciar
./mvnw spring-boot:run

# 4. Espera a ver este mensaje:
#    "Started LigaDelCumeApplication in X seconds"

# 5. Abre el navegador
#    http://localhost:8080

# 6. Borra caché del navegador (Ctrl+Shift+Del)

# 7. Prueba de nuevo
```

---

## 📞 AYUDA ADICIONAL

**Comparte conmigo:**
1. Screenshot de la console (F12)
2. Los logs de Spring Boot cuando haces click
3. El resultado de probar el endpoint manualmente

¡Con eso podré darte una solución exacta! 🎯

