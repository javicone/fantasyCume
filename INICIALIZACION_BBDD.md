# 🗄️ Script de Inicialización de Base de Datos

## 📋 Descripción

Este script (`DataInitializer.java`) puebla automáticamente la base de datos con datos iniciales para desarrollo y pruebas.

## 🎯 Datos que Crea

El script crea una liga completa con:

- **1 Liga**: "LigaCume Fantasy 2024-2025"
- **3 Usuarios**: Ibai Llanos, ElRubius, DJMaRiiO
- **4 Equipos**: Real Madrid, FC Barcelona, Atlético Madrid, Sevilla FC
- **20 Jugadores**: 5 por equipo (1 portero + 4 de campo)
- **2 Jornadas** completas
- **4 Partidos**: 2 por jornada
- **6 Alineaciones**: 3 usuarios × 2 jornadas
- **22+ Estadísticas**: Estadísticas detalladas por jugador y partido

## 🚀 Cómo Usar

### Opción 1: Activar con Perfil de Desarrollo (Recomendado)

1. **Editar `src/main/resources/application.properties`**:
   ```properties
   # Activar perfil de desarrollo para cargar datos iniciales
   spring.profiles.active=dev
   ```

2. **Iniciar la aplicación**:
   - El script se ejecutará automáticamente al iniciar
   - Verás en consola el proceso de creación de datos
   - Solo se ejecuta si la base de datos está vacía

### Opción 2: Desactivar el Script

Para desactivar la inicialización automática:

1. **Eliminar o comentar la línea** en `application.properties`:
   ```properties
   # spring.profiles.active=dev
   ```

2. O **cambiar a otro perfil**:
   ```properties
   spring.profiles.active=prod
   ```

### Opción 3: Ejecutar Manualmente

Si prefieres no usar perfiles, puedes:

1. **Comentar la anotación** `@Profile("dev")` en `DataInitializer.java`
2. El script se ejecutará siempre al iniciar la aplicación

## 🔍 Verificación de Datos Existentes

El script incluye una verificación automática:
```java
if (ligaCumeRepository.count() > 0) {
    System.out.println("⚠️  La base de datos ya contiene datos. Saltando inicialización.");
    return;
}
```

**Esto significa que:**
- ✅ Solo se ejecuta si la base de datos está **vacía**
- ✅ No duplicará datos si ya existen
- ✅ Seguro para re-ejecutar la aplicación

## 📊 Datos Creados en Detalle

### Liga
- **Nombre**: LigaCume Fantasy 2024-2025
- **Presupuesto**: 100.000.000€

### Usuarios
| Usuario | Email | Password | Puntos |
|---------|-------|----------|--------|
| Ibai Llanos | ibai@fantasy.com | pass123 | Variable |
| ElRubius | rubius@fantasy.com | pass123 | Variable |
| DJMaRiiO | djmario@fantasy.com | pass123 | Variable |

### Equipos y Jugadores

#### Real Madrid
1. Courtois (Portero) - 40M€
2. Vinicius Jr - 80M€
3. Bellingham - 100M€
4. Rodrygo - 60M€
5. Valverde - 70M€

#### FC Barcelona
1. Ter Stegen (Portero) - 30M€
2. Lewandowski - 45M€
3. Gavi - 90M€
4. Pedri - 80M€
5. Raphinha - 50M€

#### Atlético Madrid
1. Oblak (Portero) - 35M€
2. Griezmann - 40M€
3. Morata - 25M€
4. Koke - 15M€
5. De Paul - 30M€

#### Sevilla FC
1. Bounou (Portero) - 20M€
2. Ocampos - 25M€
3. En-Nesyri - 20M€
4. Rakitic - 10M€
5. Acuña - 15M€

### Jornadas

#### Jornada 1
- **Partido 1**: Real Madrid 2-1 FC Barcelona
- **Partido 2**: Atlético Madrid 3-0 Sevilla FC

#### Jornada 2
- **Partido 1**: FC Barcelona 1-1 Atlético Madrid
- **Partido 2**: Sevilla FC 0-4 Real Madrid

## 📝 Salida en Consola

Al ejecutarse, verás algo como:

```
╔═══════════════════════════════════════════════════════════════╗
║          INICIALIZANDO BASE DE DATOS                          ║
╚═══════════════════════════════════════════════════════════════╝

📋 PASO 1: Creando Liga...
✓ Liga creada: LigaCume Fantasy 2024-2025
  Presupuesto máximo: 100000000€

👥 PASO 2: Creando 3 usuarios...
✓ Usuario: Ibai Llanos
✓ Usuario: ElRubius
✓ Usuario: DJMaRiiO

⚽ PASO 3: Creando 4 equipos...
  ✓ Real Madrid
  ✓ FC Barcelona
  ✓ Atlético Madrid
  ✓ Sevilla FC
✓ Equipos creados correctamente

...

╔═══════════════════════════════════════════════════════════════╗
║                    CLASIFICACIÓN FINAL                        ║
╚═══════════════════════════════════════════════════════════════╝

🥇 1. ElRubius              XX puntos
🥈 2. Ibai Llanos           XX puntos
🥉 3. DJMaRiiO              XX puntos

📊 RESUMEN DE DATOS CREADOS:
  • Ligas: 1
  • Usuarios: 3
  • Equipos: 4
  • Jugadores: 20
  • Jornadas: 2
  • Partidos: 4
  • Alineaciones: 6
  • Estadísticas: 22

╔═══════════════════════════════════════════════════════════════╗
║           ✅ BASE DE DATOS INICIALIZADA CORRECTAMENTE         ║
╚═══════════════════════════════════════════════════════════════╝
```

## 🔧 Configuración de Base de Datos

Asegúrate de tener configurado en `application.properties`:

```properties
# Configuración de la base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/fantasycume
spring.datasource.username=root
spring.datasource.password=tu_password

# Hibernate
spring.jpa.hibernate.ddl-auto=create-drop  # Para desarrollo
# spring.jpa.hibernate.ddl-auto=update     # Para producción

# Mostrar SQL en consola (opcional)
spring.jpa.show-sql=true
```

## ⚠️ Importante

### Para Desarrollo
```properties
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=create-drop
```
- ✅ Recrea las tablas en cada inicio
- ✅ Carga datos automáticamente
- ⚠️ **PIERDE TODOS LOS DATOS al reiniciar**

### Para Producción
```properties
# spring.profiles.active=dev  # COMENTADO
spring.jpa.hibernate.ddl-auto=update
```
- ✅ Mantiene los datos existentes
- ✅ Solo actualiza el esquema si es necesario
- ✅ NO ejecuta el script de inicialización

## 🎯 Casos de Uso

### Caso 1: Primera Vez
1. Base de datos vacía
2. Script se ejecuta
3. Crea todos los datos

### Caso 2: Base de Datos con Datos
1. Base de datos tiene ligas
2. Script detecta datos existentes
3. **NO hace nada** (evita duplicados)

### Caso 3: Resetear Datos
1. Eliminar manualmente las tablas o base de datos
2. Reiniciar la aplicación
3. Script crea los datos de nuevo

## 📁 Ubicación del Archivo

```
src/main/java/com/example/Liga_Del_Cume/data/DataInitializer.java
```

## 🔍 Métodos Auxiliares

El script incluye métodos auxiliares para:
- `crearUsuario()` - Crear y guardar usuarios
- `crearEquipo()` - Crear y guardar equipos
- `crearJugador()` - Crear y guardar jugadores
- `crearPartido()` - Crear y guardar partidos
- `crearEstadistica()` - Crear estadísticas de jugadores
- `crearAlineacion()` - Crear alineaciones con cálculo automático de puntos
- `calcularPuntosAlineacion()` - Calcular puntos según estadísticas
- `actualizarPuntosUsuario()` - Actualizar puntos acumulados de usuarios
- `mostrarResultadosJornada()` - Mostrar puntuaciones por jornada

## ✅ Ventajas de este Enfoque

1. ✅ **Automático**: Se ejecuta al iniciar la aplicación
2. ✅ **Seguro**: Verifica datos existentes antes de ejecutar
3. ✅ **Controlable**: Se activa/desactiva con perfiles
4. ✅ **Informativo**: Muestra progreso en consola
5. ✅ **Completo**: Crea todos los datos necesarios
6. ✅ **Realista**: Usa datos de equipos y jugadores reales

## 🚫 Posibles Problemas

### Error: "La base de datos ya contiene datos"
**Solución**: Esto es normal. El script no sobrescribe datos existentes. Para cargar datos frescos:
1. Elimina las tablas manualmente
2. O usa `spring.jpa.hibernate.ddl-auto=create-drop`

### Error: "No se puede conectar a la base de datos"
**Solución**: Verifica:
1. MySQL está ejecutándose
2. La base de datos `fantasycume` existe
3. Credenciales correctas en `application.properties`

### Error: "Table doesn't exist"
**Solución**: Usa `spring.jpa.hibernate.ddl-auto=create-drop` o `update`

## 📞 Soporte

Si tienes problemas, verifica:
1. ✅ Perfil `dev` está activo
2. ✅ Base de datos está vacía (o usa `create-drop`)
3. ✅ Credenciales de base de datos son correctas
4. ✅ MySQL está ejecutándose
5. ✅ La base de datos `fantasycume` existe

---

**Nota**: Este script está diseñado **SOLO para desarrollo y pruebas**. NO usar en producción con datos reales.

