# 🧪 Guía de Prueba: Agregar Resultados

## 📋 Objetivo
Esta guía te ayudará a probar la nueva funcionalidad de **Agregar Resultados** paso a paso.

---

## ✅ Pre-requisitos

Antes de empezar, asegúrate de tener:

1. ✅ Base de datos inicializada
2. ✅ Al menos 1 liga creada
3. ✅ Al menos 1 jornada creada
4. ✅ Al menos 2 equipos con jugadores
5. ✅ Al menos 1 partido en la jornada
6. ✅ Aplicación Spring Boot en ejecución

---

## 🚀 Prueba Básica

### Paso 1: Iniciar la Aplicación

```bash
cd C:\Users\Miguel\Desktop\FantasyCume\fantasyCume
./mvnw spring-boot:run
```

Espera el mensaje:
```
Started LigaDelCumeApplication in X.XXX seconds
```

### Paso 2: Acceder a la Aplicación

1. Abre navegador
2. Ve a: `http://localhost:8080`
3. Inicia sesión como administrador

### Paso 3: Navegar a Agregar Resultados

1. Click en una liga
2. Menu lateral → **Opciones Admin**
3. Click en **Agregar Resultados**

**Resultado esperado:**
```
✅ Se muestra página con selector de jornada
✅ Se muestran partidos de la jornada
✅ Cada partido tiene escudos y marcador
```

### Paso 4: Cambiar Jornada

1. Click en selector de jornada (arriba derecha)
2. Selecciona otra jornada

**Resultado esperado:**
```
✅ Página recarga automáticamente
✅ Se muestran partidos de la nueva jornada
```

### Paso 5: Abrir Modal de Partido

1. Click en cualquier tarjeta de partido

**Resultado esperado:**
```
✅ Se abre modal grande
✅ Muestra marcador del partido
✅ Muestra jugadores del equipo local (izquierda)
✅ Muestra jugadores del equipo visitante (derecha)
✅ Cada jugador tiene foto, nombre y puntos
```

### Paso 6: Expandir Jugador

1. Click en un jugador de la lista

**Resultado esperado:**
```
✅ Se expande formulario de edición
✅ Muestra campos: goles, asistencias, tarjetas
✅ Si es portero: muestra campo "goles recibidos"
```

### Paso 7: Editar Estadísticas

1. Cambia **Goles** a: `2`
2. Cambia **Asistencias** a: `1`
3. Marca **Mínimo Minutos Jugados**: ☑

**Resultado esperado:**
```
✅ Puntos del jugador se actualizan automáticamente
✅ Marcador del partido se actualiza
✅ Si es portero rival: goles recibidos se actualizan
```

### Paso 8: Probar Tarjetas

1. Selecciona **Tarjetas Amarillas**: `2`

**Resultado esperado:**
```
✅ Checkbox de tarjeta roja se marca automáticamente
✅ Puntos se recalculan con penalización (-4)
```

### Paso 9: Guardar Resultados

1. Click en **[💾 Guardar Resultados]**

**Resultado esperado:**
```
✅ Mensaje: "Estadísticas guardadas correctamente"
✅ Modal se cierra
✅ Página se recarga
✅ Marcador actualizado en tarjeta de partido
```

### Paso 10: Verificar Actualizaciones

1. Ve a **Clasificación** en el menú

**Resultado esperado:**
```
✅ Equipos tienen puntos actualizados
✅ Victorias/empates/derrotas correctos
```

2. Ve a **Ranking** en el menú

**Resultado esperado:**
```
✅ Usuarios tienen puntos actualizados
✅ Ranking ordenado correctamente
```

---

## 🎯 Pruebas Específicas

### Prueba A: Portero con Portería a Cero

**Objetivo:** Verificar bonus de portería a cero

1. Abre partido
2. Selecciona portero
3. Marca **Mínimo Minutos**: ☑
4. NO agregues goles al equipo rival
5. Observa **Goles Recibidos**: `0`

**Resultado esperado:**
```
✅ Puntos del portero: +6 (1 base + 5 bonus)
```

### Prueba B: Portero con Muchos Goles Recibidos

**Objetivo:** Verificar penalización por goles

1. Abre partido
2. Jugadores rivales marcan 4 goles
3. Observa portero: **Goles Recibidos**: `4`

**Resultado esperado:**
```
✅ Puntos del portero: -1 (1 base - 2 penalización)
```

### Prueba C: Gol de Portero

**Objetivo:** Portero que marca gol

1. Abre partido
2. Selecciona portero
3. **Goles**: `1`
4. **Mínimo Minutos**: ☑

**Resultado esperado:**
```
✅ Puntos del portero: +7 (1 base + 6 gol)
✅ Marcador actualizado con el gol
```

### Prueba D: Jugador Expulsado

**Objetivo:** Tarjeta roja directa o doble amarilla

**Opción 1: Doble amarilla**
1. **Tarjetas Amarillas**: `2`

**Resultado esperado:**
```
✅ Tarjeta roja se marca automáticamente
✅ Puntos: -5 (-2 amarillas, -3 roja)
```

**Opción 2: Roja directa**
1. Marca **Tarjeta Roja**: ☑

**Resultado esperado:**
```
✅ Puntos: -3
```

### Prueba E: Marcador Complejo

**Objetivo:** Partido con múltiples goleadores

1. Jugador A: 2 goles
2. Jugador B: 1 gol
3. Jugador C: 1 gol

**Resultado esperado:**
```
✅ Marcador equipo: 4
✅ Suma correcta de todos los goles
✅ Portero rival: goles recibidos = 4
```

### Prueba F: Editar Partido Existente

**Objetivo:** Modificar estadísticas guardadas

1. Guarda estadísticas de un partido
2. Cierra modal
3. Vuelve a abrir el mismo partido
4. Modifica estadísticas
5. Guarda de nuevo

**Resultado esperado:**
```
✅ Datos anteriores se muestran correctamente
✅ Nuevos datos sobrescriben los anteriores
✅ Puntos se recalculan correctamente
```

---

## 🐛 Pruebas de Errores

### Error 1: Partido sin Jugadores

**Escenario:** Equipo sin jugadores registrados

**Resultado esperado:**
```
✅ Modal se abre
✅ Lista de jugadores vacía
⚠️ Mensaje informativo (opcional)
```

### Error 2: Conexión Perdida

**Escenario:** Servidor no responde

1. Detén el servidor
2. Intenta abrir partido

**Resultado esperado:**
```
✅ Alert: "Error al cargar los detalles del partido"
```

### Error 3: Datos Inválidos

**Escenario:** Valores negativos

1. Abre console del navegador (F12)
2. Intenta poner valores negativos

**Resultado esperado:**
```
✅ Campo input no permite negativos (min="0")
```

---

## 📊 Casos de Prueba Completos

### Caso 1: Real Madrid 3-1 Barcelona

**Setup:**
- Real Madrid (local) vs Barcelona (visitante)
- Marcador inicial: 0-0

**Acciones:**

**Real Madrid:**
1. Benzema: 2 goles, 1 asistencia, mín. jugados
2. Modric: 0 goles, 1 asistencia, mín. jugados
3. Courtois (portero): 0 goles, mín. jugados

**Barcelona:**
1. Lewandowski: 1 gol, 0 asistencias, mín. jugados
2. Pedri: 0 goles, 0 asistencias, mín. jugados
3. Ter Stegen (portero): 0 goles, mín. jugados

**Guardar**

**Verificaciones:**

```
✅ Marcador: 3-1
✅ Benzema: 13 puntos (1+8+3)
✅ Modric: 4 puntos (1+3)
✅ Courtois: 0 puntos (1-1, por 1 gol recibido)
✅ Lewandowski: 5 puntos (1+4)
✅ Pedri: 1 punto (1)
✅ Ter Stegen: -1 punto (1-2, por 3 goles recibidos)
✅ Real Madrid: +3 puntos clasificación
✅ Barcelona: +0 puntos clasificación
```

### Caso 2: Empate 2-2 con Expulsión

**Setup:**
- Atlético (local) vs Sevilla (visitante)
- Marcador inicial: 0-0

**Acciones:**

**Atlético:**
1. Griezmann: 1 gol, 1 asistencia, mín. jugados
2. Morata: 1 gol, 0 asistencias, 2 amarillas, mín. jugados
3. Oblak (portero): mín. jugados

**Sevilla:**
1. En-Nesyri: 2 goles, 0 asistencias, mín. jugados
2. Bono (portero): mín. jugados

**Guardar**

**Verificaciones:**

```
✅ Marcador: 2-2
✅ Griezmann: 8 puntos (1+4+3)
✅ Morata: 2 puntos (1+4-2-3)
✅ Morata: tarjeta roja marcada automáticamente
✅ Oblak: 0 puntos (1-1, por 2 goles)
✅ En-Nesyri: 9 puntos (1+8)
✅ Bono: 0 puntos (1-1, por 2 goles)
✅ Atlético: +1 punto clasificación
✅ Sevilla: +1 punto clasificación
```

---

## ✅ Checklist de Pruebas

### Funcionalidad Básica
- [ ] Selector de jornada funciona
- [ ] Partidos se muestran correctamente
- [ ] Modal se abre al click
- [ ] Jugadores se listan (local/visitante)
- [ ] Fotos/avatares se muestran
- [ ] Badge "PORTERO" aparece

### Edición de Estadísticas
- [ ] Campos se pueden editar
- [ ] Valores numéricos aceptan solo números
- [ ] Desplegable de amarillas funciona
- [ ] Checkbox de roja funciona
- [ ] Checkbox de minutos funciona

### Automatizaciones
- [ ] 2 amarillas → auto-marca roja
- [ ] Goles → actualiza marcador
- [ ] Goles rival → actualiza portero
- [ ] Cambios → recalcula puntos
- [ ] Guardar → actualiza todo

### Cálculo de Puntos
- [ ] Puntos jugador campo correctos
- [ ] Puntos portero correctos
- [ ] Portería a cero: +5
- [ ] Goles recibidos: penalizaciones
- [ ] Tarjetas: penalizaciones

### Sistema Completo
- [ ] Puntos usuarios actualizados
- [ ] Clasificación equipos actualizada
- [ ] Datos persisten en BD
- [ ] Ediciones posteriores funcionan

### Diseño
- [ ] Responsive en móvil
- [ ] Hover effects funcionan
- [ ] Colores correctos
- [ ] Modal se ve bien

### Errores
- [ ] Manejo de errores de red
- [ ] Validaciones frontend
- [ ] Alerts informativos
- [ ] Console sin errores críticos

---

## 🎯 Resultado Esperado Final

Después de todas las pruebas:

```
✅ Todos los partidos registrados correctamente
✅ Marcadores actualizados
✅ Puntos de jugadores calculados
✅ Usuarios con puntos correctos
✅ Clasificación actualizada
✅ Sistema funcionando al 100%
```

---

## 📝 Reporte de Problemas

Si encuentras algún problema, anota:

1. **¿Qué hiciste?** (pasos exactos)
2. **¿Qué esperabas?** (resultado esperado)
3. **¿Qué pasó?** (resultado real)
4. **Console errors?** (F12 → Console)
5. **Navegador y versión**

---

## 🎉 ¡Prueba Completa!

Si todas las pruebas pasan:

```
╔═══════════════════════════════════════╗
║  ✅ FUNCIONALIDAD VERIFICADA          ║
║  ✅ TODO FUNCIONA CORRECTAMENTE       ║
║  ✅ LISTA PARA USAR EN PRODUCCIÓN     ║
╚═══════════════════════════════════════╝
```

**¡Puedes empezar a agregar resultados reales!** ⚽🏆

---

_Happy Testing! 🧪_

