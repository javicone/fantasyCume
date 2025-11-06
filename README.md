# ⚽ Fantasy Fútbol Sala – Centro Universitario de Mérida  

## 📌 Logo
![Logo de la aplicación](imagenes/logo.png)  

---

## 👨‍💻 Desarrolladores
- **Javier Conejero Rodríguez** – DNI: 09208376G  
  ![Foto Javier Conejero](imagenes/JCR.jpg)  

- **Miguel Cendrero Calderón** – DNI: 09217200L  
  ![Foto Miguel Cendrero](imagenes/MCC.jpg)  

---

## 🏆 Eslogan
**Convierte a tus compis en leyendas**  

---


## 📋 Descripción
Aplicación web que permite a los usuarios competir entre ellos por ser el manager con mejor ojo de la universidad.
Cada manager deberá realizar una alineación para cada jornada eligiendo los 5 jugadores de toda la liga que mejor partido consideran que harán. 
Cada jornada cada manager tendrá el mismo presupuesto limitado y deberá combinar en cada jornada los 5 jugadores de la liga que mejor jugarán
para obtener la máxima puntuación posible sin sobrepasar su presupuesto. 

Cada manager deberá:  
- Realizar una alineación para cada jornada.  
- Seleccionar **5 jugadores** de toda la liga que mejor rendimiento puedan tener.  
- Ajustarse a un **presupuesto limitado** por jornada.  

### 📊 Criterios de puntuación
- **Goles anotados**  
- **Asistencias dadas**  
- **Tarjetas amarillas**  
- **Tarjetas rojas**  
- **Minutos disputados**  
- **Portería a cero (solo porteros)**  

### 👨‍💼 Rol de administrador
Los administradores serán tambien los **propios usuarios** (organizadores de la liga) serán responsables de:  
- Dar de alta equipos y jugadores.  
- Actualizar los resultados de cada jornada.  
- Gestionar las estadísticas de jugadores.  
- Generar automáticamente el calendario de enfrentamientos (ida y vuelta).  

---

## ✅ Funcionalidades, Requisitos y Pliego de Condiciones  

### Funcionalidad 1. Gestión de equipos  
- [ ] 1.1 Agregar equipos
- [ ] 1.2 Modificar equipos
- [ ] 1.3 Eliminar equipos
- [ ] 1.4 Listar equipos

### Funcionalidad 2. Gestión de jugadores  
- [ ] 2.1 Agregar jugadores a un equipo
- [ ] 2.2 Actualizar información de jugadores
- [ ] 2.3 Eliminar jugadores de un equipo
- [ ] 2.4 Listar jugadores

### Funcionalidad 3. Gestión de partidos  
- [ ] 3.1 Agregar resultados de partidos por jornada
- [ ] 3.2 Modificar resultados de partidos por jornada

### Funcionalidad 4. Estadísticas de jugadores  
- [ ] 4.1 Añadir estadísticas de jugadores por partido
- [ ] 4.2 Modificar estadísticas de jugadores por partido

### Funcionalidad 5. Calendario de enfrentamientos  
- [ ] 5.1 Generar cuadro de enfrentamientos por jornada

### Funcionalidad 6. Crear alineacion para la jornada  
- [ ] 6.1 Listar jugadores disponibles por posición
- [ ] 6.2 Seleccionar jugador por posición
- [ ] 6.3 Consultar equipo alineado

### Funcionalidad 7. Consultar estadísticas generales de jugadores 
- [ ] 7.1 Buscar jugador (filtrar por nombre, puntos o equipo)

### Funcionalidad 8. Ver clasificación general  
- [ ] Consultar ranking de todos los usuarios

### Funcionalidad 9. Ver Resultados  
- [ ] Ver resultados de partidos disputados

### Funcionalidad 10. Puntuaciones de usuarios por jornada
- [ ] Consultar puntuación total de la jornada.
- [ ] Mostrar jugadores seleccionados y sus respectivas puntuaciones.

---

## 🌟 Funcionalidades Opcionales, Recomendables o Futuribles  

### **Opcional 1.** Implementar login diferenciado entre **usuario** y **administrador** de forma que se diferencie la lógica de jugador y de administrador.

### **Opcional 2.** Sistema recomendador de jugadores usando **IA**  

### **Opcional 3.**  Implementar otro modo de juego fantasy que incluya un mercado diario y los jugadores compitan por adquirir los jugadores para sus equipos. (Esta lógica se denomina Liga Fantasy, en nuestra aplicación es desarrollada un modo de juego similar al de liga fantastica)


---

## Diagrama entidad-relación de la base de datos
![Diagrama ER](imagenes/diagrama.jpeg)  

---



## 🏗️ Arquitectura y Modelo de Datos

### 📦 Entidades del Sistema

#### **LigaCume** (Liga Principal)
- `idLigaCume` (Long) - ID autoincremental
- `nombreLiga` (String) - Nombre de la liga
- `presupuestoMaximo` (Long) - Presupuesto máximo por jornada
- Relaciones: OneToMany con Equipo, Usuario, Jornada

#### **Equipo**
- `idEquipo` (Long) - ID autoincremental
- `nombreEquipo` (String) - Nombre del equipo
- `escudoURL` (String) - URL del escudo
- `liga` (LigaCume) - Liga a la que pertenece
- Relaciones: ManyToOne con LigaCume, OneToMany con Jugador

#### **Jugador**
- `idJugador` (Long) - ID autoincremental
- `nombreJugador` (String) - Nombre del jugador
- `esPortero` (boolean) - Indica si es portero
- `precioMercado` (float) - Precio de mercado
- `equipo` (Equipo) - Equipo al que pertenece
- Relaciones: ManyToOne con Equipo, OneToMany con EstadisticaJugadorPartido, ManyToMany con Alineacion

#### **Usuario**
- `idUsuario` (Long) - ID autoincremental
- `nombreUsuario` (String) - Nombre del usuario/manager
- `puntosAcumulados` (int) - Puntos totales acumulados
- `liga` (LigaCume) - Liga en la que participa
- Relaciones: ManyToOne con LigaCume, OneToMany con Alineacion

#### **Jornada**
- `idJornada` (Long) - ID autoincremental
- `liga` (LigaCume) - Liga a la que pertenece
- Relaciones: ManyToOne con LigaCume, OneToMany con Partido y Alineacion

#### **Partido**
- `idPartido` (Long) - ID autoincremental
- `jornada` (Jornada) - Jornada del partido
- `equipoLocal` (Equipo) - Equipo local
- `equipoVisitante` (Equipo) - Equipo visitante
- `golesLocal` (Integer) - Goles del equipo local
- `golesVisitante` (Integer) - Goles del equipo visitante
- Relaciones: ManyToOne con Jornada, Equipo

#### **EstadisticaJugadorPartido**
- `idEstadistica` (Long) - ID autoincremental
- `jugador` (Jugador) - Jugador asociado
- `partido` (Partido) - Partido asociado
- `golesAnotados` (int) - Goles anotados
- `asistencias` (int) - Asistencias realizadas
- `tarjetaAmarillas` (int) - Tarjetas amarillas
- `tarjetaRojas` (boolean) - Tarjeta roja
- `minMinutosJugados` (boolean) - Mínimo de minutos jugados
- `golesRecibidos` (int) - Goles recibidos (solo porteros)
- `puntosJornada` (int) - Puntos obtenidos en la jornada
- Relaciones: ManyToOne con Jugador y Partido

#### **Alineacion**
- `idAlineacion` (Long) - ID autoincremental
- `usuario` (Usuario) - Usuario/manager propietario
- `jornada` (Jornada) - Jornada de la alineación
- `jugadores` (List<Jugador>) - Lista de 5 jugadores seleccionados
- `puntosTotalesJornada` (int) - Puntos totales de la alineación
- Relaciones: ManyToOne con Usuario y Jornada, ManyToMany con Jugador

### 🗄️ Repositorios (Spring Data JPA)

Cada entidad cuenta con su repositorio con métodos personalizados:

- **LigaCumeRepository** - Operaciones sobre ligas
- **EquipoRepository** - Búsqueda de equipos por nombre y liga
- **JugadorRepository** - Consultas avanzadas:
    - Búsqueda por nombre, equipo, posición
    - Ordenamiento por precio y puntos
    - Ranking de jugadores y porteros
- **UsuarioRepository** - Gestión de usuarios y rankings
- **JornadaRepository** - Operaciones sobre jornadas
- **PartidoRepository** - Consulta de partidos por jornada y equipos
- **EstadisticaJugadorPartidoRepository** - Estadísticas por jugador/partido
- **AlineacionRepository** - Gestión de alineaciones por usuario/jornada

### 🔄 Inicialización de Datos
**DataInitializer.java** - Carga datos iniciales de prueba al arrancar la aplicación

---

## 🧪 Testing
Aplicación basada en la *Liga Fantasy*, orientada a la liga de fútbol sala del **Centro Universitario de Mérida**.
---



## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - ORM y gestión de persistencia
- **Hibernate** - Implementación JPA
- **MySQL** - Base de datos relacional

### Testing
- **JUnit 5** - Framework de testing
- **Spring Boot Test** - Tests de integración
- **Maven** - Gestión de dependencias y construcción

### Configuración
- **application.properties** - Configuración de la aplicación
  - Conexión a MySQL: `jdbc:mysql://localhost:3306/fantasycume`
  - Usuario: `root`
  - DDL auto: `create-drop` (recreación automática del esquema)

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

### Configuración de Base de Datos
1. Crear base de datos en MySQL:
```sql
CREATE DATABASE fantasycume;
```

2. Actualizar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/fantasycume
spring.datasource.username=root
spring.datasource.password=tu_password
```

### Ejecutar la Aplicación
```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### Ejecutar Tests
```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UseCasesTest
mvn test -Dtest=CRUDTests
mvn test -Dtest=GenericUseCaseTest
```

---

## 📊 Estado del Proyecto

✅ **Completado:**
- Modelo de datos completo
- Repositorios con consultas personalizadas
- Suite completa de tests (casos de uso, CRUD, integración)
- Inicializador de datos de prueba
- Gestión completa de ligas, equipos, jugadores, partidos y estadísticas
- Sistema de alineaciones y rankings

🚧 **En desarrollo:**
- Interfaz web (vistas y controladores)
- Sistema de autenticación y autorización
- API REST

💡 **Futuro:**
- Sistema recomendador con IA
- Modo de juego con mercado diario
- Notificaciones en tiempo real

---

## 📝 Notas de Desarrollo

### Consideraciones Importantes
- El sistema calcula automáticamente los puntos basándose en las estadísticas de los jugadores
- Cada alineación debe tener exactamente 5 jugadores (1 portero + 4 de campo)
- El presupuesto máximo por jornada está definido en la liga
- Las estadísticas se registran por partido y jugador
- Los rankings se actualizan automáticamente con los puntos de cada jornada

### Reglas de Puntuación (ejemplos en tests)
- **Goles**: +5 puntos por gol
- **Asistencias**: +3 puntos por asistencia
- **Tarjeta amarilla**: -1 punto
- **Tarjeta roja**: -3 puntos
- **Minutos jugados**: +2 puntos (si supera el mínimo)
- **Portería a cero** (porteros): +5 puntos
- **Goles recibidos** (porteros): -1 punto por gol

---

## 👥 Contribuciones

Este proyecto es parte del trabajo de desarrollo de la asignatura de **Metodologías de Desarrollo de Aplicaciones para Internet** del Centro Universitario de Mérida.

---

## 📄 Licencia

Proyecto académico - Centro Universitario de Mérida - Universidad de Extremadura

