# Documentación: Agregar Resultados de Partidos

## 📋 Descripción General

La funcionalidad de **Agregar Resultados** permite a los administradores de la liga registrar y editar las estadísticas detalladas de cada jugador en los partidos, calculando automáticamente:
- Puntos de fantasy de cada jugador
- Marcador del partido
- Puntos acumulados de los usuarios
- Clasificación de equipos actualizada

## 🎯 Características Principales

### 1. Selección de Jornada
- Desplegable en la parte superior derecha
- Permite cambiar entre diferentes jornadas
- Muestra todos los partidos de la jornada seleccionada

### 2. Vista de Partidos
- Grid responsivo con tarjetas de partidos
- Muestra escudos y nombres de equipos
- Marcador actualizado (goles local - goles visitante)
- Click en cualquier partido para editar

### 3. Modal de Edición de Partido

#### Marcador en Tiempo Real
- Se actualiza automáticamente al sumar goles
- Muestra los goles de cada equipo

#### Listas de Jugadores
- **Equipo Local** (izquierda)
- **Equipo Visitante** (derecha)

Para cada jugador se muestra:
- **Avatar/Foto** del jugador
- **Nombre** del jugador
- **Badge de PORTERO** (si aplica)
- **Puntos de la jornada** (actualizados en tiempo real)

### 4. Edición de Estadísticas por Jugador

Al hacer click en un jugador, se despliegan los campos editables:

#### Campos Comunes (Todos los jugadores):
- **Goles**: Número de goles anotados (input numérico)
- **Asistencias**: Número de asistencias (input numérico)
- **Tarjetas Amarillas**: Desplegable (0, 1, 2)
  - Si selecciona 2, se marca automáticamente tarjeta roja
- **Tarjeta Roja**: Checkbox
- **Mínimo Minutos Jugados**: Checkbox (más de 25 minutos)

#### Campo Extra para Porteros:
- **Goles Recibidos**: Se actualiza automáticamente según goles del rival
  - Portero local recibe los goles del equipo visitante
  - Portero visitante recibe los goles del equipo local
  - Campo de solo lectura (readonly)

## 🎮 Flujo de Uso

### Paso 1: Acceder a la Funcionalidad
1. Navegar a una liga
2. Click en menú lateral → "Opciones Admin" → "Agregar Resultados"

### Paso 2: Seleccionar Jornada
1. Usar el desplegable superior derecho
2. Seleccionar la jornada deseada

### Paso 3: Editar Partido
1. Click en el partido que deseas editar
2. Se abre modal con todos los jugadores

### Paso 4: Editar Jugadores
1. Click en cada jugador para expandir sus estadísticas
2. Modificar goles, asistencias, tarjetas, etc.
3. Los puntos se recalculan automáticamente
4. El marcador se actualiza con los goles

### Paso 5: Guardar
1. Click en "Guardar Resultados"
2. Se guardan todas las estadísticas
3. Se actualizan:
   - Puntos de jugadores
   - Marcador del partido
   - Puntos de usuarios
   - Clasificación de equipos

## ⚙️ Sistema de Puntuación Fantasy

### Jugadores de Campo

| Acción | Puntos |
|--------|--------|
| Jugar mínimo de minutos | +1 |
| Gol anotado | +4 |
| Asistencia | +3 |
| Tarjeta amarilla | -1 |
| Tarjeta roja | -3 |

### Porteros

| Acción | Puntos |
|--------|--------|
| Jugar mínimo de minutos | +1 |
| Gol anotado | +6 |
| Asistencia | +3 |
| Portería a cero (0 goles recibidos) | +5 |
| 1-2 goles recibidos | -1 |
| 3+ goles recibidos | -2 |
| Tarjeta amarilla | -1 |
| Tarjeta roja | -3 |

### Ejemplo de Cálculo

**Jugador de campo:**
- Juega 60 minutos (✓ mínimo) = +1
- Marca 2 goles = +8
- Da 1 asistencia = +3
- Tarjeta amarilla = -1
- **Total: 11 puntos**

**Portero:**
- Juega 90 minutos (✓ mínimo) = +1
- No recibe goles = +5
- **Total: 6 puntos**

## 🔄 Actualizaciones Automáticas

### 1. Goles Recibidos de Porteros
- Se actualizan automáticamente al modificar goles
- Portero local recibe goles del visitante
- Portero visitante recibe goles del local

### 2. Marcador del Partido
- Suma automática de todos los goles anotados
- Actualización en tiempo real
- Se guarda en la base de datos

### 3. Puntos de Usuarios
- Se recalculan basándose en las alineaciones
- Se suman los puntos de todos los jugadores de cada usuario
- Actualización de `puntosAcumulados` en Usuario

### 4. Clasificación de Equipos
- Se actualiza automáticamente después de guardar
- Recalcula victorias, empates, derrotas
- Actualiza puntos según resultados

## 📊 Arquitectura Técnica

### Backend

#### Controlador: `AgregarResultadosController.java`

**Endpoints:**

1. `GET /liga/{ligaId}/admin/agregar-resultados`
   - Muestra la página principal
   - Parámetro opcional: `jornadaId`

2. `GET /liga/{ligaId}/admin/partido/{partidoId}/detalles`
   - Retorna JSON con datos del partido
   - Incluye jugadores y estadísticas actuales

3. `POST /liga/{ligaId}/admin/partido/{partidoId}/guardar`
   - Guarda estadísticas de todos los jugadores
   - Actualiza marcador y puntos

**Métodos Privados:**

- `calcularPuntosFantasy()`: Calcula puntos según las reglas
- `actualizarGolesRecibidosPorteros()`: Actualiza goles recibidos
- `recalcularPuntosUsuarios()`: Actualiza puntos de usuarios

### Frontend

#### Vista: `agregarResultados.html`

**Componentes JavaScript:**

- `abrirModalPartido()`: Abre modal de edición
- `mostrarDetallesPartido()`: Renderiza datos del partido
- `crearJugadorHTML()`: Genera HTML de cada jugador
- `toggleJugador()`: Expande/colapsa estadísticas
- `actualizarEstadisticas()`: Recalcula puntos en tiempo real
- `actualizarMarcador()`: Actualiza marcador del partido
- `guardarEstadisticas()`: Envía datos al servidor

### Base de Datos

**Tablas Utilizadas:**

- `Jornada`: Jornadas de la liga
- `Partido`: Partidos de cada jornada
- `Jugador`: Jugadores de los equipos
- `EstadisticaJugadorPartido`: Estadísticas por partido
- `Usuario`: Usuarios/managers
- `Alineacion`: Alineaciones de usuarios por jornada

## 🎨 Diseño Visual

### Colores
- **Fondo:** Azul oscuro (#0A0E27)
- **Primario:** Rosa/fucsia (#D946A6)
- **Secundario:** Rojo (#FF3366)
- **Acento:** Dorado (#FFD700)

### Layout
- **Grid responsive** para partidos
- **Modal de pantalla completa** para edición
- **Dos columnas** para equipos local y visitante
- **Tarjetas expandibles** para jugadores

### Efectos
- Hover en tarjetas de partidos
- Transiciones suaves
- Marcador en tiempo real
- Feedback visual al seleccionar jugador

## 🔐 Seguridad y Validaciones

### Backend
- Validación de IDs de liga y partido
- Verificación de existencia de entidades
- Validaciones de números negativos
- Límites en tarjetas amarillas (máx 2)

### Frontend
- Campos numéricos con `min="0"`
- Auto-marcado de tarjeta roja con 2 amarillas
- Confirmación antes de guardar
- Manejo de errores con alerts

## 🐛 Manejo de Errores

### Errores Comunes

1. **"Partido no encontrado"**
   - Causa: ID de partido inválido
   - Solución: Verificar que el partido existe

2. **"Error al cargar detalles"**
   - Causa: Problema de conexión o datos
   - Solución: Recargar la página

3. **"Error al guardar"**
   - Causa: Validación fallida o problema BD
   - Solución: Revisar datos ingresados

### Logs
Los errores se registran en:
- Console del navegador (frontend)
- Logs de Spring Boot (backend)

## 📱 Responsive Design

### Desktop (>768px)
- Grid de 2-3 columnas para partidos
- Modal con equipos lado a lado
- Todas las estadísticas visibles

### Tablet (768px)
- Grid de 2 columnas
- Modal ajustado
- Navegación simplificada

### Móvil (<768px)
- Grid de 1 columna
- Modal en columna única (equipos apilados)
- Menú colapsado

## 🚀 Mejoras Futuras

### Posibles Extensiones:
1. **Autocompletado** de estadísticas desde APIs externas
2. **Historial de cambios** en estadísticas
3. **Validación de coherencia** (ej: goles del partido vs suma de goles)
4. **Notificaciones** a usuarios cuando se actualizan puntos
5. **Exportar** resultados a PDF
6. **Gráficos** de rendimiento de jugadores
7. **Comparador** de estadísticas entre jornadas
8. **Modo rápido** para edición masiva

## 📝 Ejemplo de Uso Completo

### Escenario: Registrar resultado Real Madrid 3-1 Barcelona

1. **Acceder**: Admin → Agregar Resultados
2. **Seleccionar**: Jornada 5
3. **Abrir**: Click en partido Real Madrid vs Barcelona
4. **Editar jugadores locales (Real Madrid)**:
   - Benzema: 2 goles, 1 asistencia, mínimo minutos → 13 pts
   - Modric: 1 asistencia, mínimo minutos → 4 pts
   - Courtois: 0 goles, 1 gol recibido, mínimo minutos → 0 pts
5. **Editar jugadores visitantes (Barcelona)**:
   - Lewandowski: 1 gol, mínimo minutos → 5 pts
   - Ter Stegen: 3 goles recibidos, mínimo minutos → -1 pts
6. **Verificar marcador**: 3-1 ✓
7. **Guardar**: Click en "Guardar Resultados"
8. **Resultado**: 
   - Estadísticas guardadas
   - Puntos de usuarios actualizados
   - Clasificación actualizada (Real Madrid +3 pts)

## ✅ Checklist de Implementación

- [x] Controlador creado (`AgregarResultadosController`)
- [x] Vista HTML creada (`agregarResultados.html`)
- [x] Endpoints REST implementados
- [x] Sistema de puntuación fantasy
- [x] Actualización automática de marcador
- [x] Goles recibidos de porteros (automático)
- [x] Recálculo de puntos de usuarios
- [x] Integración con menú admin
- [x] Diseño responsive
- [x] JavaScript interactivo
- [x] Validaciones frontend y backend
- [x] Manejo de errores
- [x] Documentación completa

## 🎉 Conclusión

La funcionalidad de **Agregar Resultados** está completa y lista para usar. Permite una gestión integral de las estadísticas de partidos con:
- Interfaz intuitiva y moderna
- Cálculos automáticos
- Actualizaciones en cascada
- Experiencia de usuario fluida

**¡El sistema está listo para registrar resultados de partidos!** ⚽🏆

