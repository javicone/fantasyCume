# 📋 Servicios Creados para Liga Del Cume - Fantasy Football

## ✅ Resumen de Servicios Implementados

Se han creado **8 servicios** que cubren todas las funcionalidades de negocio de la aplicación Fantasy Football.

---

## 🗂️ Servicios y sus Responsabilidades

### 1️⃣ **LigaService**
📁 `LigaService.java`

**Responsabilidad:** Gestión de ligas fantasy

**Métodos principales:**
- `crearLiga(nombre, presupuesto)` - Crea una nueva liga
- `obtenerLiga(id)` - Obtiene una liga por ID
- `modificarLiga(id, nombre, presupuesto)` - Modifica datos de una liga
- `eliminarLiga(id)` - Elimina una liga
- `listarTodasLasLigas()` - Lista todas las ligas

**Casos de uso cubiertos:**
- Creación y configuración de ligas

---

### 2️⃣ **UsuarioService**
📁 `UsuarioService.java`

**Responsabilidad:** Gestión de usuarios (managers) de las ligas

**Métodos principales:**
- `darDeAltaUsuario(nombre, liga, puntos)` - Registra un nuevo usuario
- `modificarUsuario(id, nombre, puntos)` - Actualiza datos del usuario
- `eliminarUsuario(id)` - Elimina un usuario (cascada a alineaciones)
- `listarUsuariosPorLiga(ligaId)` - Lista usuarios de una liga
- `actualizarPuntosAcumulados(usuarioId, puntos)` - Actualiza puntos
- `obtenerRankingLiga(ligaId)` - **Funcionalidad 8:** Ranking ordenado por puntos

**Casos de uso cubiertos:**
- **1.1 a 1.4:** Agregar, modificar, eliminar y listar usuarios
- **8:** Ver clasificación general de usuarios

---

### 3️⃣ **EquipoService**
📁 `EquipoService.java`

**Responsabilidad:** Gestión de equipos de fútbol

**Métodos principales:**
- `agregarEquipo(nombre, liga)` - **Funcionalidad 1.1:** Agregar equipo
- `modificarEquipo(id, nombre, escudo)` - **Funcionalidad 1.2:** Modificar equipo
- `eliminarEquipo(id)` - **Funcionalidad 1.3:** Eliminar equipo
- `listarEquiposPorLiga(ligaId)` - **Funcionalidad 1.4:** Listar equipos
- `obtenerEquipo(id)` - Obtiene un equipo específico

**Casos de uso cubiertos:**
- **1.1:** Agregar equipos
- **1.2:** Modificar equipos
- **1.3:** Eliminar equipos
- **1.4:** Listar equipos

---

### 4️⃣ **JugadorService**
📁 `JugadorService.java`

**Responsabilidad:** Gestión de jugadores y consultas avanzadas

**Métodos principales:**
- `agregarJugador(nombre, esPortero, equipo, precio)` - **Funcionalidad 2.1:** Agregar jugador
- `actualizarJugador(id, nombre, precio, esPortero)` - **Funcionalidad 2.2:** Actualizar jugador
- `eliminarJugador(id)` - **Funcionalidad 2.3:** Eliminar jugador
- `listarTodosLosJugadores()` - **Funcionalidad 2.4:** Listar jugadores
- `listarPorteros()` - **Funcionalidad 6.1:** Listar porteros disponibles
- `listarJugadoresDeCampo()` - **Funcionalidad 6.1:** Listar jugadores de campo
- `buscarPorNombre(nombre)` - **Funcionalidad 7.1:** Buscar jugadores por nombre
- `buscarPorEquipo(equipoId)` - **Funcionalidad 7.1:** Filtrar por equipo

**Casos de uso cubiertos:**
- **2.1:** Agregar jugadores a un equipo
- **2.2:** Actualizar información de jugadores
- **2.3:** Eliminar jugadores de un equipo
- **2.4:** Listar jugadores
- **6.1:** Listar jugadores disponibles por posición
- **7.1:** Buscar jugador (filtrar por nombre o equipo)

---

### 5️⃣ **JornadaService**
📁 `JornadaService.java`

**Responsabilidad:** Gestión de jornadas de la liga

**Métodos principales:**
- `crearJornada(liga)` - Crea una nueva jornada
- `obtenerJornada(id)` - Obtiene una jornada por ID
- `listarJornadasPorLiga(ligaId)` - Lista jornadas de una liga
- `eliminarJornada(id)` - Elimina una jornada

**Casos de uso cubiertos:**
- **5.1:** Generar jornadas para enfrentamientos

---

### 6️⃣ **PartidoService**
📁 `PartidoService.java`

**Responsabilidad:** Gestión de partidos y resultados

**Métodos principales:**
- `agregarPartido(local, visitante, goles, jornada)` - **Funcionalidad 3.1:** Agregar partido
- `modificarResultado(partidoId, golesLocal, golesVisitante)` - **Funcionalidad 3.2:** Modificar resultado
- `obtenerPartidosPorJornada(jornadaId)` - **Funcionalidad 5.1:** Cuadro de enfrentamientos
- `verResultadosJornada(jornadaId)` - **Funcionalidad 9:** Ver resultados
- `obtenerPartidosDeEquipo(equipoId)` - Partidos de un equipo

**Casos de uso cubiertos:**
- **3.1:** Agregar resultados de partidos por jornada
- **3.2:** Modificar resultados de partidos
- **5.1:** Generar cuadro de enfrentamientos por jornada
- **9:** Ver resultados de partidos disputados

---

### 7️⃣ **EstadisticaService**
📁 `EstadisticaService.java`

**Responsabilidad:** Gestión de estadísticas de jugadores por partido

**Métodos principales:**
- `añadirEstadistica(jugador, partido, goles, asistencias, ...)` - **Funcionalidad 4.1:** Añadir estadísticas
- `modificarEstadistica(jugadorId, partidoId, ...)` - **Funcionalidad 4.2:** Modificar estadísticas
- `obtenerEstadisticasJugador(jugadorId)` - **Funcionalidad 7.1:** Consultar estadísticas de jugador
- `obtenerEstadisticasPartido(partidoId)` - Estadísticas de un partido
- `obtenerEstadisticasJornada(jornadaId)` - Estadísticas de una jornada

**Casos de uso cubiertos:**
- **4.1:** Añadir estadísticas de jugadores por partido
- **4.2:** Modificar estadísticas de jugadores por partido
- **7.1:** Consultar estadísticas generales de jugadores

---

### 8️⃣ **AlineacionService**
📁 `AlineacionService.java`

**Responsabilidad:** Gestión de alineaciones de usuarios y cálculo de puntos

**Métodos principales:**
- `crearAlineacion(usuario, jornada, jugadores)` - **Funcionalidad 6.2:** Crear alineación
- `consultarAlineacion(usuarioId, jornadaId)` - **Funcionalidad 6.3:** Consultar equipo alineado
- `calcularPuntosAlineacion(alineacionId)` - **Funcionalidad 10:** Calcular puntos de jornada
- `modificarAlineacion(alineacionId, jugadores)` - Modificar jugadores seleccionados
- `listarAlineacionesPorJornada(jornadaId)` - **Funcionalidad 10:** Ver todas las alineaciones de la jornada
- `eliminarAlineacionesPorUsuario(usuarioId)` - Elimina en cascada al borrar usuario

**Casos de uso cubiertos:**
- **6.2:** Seleccionar jugador por posición
- **6.3:** Consultar equipo alineado
- **10:** Puntuaciones de usuarios por jornada
- **10:** Mostrar jugadores seleccionados y sus puntuaciones

---

### 9️⃣ **RankingService**
📁 `RankingService.java`

**Responsabilidad:** Generación de rankings y clasificaciones

**Métodos principales:**
- `obtenerRankingGeneral(ligaId)` - **Funcionalidad 8:** Ranking completo de la liga
- `obtenerRankingJornada(jornadaId)` - **Funcionalidad 10:** Ranking de una jornada
- `obtenerDetallePuntuacionJornada(usuarioId, jornadaId)` - **Funcionalidad 10:** Detalle de puntos
- `actualizarPuntosAcumulados(ligaId)` - Actualiza puntos de todos los usuarios
- `obtenerPosicionUsuario(ligaId, usuarioId)` - Posición en el ranking

**Casos de uso cubiertos:**
- **8:** Ver clasificación general - Consultar ranking de todos los usuarios
- **10:** Consultar puntuación total de la jornada
- **10:** Mostrar jugadores seleccionados y sus respectivas puntuaciones

---

## 📊 Matriz de Funcionalidades Cubiertas

| Funcionalidad | Servicio(s) Responsable(s) |
|---------------|---------------------------|
| **1. Gestión de equipos (1.1-1.4)** | `EquipoService` |
| **2. Gestión de jugadores (2.1-2.4)** | `JugadorService` |
| **3. Gestión de partidos (3.1-3.2)** | `PartidoService` |
| **4. Estadísticas de jugadores (4.1-4.2)** | `EstadisticaService` |
| **5. Calendario de enfrentamientos (5.1)** | `PartidoService`, `JornadaService` |
| **6. Crear alineación (6.1-6.3)** | `AlineacionService`, `JugadorService` |
| **7. Consultar estadísticas (7.1)** | `JugadorService`, `EstadisticaService` |
| **8. Ver clasificación general** | `UsuarioService`, `RankingService` |
| **9. Ver resultados** | `PartidoService` |
| **10. Puntuaciones por jornada** | `AlineacionService`, `RankingService` |

---

## 🔧 Características Técnicas

### Anotaciones Utilizadas
- `@Service` - Marca la clase como servicio de Spring
- `@Transactional` - Gestión automática de transacciones
- `@Autowired` - Inyección de dependencias

### Gestión de Transacciones
Todos los servicios están anotados con `@Transactional`, lo que garantiza:
- **Atomicidad:** Las operaciones se completan o se revierten completamente
- **Consistencia:** Los datos permanecen consistentes
- **Rollback automático:** En caso de error, se deshacen los cambios

### Manejo de Errores
Los servicios lanzan `RuntimeException` cuando:
- No se encuentra una entidad por ID
- Falla una operación crítica
- Se intenta acceder a datos inexistentes

---

## 📝 Relación con los Repositorios

Cada servicio utiliza uno o más repositorios:

| Servicio | Repositorios Utilizados |
|----------|------------------------|
| `LigaService` | `LigaCumeRepository` |
| `UsuarioService` | `UsuarioRepository` |
| `EquipoService` | `EquipoRepository` |
| `JugadorService` | `JugadorRepository` |
| `JornadaService` | `JornadaRepository` |
| `PartidoService` | `PartidoRepository` |
| `EstadisticaService` | `EstadisticaJugadorPartidoRepository` |
| `AlineacionService` | `AlineacionRepository`, `EstadisticaService` |
| `RankingService` | `UsuarioService`, `AlineacionService` |

---

## 🎯 Próximos Pasos

Una vez implementados los servicios, se pueden:

1. **Crear controladores REST** para exponer las funcionalidades vía API
2. **Implementar tests unitarios** para cada servicio
3. **Añadir validaciones** de negocio (ej: alineación con 11 jugadores)
4. **Implementar cálculo automático de puntos** según reglas del fantasy
5. **Añadir seguridad** con Spring Security

---

## ✅ Conclusión

Se han creado **9 servicios completos** que implementan todas las **10 funcionalidades** principales del sistema Fantasy Football, siguiendo las mejores prácticas de Spring Boot y arquitectura en capas.

