-- ============================================
-- Script de Migración: Añadir numeroJornada
-- ============================================
-- Fecha: 2025-01-29
-- Descripción: Añade el campo numeroJornada a la tabla Jornada
-- ============================================

-- 1. Añadir la columna numeroJornada
ALTER TABLE jornada ADD COLUMN numero_jornada INT;

-- 2. Actualizar jornadas existentes con números secuenciales por liga
-- Esto asigna números 1, 2, 3, etc. a las jornadas de cada liga

-- Para MySQL 8.0+
UPDATE jornada j1
SET numero_jornada = (
    SELECT COUNT(*)
    FROM jornada j2
    WHERE j2.liga_id = j1.liga_id
    AND j2.id_jornada <= j1.id_jornada
);

-- Verificar el resultado
SELECT id_jornada, liga_id, numero_jornada
FROM jornada
ORDER BY liga_id, id_jornada;

-- 3. (Opcional) Hacer la columna NOT NULL después de la migración
-- ALTER TABLE jornada MODIFY COLUMN numero_jornada INT NOT NULL;

-- ============================================
-- Fin del script
-- ============================================

