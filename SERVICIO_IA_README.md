# 🤖 Servicio de IA - Guardiol-IA

## Descripción

**Guardiol-IA** es un asistente inteligente integrado en la aplicación Fantasy Fútbol Sala que utiliza **OpenRouter API con el modelo Nemotron (nvidia/llama-3.1-nemotron-70b-instruct)** para recomendar la mejor alineación posible basándose en estadísticas reales de los jugadores.

---

## 📋 Características

- ✅ **Análisis completo** de todos los jugadores disponibles en la liga
- ✅ **Recomendación inteligente** de 1 portero + 4 jugadores de campo
- ✅ **Respeta el presupuesto** máximo de la liga
- ✅ **Evalúa estadísticas**: goles, asistencias, tarjetas, puntos totales
- ✅ **Explicación detallada** de por qué cada jugador es recomendado
- ✅ **Resumen económico** con costes y saldo restante

---

## 🚀 Configuración

### 1. Obtener API Key de OpenRouter

1. Ve a [OpenRouter](https://openrouter.ai/)
2. Regístrate o inicia sesión
3. Ve a la sección **"Keys"**
4. Haz clic en **"Create Key"**
5. Copia la clave generada (comienza con `sk-or-v1-...`)

### 2. Configurar la API Key en la aplicación

Abre el archivo `src/main/resources/application.properties` y configura tu clave:

```properties
# --- Configuración de OpenRouter AI ---
openrouter.api.key=sk-or-v1-TU_CLAVE_AQUI
openrouter.api.url=https://openrouter.ai/api/v1/chat/completions
openrouter.model=nvidia/llama-3.1-nemotron-70b-instruct
```

⚠️ **IMPORTANTE**: 
- Nunca subas tu API key a repositorios públicos
- OpenRouter ofrece créditos gratuitos para empezar
- Puedes cambiar el modelo si lo deseas (ej: `anthropic/claude-3.5-sonnet`, `openai/gpt-4`, etc.)

### 3. Dependencias necesarias

El servicio utiliza Spring Boot Web y Jackson (ya incluidas en el `pom.xml`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

---

## 💻 Uso

### Desde la interfaz web

1. Inicia sesión en la aplicación
2. Accede a una liga
3. En el menú lateral, haz clic en **"Alineación SugerIA"** (ícono de robot 🤖)
4. Espera unos segundos mientras la IA analiza los datos
5. Revisa la recomendación personalizada
6. Puedes regenerar la recomendación si lo deseas

### Desde el código

```java
@Autowired
private IAService iaService;

// Generar recomendación básica
String recomendacion = iaService.generarRecomendacionAlineacion(ligaId);

// Generar recomendación personalizada
String recomendacionPersonalizada = iaService.generarRecomendacionPersonalizada(
    ligaId, 
    "Nombre del Manager"
);
```

---

## 🧠 Cómo funciona

### Proceso de análisis

1. **Recopilación de datos**: El servicio obtiene todos los jugadores de la liga con sus estadísticas
2. **Construcción del contexto**: Se crea un prompt detallado con:
   - Información de todos los porteros (goles recibidos, tarjetas, puntos)
   - Información de jugadores de campo (goles, asistencias, tarjetas, puntos)
   - Presupuesto máximo disponible
   - Reglas y restricciones
3. **Consulta a la IA**: Se envía el contexto a Google Gemini
4. **Procesamiento de respuesta**: La IA devuelve una recomendación estructurada

### Criterios de evaluación de la IA

La IA considera múltiples factores:

- ✅ **Rendimiento**: Jugadores con más goles, asistencias y puntos
- ✅ **Disciplina**: Evita jugadores con muchas tarjetas
- ✅ **Porteros**: Prioriza los que menos goles reciben
- ✅ **Presupuesto**: Optimiza el uso del dinero disponible
- ✅ **Balance**: Busca un equipo equilibrado

---

## 📊 Formato de respuesta

La IA devuelve una respuesta estructurada con:

```
🎯 ALINEACIÓN RECOMENDADA

PORTERO:
• Nombre - Equipo - Precio: X€
  Razón: [Explicación]

JUGADORES DE CAMPO:
• Jugador 1 - Equipo - Precio: X€
  Razón: [Explicación]
• Jugador 2 - Equipo - Precio: X€
  Razón: [Explicación]
• Jugador 3 - Equipo - Precio: X€
  Razón: [Explicación]
• Jugador 4 - Equipo - Precio: X€
  Razón: [Explicación]

💰 RESUMEN ECONÓMICO:
Coste Total: X€
Presupuesto Disponible: X€
Saldo Restante: X€

📊 ANÁLISIS:
[Explicación de por qué esta es la mejor alineación]
```

---

## 🔧 Componentes técnicos

### IAService.java

Clase principal que gestiona la comunicación con Google AI:

- `generarRecomendacionAlineacion(Long ligaId)`: Genera recomendación básica
- `generarRecomendacionPersonalizada(Long ligaId, String nombreUsuario)`: Recomendación con saludo personalizado
- `construirContextoParaIA(...)`: Construye el prompt para la IA
- `formatearJugadorParaIA(...)`: Formatea datos de jugadores
- `calcularEstadisticasTotales(...)`: Suma estadísticas de todos los partidos

### IAController.java

Controlador REST que expone los endpoints:

- `GET /liga/{idLiga}/alineacion-sugeria`: Muestra la vista con la recomendación
- `GET /liga/{idLiga}/alineacion-sugeria/regenerar`: Regenera la recomendación

### alineacionSugerIA.html

Vista Thymeleaf que muestra la recomendación con diseño atractivo.

---

## ⚠️ Limitaciones y consideraciones

- 🔒 **Requiere conexión a Internet** para consultar la API de Google
- 💰 **Límites de uso**: Google AI tiene cuotas gratuitas y de pago
- ⏱️ **Tiempo de respuesta**: Puede tardar 2-5 segundos en generar recomendación
- 🔐 **Seguridad**: La API key debe protegerse adecuadamente

---

## 🐛 Solución de problemas

### Error: "API key not found"

**Solución**: Verifica que hayas configurado correctamente la API key en `application.properties`

### Error: "Failed to generate content"

**Posibles causas**:
- API key inválida o expirada
- Límite de cuota excedido
- Problemas de conexión a Internet

**Solución**: Verifica tu API key en [Google AI Studio](https://makersuite.google.com/app/apikey)

### La recomendación no respeta el presupuesto

**Solución**: Verifica que el presupuesto máximo esté correctamente configurado en la liga

---

## 📚 Recursos adicionales

- [Documentación oficial de OpenRouter](https://openrouter.ai/docs)
- [OpenRouter Dashboard](https://openrouter.ai/)
- [Modelos disponibles en OpenRouter](https://openrouter.ai/models)
- [Nemotron 70B Instruct](https://openrouter.ai/models/nvidia/llama-3.1-nemotron-70b-instruct)

---

## 🎯 Mejoras futuras

- [ ] Caché de recomendaciones para evitar llamadas repetidas
- [ ] Análisis de tendencias de rendimiento
- [ ] Comparación con alineaciones anteriores
- [ ] Predicción de puntos esperados
- [ ] Alertas de jugadores en racha
- [ ] Modo "conservador" vs "arriesgado"

---

## 👨‍💻 Autor

Desarrollado como parte del proyecto Fantasy Fútbol Sala - Liga Del Cume

