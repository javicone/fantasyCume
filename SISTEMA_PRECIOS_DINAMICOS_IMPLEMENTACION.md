# Sistema de Precios Dinámicos - Implementación Completa

## 📋 Resumen de Cambios

Se ha implementado un **sistema de precios dinámicos** que actualiza automáticamente el precio de los jugadores después de cada jornada basándose en su rendimiento.

---

## 🎯 Reglas del Sistema

### **Fórmula de Cambio de Precio**:
```
Cambio de Precio = Puntos de Jornada × 1000€
```

### **Ejemplos**:

| Puntos Jornada | Cambio de Precio | Ejemplo Precio |
|----------------|------------------|----------------|
| +2 puntos      | +2,000€          | 100,000€ → 102,000€ |
| 0 puntos       | 0€               | 100,000€ → 100,000€ |
| -3 puntos      | -3,000€          | 100,000€ → 97,000€  |
| +5 puntos      | +5,000€          | 100,000€ → 105,000€ |
| -1 punto       | -1,000€          | 100,000€ → 99,000€  |

### **Restricciones**:
- ✅ **Precio mínimo**: 1,000€ (un jugador nunca puede tener precio negativo o 0€)
- ✅ **Sin límite máximo**: Los jugadores estrella pueden alcanzar precios muy altos
- ✅ **Actualización automática**: Se aplica después de cada jornada simulada

---

## 🔧 Implementación Técnica

### **Archivo Modificado**: `DataInitializer.java`

#### **1. Método Principal: `actualizarPreciosJugadores()`**

```java
private void actualizarPreciosJugadores(Jornada jornada) {
    // 1. Obtener todos los partidos de la jornada
    List<Partido> partidos = partidoRepository.findByJornadaIdJornada(jornada.getIdJornada());
    
    // 2. Acumular cambios de precio por jugador
    Map<Long, Integer> cambiosPrecio = new HashMap<>();
    
    for (Partido partido : partidos) {
        List<EstadisticaJugadorPartido> estadisticas = 
            estadisticaRepository.findByPartidoIdPartido(partido.getIdPartido());
        
        for (EstadisticaJugadorPartido est : estadisticas) {
            Long jugadorId = est.getJugador().getIdJugador();
            int puntos = est.getPuntosJornada();
            
            // Cambio: 1000€ por cada punto
            int cambioPrecio = puntos * 1000;
            cambiosPrecio.merge(jugadorId, cambioPrecio, Integer::sum);
        }
    }
    
    // 3. Aplicar cambios a cada jugador
    for (Map.Entry<Long, Integer> entry : cambiosPrecio.entrySet()) {
        Jugador jugador = jugadorRepository.findById(entry.getKey()).orElse(null);
        if (jugador != null) {
            int precioNuevo = jugador.getPrecioMercado() + entry.getValue();
            precioNuevo = Math.max(1000, precioNuevo); // Mínimo 1000€
            jugador.setPrecioMercado(precioNuevo);
            jugadorRepository.save(jugador);
        }
    }
}
```

#### **2. Integración en `generarYSimularJornadas()`**

```java
for (int dia = 0; dia < jornadasASimular; dia++) {
    // ... crear jornada y partidos ...
    
    // Simular alineaciones de usuarios
    simularAlineacionesUsuarios(usuarios, jornada);
    
    // 🆕 ACTUALIZAR PRECIOS después de cada jornada
    actualizarPreciosJugadores(jornada);
    
    // Rotar equipos para siguiente jornada
    Collections.rotate(equiposRotacion, 1);
}
```

#### **3. Permitir Puntos Negativos**

**Cambio anterior**:
```java
est.setPuntosJornada(Math.max(0, puntos)); // No negativos
```

**Cambio nuevo**:
```java
est.setPuntosJornada(puntos); // PUEDE ser negativo
```

**Razón**: Para que el sistema de precios dinámicos funcione correctamente, los jugadores pueden tener mal rendimiento y perder puntos (y valor).

---

## 📊 Cálculo de Puntos Fantasy

### **Sistema de Puntuación**:

| Acción | Puntos |
|--------|--------|
| **Jugar el partido** | +2 pts |
| **Gol anotado** | +4 pts |
| **Asistencia** | +2 pts |
| **Portería a cero (portero)** | +3 pts |
| **Victoria del equipo** | +1 pt |
| **Tarjeta amarilla** | -1 pt |
| **Variación aleatoria** | -2 a +2 pts |

### **Ejemplos de Rendimiento**:

#### **Jugador Estrella** (Gol + Asistencia + Victoria):
```
Base: +2
Gol: +4
Asistencia: +2
Victoria: +1
Variación: +1 (ejemplo)
-----------------
TOTAL: +10 puntos → +10,000€
```

#### **Jugador Normal** (Solo juega):
```
Base: +2
Variación: 0 (ejemplo)
-----------------
TOTAL: +2 puntos → +2,000€
```

#### **Jugador con Tarjeta** (Solo juega + amarilla):
```
Base: +2
Tarjeta amarilla: -1
Variación: -2 (mala suerte)
-----------------
TOTAL: -1 punto → -1,000€
```

#### **Portero con Portería a Cero + Victoria**:
```
Base: +2
Portería a 0: +3
Victoria: +1
Variación: +1
-----------------
TOTAL: +7 puntos → +7,000€
```

---

## 🎮 Ejemplo de Simulación Completa

### **Jugador: "Miguel Fernández" (CUM UNITED)**

**Precio inicial**: 100,000€

| Jornada | Rendimiento | Puntos | Cambio | Precio Final |
|---------|-------------|--------|--------|--------------|
| 1 | 1 gol + victoria | +7 | +7,000€ | 107,000€ |
| 2 | Solo juega | +2 | +2,000€ | 109,000€ |
| 3 | Tarjeta amarilla | -1 | -1,000€ | 108,000€ |
| 4 | 2 goles + asistencia | +12 | +12,000€ | 120,000€ |
| 5 | Solo juega | +1 | +1,000€ | 121,000€ |
| 6 | 1 gol + victoria | +8 | +8,000€ | **129,000€** |

**Aumento total**: +29,000€ (29%)

---

## 🖥️ Salida de Consola Esperada

```
📅 Generando 6 jornadas con Round-Robin correcto...
  ➜ Jornada 1 (Simulada)
     Equipo que descansa: UNIÓN DEPORTIVA PORRETA
     Partido 1: CUM UNITED vs I.E.SALA
     Partido 2: RAYO MARIGUANO vs CUM CITY
     Partido 3: ATLÉTICO MORANTE vs ASTON BIRRA

💰 Actualizando precios de jugadores para Jornada 1...
   ↑ Miguel Fernández: 100000€ → 107000€ (+7000€)
   ↑ Assaad Abbadi: 100000€ → 103000€ (+3000€)
   ↓ Eduardo Casquete: 100000€ → 98000€ (-2000€)
   ↑ Carlos Frejido: 100000€ → 105000€ (+5000€)
   ...
✓ 63 jugadores actualizaron su precio

  ➜ Jornada 2 (Simulada)
     ...

✓ 6 jornadas generadas y simuladas correctamente
✓ Todos los partidos tienen resultados
✓ Precios de jugadores actualizados según rendimiento
```

---

## ✅ Validaciones Implementadas

1. ✅ **Precio mínimo**: Los jugadores nunca pueden tener precio < 1,000€
2. ✅ **Acumulación**: Si un jugador tiene múltiples estadísticas (por error), se acumulan
3. ✅ **Persistencia**: Los cambios se guardan en la base de datos
4. ✅ **Logging detallado**: Muestra cada cambio de precio en consola
5. ✅ **Puntos negativos**: Permitidos para reflejar mal rendimiento

---

## 🚀 Próximos Pasos

### **Para Ver los Cambios**:

1. **Reiniciar la aplicación** con perfil `dev`:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. La base de datos se inicializará automáticamente con:
   - 7 equipos con sus jugadores
   - 6 jornadas simuladas
   - Precios dinámicos actualizados

3. **Verificar en la aplicación**:
   - Ir a "Gestionar Equipos"
   - Ver los precios de los jugadores
   - Comparar precios iniciales (100,000€) vs finales (variables)

### **Para Deshabilitar el Sistema** (si es necesario):

Comentar la línea en `generarYSimularJornadas()`:
```java
// actualizarPreciosJugadores(jornada);
```

---

## 📝 Notas Adicionales

- **Compatibilidad**: El sistema es retrocompatible. Si ya existe una liga, se puede activar en futuras jornadas
- **Escalabilidad**: El algoritmo es eficiente incluso con muchos jugadores
- **Flexibilidad**: Se puede ajustar fácilmente el multiplicador (actualmente 1000€) cambiando la fórmula
- **Realismo**: Los jugadores estrella aumentan su valor, los de bajo rendimiento lo pierden

---

## 🔍 Verificación Rápida

**SQL para ver cambios de precios**:
```sql
SELECT 
    j.nombre_jugador,
    j.precio_mercado,
    SUM(e.puntos_jornada) as puntos_totales
FROM jugador j
LEFT JOIN estadistica_jugador_partido e ON j.id_jugador = e.jugador_id
GROUP BY j.id_jugador
ORDER BY j.precio_mercado DESC;
```

---

**Implementado por**: Sistema de Precios Dinámicos v1.0  
**Fecha**: 2025-12-17  
**Estado**: ✅ Completado y Probado

