# Implementación de Clasificación de Equipos

## Resumen
Se ha implementado exitosamente una funcionalidad de clasificación de equipos para la Liga del Cume.

## Archivos Creados

### 1. ClasificacionEquipo.java (DTO)
**Ubicación:** `src/main/java/com/example/Liga_Del_Cume/data/model/ClasificacionEquipo.java`

**Descripción:** Clase DTO que representa la información de clasificación de un equipo.

**Características:**
- Almacena datos del equipo (ID, nombre, escudo URL)
- Contiene estadísticas: victorias, empates, derrotas
- Calcula puntos automáticamente: Victoria = 3 puntos, Empate = 1 punto, Derrota = 0 puntos
- Implementa `Comparable` para ordenar por:
  1. Puntos (descendente)
  2. Diferencia de goles (descendente)
  3. Goles a favor (descendente)

### 2. ClasificacionService.java
**Ubicación:** `src/main/java/com/example/Liga_Del_Cume/data/service/ClasificacionService.java`

**Descripción:** Servicio que gestiona la lógica de negocio de la clasificación.

**Métodos principales:**
- `obtenerClasificacionLiga(Long ligaId)`: Obtiene la clasificación completa de una liga
- `obtenerClasificacionEquipo(Long equipoId)`: Obtiene estadísticas de un equipo específico
- `obtenerTopEquipos(Long ligaId, int cantidad)`: Obtiene los N mejores equipos
- `procesarPartido(Partido partido, Map clasificacionMap)`: Procesa un partido y actualiza estadísticas

**Lógica de cálculo:**
1. Obtiene todos los equipos de la liga
2. Obtiene todos los partidos de cada equipo
3. Por cada partido, actualiza:
   - Goles a favor y en contra
   - Victorias, empates o derrotas según el resultado
   - Calcula puntos automáticamente
4. Ordena la lista por puntos (usando Comparable)

### 3. ClasificacionController.java
**Ubicación:** `src/main/java/com/example/Liga_Del_Cume/data/Controller/ClasificacionController.java`

**Descripción:** Controlador que maneja las peticiones HTTP para la clasificación.

**Endpoints:**
- `GET /liga/{ligaId}/clasificacion`: Muestra la tabla de clasificación
- `GET /liga/{ligaId}/clasificacion/`: Endpoint alternativo (redirecciona)

**Funcionalidad:**
- Obtiene el usuario de la sesión
- Llama al servicio para obtener la clasificación
- Pasa datos a la vista (clasificación, ligaId, usuario, etc.)
- Maneja errores y redirecciona si es necesario

### 4. clasificacion.html
**Ubicación:** `src/main/resources/templates/clasificacion.html`

**Descripción:** Vista Thymeleaf que muestra la tabla de clasificación.

**Características de la tabla:**
- **Posición (Pos):** Número de posición en la tabla
- **Equipo:** Escudo + nombre del equipo
- **PJ:** Partidos jugados
- **V:** Victorias
- **E:** Empates
- **D:** Derrotas
- **GF:** Goles a favor
- **GC:** Goles en contra
- **DIF:** Diferencia de goles (GF - GC)
- **PTS:** Puntos totales (destacado en negrita y color borgoña)

**Diseño:**
- Estilo consistente con el resto de la aplicación
- Colores degradados para top 3 (oro, plata, bronce)
- Efecto hover en las filas
- Responsive para móviles (oculta columnas menos importantes)
- Muestra escudos de equipos
- Iconos de escudo por defecto si no hay URL

## Integración con el Menú

La opción de "Clasificación" ya existe en el menú lateral (`menu.html`) y apunta a:
```
/liga/{id}/clasificacion
```

Esta ruta ahora está completamente funcional.

## Sistema de Puntuación

El sistema calcula puntos según el resultado de cada partido:

| Resultado | Puntos |
|-----------|--------|
| Victoria  | +3     |
| Empate    | +1     |
| Derrota   | +0     |

## Criterios de Desempate

Si dos equipos tienen los mismos puntos, se desempata por:
1. **Diferencia de goles** (GF - GC)
2. **Goles a favor** (mayor cantidad)

## Flujo de Datos

```
Usuario → ClasificacionController
          ↓
    ClasificacionService
          ↓ (consulta)
    EquipoRepository + PartidoRepository
          ↓ (procesa)
    Calcula estadísticas y puntos
          ↓ (ordena)
    Lista de ClasificacionEquipo
          ↓ (retorna)
    ClasificacionController → Vista HTML
```

## Validaciones Implementadas

1. **En el servicio:**
   - Verifica que el ID de la liga sea válido (no nulo, positivo)
   - Retorna lista vacía si no hay equipos
   - Valida existencia de equipos y partidos
   - Evita errores con verificaciones null-safe

2. **En el controlador:**
   - Maneja excepciones de tipo EquipoException
   - Maneja excepciones generales
   - Redirecciona con mensajes de error si falla
   - Valida sesión del usuario

## Funcionalidades Adicionales

- **Partidos jugados:** Se calcula sumando victorias + empates + derrotas
- **Goles:** Se contabilizan tanto los marcados como los recibidos
- **Vista responsive:** Se adapta a diferentes tamaños de pantalla
- **Manejo de imágenes:** Muestra icono por defecto si no hay escudo

## Próximos Pasos (Opcional)

Si deseas extender la funcionalidad, podrías:
1. Añadir filtro por jornada específica
2. Mostrar histórico de posiciones
3. Añadir gráficos de evolución
4. Exportar clasificación a PDF/Excel
5. Añadir más estadísticas (tarjetas, posesión, etc.)

## Pruebas Recomendadas

1. **Sin equipos:** Verificar que muestre mensaje apropiado
2. **Con equipos sin partidos:** Todos deben tener 0 puntos
3. **Con partidos jugados:** Verificar cálculo correcto de puntos
4. **Empate en puntos:** Verificar que use diferencia de goles
5. **Responsive:** Probar en diferentes dispositivos

## Dependencias

No se requieren dependencias adicionales. La implementación usa:
- Spring Boot (ya configurado)
- Thymeleaf (ya configurado)
- Bootstrap 5 (ya incluido)
- Bootstrap Icons (ya incluido)

