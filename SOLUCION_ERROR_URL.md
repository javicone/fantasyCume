# Solución al Error: "Data too long for column 'avatar_url'"

## 🔴 Problema

Al intentar crear un jugador o equipo con una URL de imagen larga, aparece el siguiente error:

```
HHH000247: ErrorCode: 1406, SQLState: 22001
Data truncation: Data too long for column 'avatar_url' at row 1
```

## ✅ Causa

Las columnas `avatar_url` (en la tabla `jugador`) y `escudo_url` (en la tabla `equipo`) estaban configuradas por defecto con un tamaño de `VARCHAR(255)`, lo cual es insuficiente para URLs largas (especialmente URLs de servicios de imágenes como Imgur, Google Drive, etc.).

## 🛠️ Solución Implementada

Se han modificado las entidades `Jugador` y `Equipo` para aumentar el tamaño de las columnas de URL a **1000 caracteres**:

### Cambios en `Jugador.java`:
```java
@Column(length = 1000)
private String avatarUrl;
```

### Cambios en `Equipo.java`:
```java
@Column(length = 1000)
private String escudoURL;
```

## 📋 Opciones para Aplicar los Cambios

### Opción 1: Reiniciar la aplicación (RECOMENDADO si no te importa perder datos)

Si tu archivo `application.properties` tiene configurado:
```properties
spring.jpa.hibernate.ddl-auto=create
```

Simplemente **reinicia la aplicación** y las tablas se recrearán automáticamente con el nuevo tamaño de columnas.

⚠️ **ADVERTENCIA**: Esto borrará todos los datos existentes.

---

### Opción 2: Ejecutar script de migración SQL (MANTIENE los datos)

Si deseas **conservar los datos existentes**, ejecuta el script SQL de migración:

1. Abre tu cliente de MySQL (MySQL Workbench, phpMyAdmin, línea de comandos, etc.)
2. Conéctate a tu base de datos
3. Ejecuta el script `MIGRACION_URL_COLUMNS.sql`:

```sql
USE fantasyCume;

ALTER TABLE jugador 
MODIFY COLUMN avatar_url VARCHAR(1000);

ALTER TABLE equipo 
MODIFY COLUMN escudo_url VARCHAR(1000);
```

4. Verifica que los cambios se aplicaron correctamente:
```sql
DESCRIBE jugador;
DESCRIBE equipo;
```

---

### Opción 3: Cambiar a modo update (desarrollo continuo)

Si estás en desarrollo activo y quieres que Hibernate actualice automáticamente el esquema sin borrar datos, cambia en `application.properties`:

```properties
# Cambiar de:
spring.jpa.hibernate.ddl-auto=create

# A:
spring.jpa.hibernate.ddl-auto=update
```

⚠️ **NOTA**: Con `update`, la próxima vez que inicies la aplicación, Hibernate actualizará automáticamente las columnas. Sin embargo, este modo NO es recomendado para producción.

---

## ✨ Resultado

Ahora puedes:
- ✅ Insertar URLs de hasta **1000 caracteres** para avatares de jugadores
- ✅ Insertar URLs de hasta **1000 caracteres** para escudos de equipos
- ✅ Usar URLs de servicios como:
  - Imgur: `https://i.imgur.com/xxxxxxxxxxxxx.png`
  - Google Drive: `https://drive.google.com/uc?export=view&id=xxxxxxxxxxxxxxxxxxxx`
  - URLs de CDNs con parámetros largos
  - Etc.

---

## 🎯 Pasos Siguientes

1. **Elige una de las 3 opciones** anteriores según tus necesidades
2. Reinicia la aplicación Spring Boot
3. Intenta crear un nuevo jugador o equipo con una URL larga
4. ✅ Debería funcionar sin errores

---

## 📞 Soporte

Si después de aplicar estos cambios sigues teniendo problemas:
1. Verifica que la base de datos esté usando la estructura actualizada con `DESCRIBE jugador;` y `DESCRIBE equipo;`
2. Asegúrate de que la aplicación se haya reiniciado completamente
3. Revisa los logs de la aplicación para ver si hay otros errores

---

**Última actualización:** 2025-11-29
**Versión:** 1.0

