# ⚽ Fantasy Fútbol Sala – Centro Universitario de Mérida  

<div align="center">
  <img src="imagenes/logo.png" alt="Logo de la aplicación" width="300"/>
  
  ### **Convierte a tus compis en leyendas**
</div>

---

## 👨‍💻 Desarrolladores

<table>
  <tr>
    <td align="center">
      <img src="imagenes/JCR.jpg" width="150" alt="Javier Conejero"/><br/>
      <b>Javier Conejero Rodríguez</b><br/>
      DNI: 09208376G
    </td>
    <td align="center">
      <img src="imagenes/MCC.jpg" width="150" alt="Miguel Cendrero"/><br/>
      <b>Miguel Cendrero Calderón</b><br/>
      DNI: 09217200L
    </td>
  </tr>
</table>

---

## 📋 Descripción del Proyecto

Aplicación web que permite a los usuarios competir entre ellos por ser el manager con mejor ojo de la universidad. Cada manager debe realizar una alineación por jornada seleccionando **5 jugadores** de toda la liga, ajustándose a un **presupuesto limitado**, para obtener la máxima puntuación posible.

### 📊 Criterios de Puntuación
- **Goles anotados** y **Asistencias**
- **Tarjetas** (amarillas y rojas)
- **Minutos disputados**
- **Portería a cero** (solo porteros)

### 👨‍💼 Rol de Administrador
Los organizadores de la liga pueden:
- Gestionar equipos y jugadores
- Actualizar resultados y estadísticas
- Generar calendarios automáticamente (ida y vuelta)
- Configurar presupuesto y reiniciar la liga

---

## ⚡ Inicio Rápido

### Requisitos Previos
- Java 17+
- Maven
- MySQL 8.0+ (o Docker)
- (Opcional) API key de [OpenRouter](https://openrouter.ai/) para funcionalidad de IA

### Instalación

1. **Clonar el repositorio**
```bash
git clone [URL_DEL_REPOSITORIO]
cd fantasyCume
```

2. **Configurar base de datos con Docker**
```bash
docker run -d --name fantasyCumeDB \
  -e MYSQL_DATABASE=fantasyCume \
  -e MYSQL_USER=admin \
  -e MYSQL_PASSWORD=1234 \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -p 3306:3306 \
  mysql:latest
```

3. **Configurar application.properties**
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/fantasyCume
spring.datasource.username=admin
spring.datasource.password=1234

# Perfil de desarrollo (carga datos de prueba)
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=create-drop

# (Opcional) IA - OpenRouter API
openrouter.api.key=sk-or-v1-TU_CLAVE_AQUI
```

4. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

5. **Acceder a la aplicación**
```
http://localhost:8080
```

### 👤 Usuarios de Prueba

| Usuario | Email | Password | Rol |
|---------|-------|----------|-----|
| Ibai Llanos | ibai@fantasy.com | pass123 | USER |
| ElRubius | rubius@fantasy.com | pass123 | USER |
| DJMaRiiO | djmario@fantasy.com | pass123 | USER |

---

## ✨ Funcionalidades Principales

### 🏆 Para Managers (Usuarios)
- **Crear Alineaciones**: Selecciona 5 jugadores (1 portero + 4 de campo) respetando el presupuesto
- **Ver Ranking**: Consulta tu posición en la clasificación general
- **Historial**: Revisa tus alineaciones y puntos de jornadas anteriores
- **Estadísticas**: Analiza el rendimiento de jugadores y equipos
- **SugerIA**: Obtén recomendaciones de alineación con inteligencia artificial

### 👨‍💼 Para Administradores
- **Gestión de Equipos y Jugadores**: Alta, baja y modificación con URLs personalizadas
- **Agregar Resultados**: Registra marcadores y estadísticas individuales por partido
- **Configuración**: Ajusta el presupuesto máximo de la liga
- **Reiniciar Liga**: Resetea resultados manteniendo equipos y jugadores
- **Validaciones Inteligentes**: 
  - No se pueden eliminar equipos/jugadores si hay resultados registrados
  - Mensaje claro: "Para eliminar, primero debes reiniciar la liga"

---

## 🤖 Alineación SugerIA (IA)

Sistema de recomendaciones personalizado usando **Nemotron 70B Instruct** vía OpenRouter.

### Características
- ✅ Análisis completo de todos los jugadores de la liga
- ✅ Recomendación óptima respetando el presupuesto
- ✅ Justificación detallada de cada selección
- ✅ Saludo personalizado al manager
- ✅ Regenerable para obtener nuevas sugerencias

### Configuración
1. Obtén tu API key en [OpenRouter](https://openrouter.ai/)
2. Agrégala en `application.properties`:
```properties
openrouter.api.key=sk-or-v1-TU_CLAVE_AQUI
openrouter.api.url=https://openrouter.ai/api/v1/chat/completions
openrouter.model=nvidia/llama-3.1-nemotron-70b-instruct
```

### Uso
1. Inicia sesión como usuario
2. Haz clic en **"Alineación SugerIA"** en el menú lateral 🤖
3. Espera unos segundos mientras la IA analiza
4. Revisa la recomendación y razonamiento
5. Puedes regenerar si deseas otra opción

---

## 🔐 Seguridad

Sistema completo basado en **Spring Security 6.4**:
- ✅ Autenticación por sesión HTTP
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Roles: `ROLE_USER` y `ROLE_ADMIN`
- ✅ Protección de rutas según rol
- ✅ Página de error personalizada
- ✅ Gestión de sesiones con logout

---

## 🏗️ Arquitectura

### Tecnologías Backend
- **Spring Boot 4.0.0**
- **Spring Security 6.4**
- **Spring Data JPA**
- **Hibernate 7.1.8**
- **MySQL 9.5**

### Tecnologías Frontend
- **Thymeleaf 3.1.3**
- **Bootstrap 5.3.0**
- **Bootstrap Icons 1.11.0**
- **JavaScript ES6+**
- **Fetch API**

### Modelo de Datos

```
LigaCume
  ├── Equipos
  │     └── Jugadores
  ├── Usuarios (Managers)
  │     └── Alineaciones
  └── Jornadas
        └── Partidos
              └── EstadisticasJugadorPartido
```

### Entidades Principales

| Entidad | Descripción |
|---------|-------------|
| **LigaCume** | Liga con nombre y presupuesto máximo |
| **Equipo** | Equipos con escudo y nombre |
| **Jugador** | Jugadores con precio, posición y avatar |
| **Usuario** | Managers con puntos acumulados y rol |
| **Jornada** | Jornadas del calendario |
| **Partido** | Partidos con marcadores |
| **EstadisticaJugadorPartido** | Stats individuales (goles, asistencias, etc.) |
| **Alineacion** | Alineaciones de usuarios por jornada |

**Diagrama ER Completo:**  
![Diagrama ER](imagenes/diagrama.jpeg)

---

## 🧪 Testing

El proyecto incluye suites de testing completas:

### UseCasesTest
Valida todas las funcionalidades del pliego de condiciones:
- ✅ Gestión de equipos (CRUD completo)
- ✅ Gestión de jugadores (CRUD completo)
- ✅ Gestión de partidos y resultados
- ✅ Estadísticas de jugadores
- ✅ Sistema de alineaciones
- ✅ Cálculo de puntuaciones
- ✅ Rankings y clasificaciones

### GenericUseCaseTest
Simula un caso de uso completo de liga:
- 3 usuarios compitiendo
- 2 equipos con 6 jugadores
- 3 jornadas con partidos
- Alineaciones y estadísticas
- Ranking final

### CRUDTests
Valida operaciones básicas de todas las entidades en base de datos.

**Ejecutar tests:**
```bash
mvn test
```

---

## 📦 Datos de Prueba

El proyecto incluye `DataInitializer` que carga automáticamente:
- 1 Liga completa
- 3 Usuarios (Ibai, ElRubius, DJMaRiiO)
- 4 Equipos (Real Madrid, Barcelona, Atlético, Sevilla)
- 20 Jugadores (5 por equipo)
- 2 Jornadas con partidos
- 6 Alineaciones de ejemplo
- 22+ Estadísticas

**Activar/Desactivar:**
```properties
# Activar datos de prueba
spring.profiles.active=dev

# Desactivar datos de prueba
# spring.profiles.active=dev
```

---

## 🎨 Características de UI/UX

- ✅ **Diseño Responsive**: Adaptado a móvil, tablet y desktop
- ✅ **Menú Lateral Dinámico**: Con gradientes y animaciones
- ✅ **Tablas Modernas**: Con hover effects y badges
- ✅ **Modales Interactivos**: Para edición rápida
- ✅ **Validaciones en Cliente**: Feedback inmediato al usuario
- ✅ **Página de Error Personalizada**: Con animaciones CSS

---

## 📝 Configuración de Producción

Para desplegar en producción:

```properties
# application.properties

# Desactivar datos de prueba
# spring.profiles.active=dev

# Mantener esquema existente
spring.jpa.hibernate.ddl-auto=update

# Configurar base de datos de producción
spring.datasource.url=jdbc:mysql://[HOST]:[PUERTO]/fantasyCume
spring.datasource.username=[USUARIO]
spring.datasource.password=[PASSWORD]

# (Opcional) Configurar IA
openrouter.api.key=sk-or-v1-[TU_CLAVE]
```

---

## 🔧 Solución de Problemas

### Error: "No se puede eliminar el equipo/jugador"
**Causa**: La liga tiene resultados registrados  
**Solución**: Ve a **Opciones Admin → Reiniciar Liga** primero

### Error: "API key not found" (IA)
**Causa**: No has configurado la API key de OpenRouter  
**Solución**: Agrega tu clave en `application.properties`

### Error de Conexión a Base de Datos
**Causa**: MySQL no está ejecutándose  
**Solución**: Inicia el contenedor Docker:
```bash
docker start fantasyCumeDB
```

---

## 🚀 Futuras Mejoras

- 📱 Aplicación móvil nativa
- 🏪 Mercado de fichajes entre usuarios
- 📊 Estadísticas avanzadas con gráficos
- 🎮 Modo torneo eliminatorio
- 🔔 Notificaciones push de resultados
- 💬 Chat entre managers
- 🏅 Sistema de logros y badges

---

## 📄 Licencia

Proyecto académico - Centro Universitario de Mérida - Universidad de Extremadura

---

## 📞 Contacto

¿Preguntas o sugerencias? Contacta con los desarrolladores:
- Javier Conejero Rodríguez
- Miguel Cendrero Calderón

