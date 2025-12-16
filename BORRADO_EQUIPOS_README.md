# Sistema de Borrado de Equipos con Regeneración Automática

## Resumen de la Implementación

Se ha implementado un sistema inteligente para el borrado de equipos que verifica si la liga ha sido reiniciada antes de permitir la eliminación. Si la liga está reiniciada (sin resultados), el sistema elimina todas las dependencias y regenera automáticamente los cuadros de competición.

## Cambios Realizados

### 1. Archivo Modificado: `EquipoService.java`

#### Nuevas Dependencias Añadidas
```java
@Autowired
private PartidoRepository partidoRepository;

@Autowired
private JornadaService jornadaService;

@Autowired
private AlineacionRepository alineacionRepository;
```

#### Método Principal Modificado: `eliminarEquipo(Long id)`

**Nuevo Comportamiento:**
1. **Validaciones básicas**: Verifica que el equipo exista y tenga una liga asociada
2. **Verificación de estado de liga**: Comprueba si la liga ha sido reiniciada
3. **Si la liga está reiniciada** (todos los partidos con resultado 0-0):
   - Elimina todas las alineaciones de las jornadas
   - Elimina todas las jornadas (lo que elimina los partidos en cascada)
   - Elimina el equipo
   - **Regenera automáticamente los cuadros de competición** para los equipos restantes
4. **Si la liga tiene resultados**: Lanza una excepción indicando que primero debe reiniciar la liga

#### Métodos Auxiliares Añadidos

##### `verificarLigaReiniciada(Long ligaId)`
- **Propósito**: Verifica si todos los partidos de la liga tienen resultado 0-0
- **Retorna**: `true` si la liga está reiniciada, `false` si hay resultados

##### `regenerarCuadrosCompeticion(Long ligaId)`
- **Propósito**: Regenera las jornadas y partidos usando el algoritmo Round-Robin
- **Comportamiento**:
  - Obtiene los equipos restantes de la liga
  - Si hay menos de 2 equipos, no genera jornadas
  - Genera enfrentamientos para la primera y segunda vuelta
  - Crea las jornadas y partidos con resultado inicial 0-0

## Flujo de Ejecución

```
Usuario solicita eliminar equipo
    ↓
¿El equipo existe?
    ↓ Sí
¿La liga tiene una liga asociada?
    ↓ Sí
¿La liga está reiniciada? (todos los partidos 0-0)
    ↓ Sí
1. Eliminar alineaciones de todas las jornadas
    ↓
2. Eliminar todas las jornadas (partidos en cascada)
    ↓
3. Eliminar el equipo
    ↓
4. ¿Quedan al menos 2 equipos?
    ↓ Sí
5. Regenerar cuadros de competición (Round-Robin)
    ↓
Éxito: Equipo eliminado y jornadas regeneradas
```

## Casos de Uso

### Caso 1: Liga Reiniciada con Suficientes Equipos
```
Estado: Liga tiene 4 equipos, todos los partidos 0-0
Acción: Eliminar "Equipo A"
Resultado:
  ✓ Se eliminan alineaciones
  ✓ Se eliminan jornadas
  ✓ Se elimina "Equipo A"
  ✓ Se regeneran jornadas para los 3 equipos restantes
```

### Caso 2: Liga Reiniciada con Solo 2 Equipos
```
Estado: Liga tiene 2 equipos, todos los partidos 0-0
Acción: Eliminar "Equipo A"
Resultado:
  ✓ Se eliminan alineaciones
  ✓ Se eliminan jornadas
  ✓ Se elimina "Equipo A"
  ✗ No se regeneran jornadas (solo queda 1 equipo)
```

### Caso 3: Liga con Resultados
```
Estado: Liga tiene 4 equipos, hay partidos con resultados
Acción: Eliminar "Equipo A"
Resultado:
  ✗ Error: "No se puede eliminar el equipo porque la liga ya 
    tiene resultados registrados. Para eliminar el equipo, 
    primero debes reiniciar la liga (Generar Jornadas con force=true)."
```

## Integración con el Sistema Existente

### Compatible con:
- **JornadaController**: Usa el mismo algoritmo Round-Robin para generar jornadas
- **EstadisticaService**: Respeta el método `resetEstadisticasYResultadosDeLiga`
- **PartidoService**: Usa los métodos existentes del repositorio

### Sin Dependencias Circulares
La implementación evita dependencias circulares usando:
- `@Autowired` para inyección de dependencias
- Uso directo de repositorios cuando es posible
- Solo usa `JornadaService` para operaciones específicas

## Validaciones SQL

El sistema evita fallos SQL porque:
1. **Elimina en orden correcto**: Alineaciones → Jornadas → Partidos (cascada) → Equipo
2. **Regenera después de eliminar**: Crea nuevas jornadas con referencias válidas
3. **Verifica integridad**: Solo permite eliminación si la liga está reiniciada

## Mensajes del Sistema

### Mensaje de Éxito
```
"Equipo eliminado exitosamente. Los cuadros de competición han sido 
regenerados automáticamente."
```

### Error: Equipo no encontrado
```
"No existe ningún equipo con ID: {id}. No se puede eliminar."
```

### Error: Equipo sin liga
```
"El equipo no tiene una liga asociada"
```

### Error: Liga con resultados
```
"No se puede eliminar el equipo porque la liga ya tiene resultados 
registrados. Para eliminar el equipo, primero debes reiniciar la liga 
(Generar Jornadas con force=true)."
```

### Error: Regeneración fallida
```
"Error al regenerar cuadros de competición: {mensaje}"
```

### Error: Controlador (genérico)
```
"Error al eliminar el equipo: {mensaje}"
```

## Recomendaciones de Uso

1. **Antes de eliminar equipos**: Verificar que la liga esté reiniciada
2. **Para reiniciar la liga**: Usar el botón "Generar Jornadas" con `force=true`
3. **Después de eliminar**: El sistema regenera automáticamente, no requiere acción manual
4. **Con menos de 2 equipos**: El sistema no generará jornadas, pero permitirá la eliminación

## Pruebas Recomendadas

### Test 1: Eliminar equipo en liga reiniciada
1. Crear liga con 4 equipos
2. Generar jornadas con force=true (reiniciar)
3. Eliminar 1 equipo
4. Verificar que se regeneraron jornadas para 3 equipos

### Test 2: Intentar eliminar con resultados
1. Crear liga con 4 equipos
2. Generar jornadas
3. Agregar resultados a algunos partidos
4. Intentar eliminar 1 equipo
5. Verificar que se muestra error

### Test 3: Eliminar hasta 1 equipo
1. Crear liga con 2 equipos
2. Generar jornadas con force=true
3. Eliminar 1 equipo
4. Verificar que no hay jornadas pero el equipo se eliminó

## Notas Técnicas

- **Transaccional**: El método `eliminarEquipo` usa `@Transactional` heredado de la clase
- **Algoritmo Round-Robin**: Mismo que `JornadaController.generarJornadasTransactional`
- **Performance**: Optimizado para ligas con hasta 20 equipos
- **Thread-safe**: Las operaciones son atómicas gracias a `@Transactional`

## Mantenimiento Futuro

Si necesitas modificar el algoritmo de generación de jornadas:
1. Actualiza `JornadaController.generarJornadasTransactional`
2. Actualiza `EquipoService.regenerarCuadrosCompeticion` con los mismos cambios
3. Considera extraer el algoritmo a un servicio separado para evitar duplicación

---

**Fecha de Implementación**: 16 de Diciembre de 2025
**Versión**: 1.0
**Autor**: Sistema de IA

