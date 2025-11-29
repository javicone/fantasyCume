-- ============================================================================
-- Script de migración para aumentar el tamaño de las columnas de URL
-- ============================================================================
-- Este script aumenta el tamaño de las columnas avatar_url y escudo_url
-- de VARCHAR(255) a VARCHAR(1000) para permitir URLs más largas
-- ============================================================================
-- IMPORTANTE: Ejecuta este script SOLO si deseas mantener los datos existentes
-- Si no te importa perder los datos, simplemente reinicia la aplicación
-- con spring.jpa.hibernate.ddl-auto=create
-- ============================================================================

USE fantasyCume;

-- Aumentar tamaño de la columna avatar_url en la tabla jugador
ALTER TABLE jugador
MODIFY COLUMN avatar_url VARCHAR(1000);

-- Aumentar tamaño de la columna escudo_url en la tabla equipo
ALTER TABLE equipo
MODIFY COLUMN escudo_url VARCHAR(1000);

-- Verificar los cambios
DESCRIBE jugador;
DESCRIBE equipo;

-- Mensaje de confirmación
SELECT 'Migración completada exitosamente. Las columnas de URL ahora soportan hasta 1000 caracteres.' AS mensaje;

