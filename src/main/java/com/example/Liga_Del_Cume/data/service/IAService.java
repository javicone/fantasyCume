package com.example.Liga_Del_Cume.data.service;

import com.example.Liga_Del_Cume.data.model.*;
import com.example.Liga_Del_Cume.data.repository.JugadorRepository;
import com.example.Liga_Del_Cume.data.repository.LigaCumeRepository;
import com.example.Liga_Del_Cume.data.repository.EstadisticaJugadorPartidoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de Inteligencia Artificial para recomendación de alineaciones
 * Utiliza OpenRouter API con el modelo Nemotron para analizar jugadores
 * y recomendar la mejor alineación posible dentro del presupuesto disponible
 */
@Service
@Transactional
public class IAService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    private final JugadorRepository jugadorRepository;
    private final LigaCumeRepository ligaRepository;
    private final EstadisticaJugadorPartidoRepository estadisticaRepository;

    @Autowired
    public IAService(JugadorRepository jugadorRepository,
                     LigaCumeRepository ligaRepository,
                     EstadisticaJugadorPartidoRepository estadisticaRepository) {
        this.jugadorRepository = jugadorRepository;
        this.ligaRepository = ligaRepository;
        this.estadisticaRepository = estadisticaRepository;
    }

    /**
     * Genera una recomendación de alineación usando IA
     *
     * @param ligaId ID de la liga
     * @return Respuesta de la IA con la recomendación
     * @throws Exception si hay problemas con la API de OpenRouter
     */
    public String generarRecomendacionAlineacion(Long ligaId) throws Exception {
        // Validar que la liga existe
        LigaCume liga = ligaRepository.findById(ligaId)
            .orElseThrow(() -> new RuntimeException("Liga no encontrada"));

        // Obtener presupuesto máximo
        Long presupuestoMaximo = liga.getPresupuestoMaximo();

        // Obtener todos los jugadores de la liga con sus estadísticas
        List<Jugador> todosJugadores = new ArrayList<>();
        for (Equipo equipo : liga.getEquipos()) {
            todosJugadores.addAll(equipo.getJugadores());
        }

        // Construir el contexto para la IA
        String contexto = construirContextoParaIA(todosJugadores, presupuestoMaximo);

        // Llamar a la API de OpenRouter
        return llamarOpenRouterAPI(contexto);
    }

    /**
     * Realiza la llamada HTTP a la API de OpenRouter
     *
     * @param prompt Prompt/contexto para la IA
     * @return Respuesta de la IA
     * @throws Exception si hay problemas con la API
     */
    private String llamarOpenRouterAPI(String prompt) throws Exception {
        // Validar que la API key esté configurada
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("${")) {
            throw new Exception("API key de OpenRouter no configurada. " +
                "Por favor, configura la variable de entorno OPENROUTER_API_KEY");
        }

        // Configurar timeouts para la petición (30 segundos de conexión, 120 segundos de lectura)
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);  // 30 segundos para conectar
        factory.setReadTimeout(120000);    // 120 segundos para leer la respuesta (las IAs pueden tardar)

        RestTemplate restTemplate = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "https://ligadelcume.com");
        headers.set("X-Title", "Liga Del Cume Fantasy");

        // Construir el cuerpo de la petición según la API de OpenRouter
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Eres Guardiol-IA, un asistente experto en Fantasy Fútbol Sala. " +
            "Debes recomendar la mejor alineación posible de 5 jugadores (1 portero + 4 jugadores de campo) " +
            "basándote en estadísticas y rendimiento. Siempre respeta el presupuesto máximo disponible.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("🤖 Llamando a OpenRouter API...");
            System.out.println("   Modelo: " + model);
            System.out.println("   Tamaño del prompt: " + prompt.length() + " caracteres");
            System.out.println("   Timeouts: Conexión=30s, Lectura=120s");

            long startTime = System.currentTimeMillis();

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("⏱️  Tiempo de respuesta: " + duration + "ms (" + (duration/1000) + "s)");

            String responseBody = response.getBody();
            System.out.println("📩 Respuesta recibida de OpenRouter API");
            System.out.println("   Status: " + response.getStatusCode());
            System.out.println("   Body (primeros 500 chars): " +
                (responseBody != null && responseBody.length() > 500
                    ? responseBody.substring(0, 500) + "..."
                    : responseBody));

            // Parsear la respuesta JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            // Verificar si hay un error en la respuesta
            if (root.has("error")) {
                JsonNode error = root.get("error");
                String errorMessage = error.has("message")
                    ? error.get("message").asText()
                    : "Error desconocido de la API";
                throw new Exception("Error de OpenRouter API: " + errorMessage);
            }

            // Verificar que exista el campo "choices"
            if (!root.has("choices") || root.get("choices").isEmpty()) {
                throw new Exception("La respuesta de OpenRouter API no contiene 'choices'. " +
                    "Respuesta completa: " + responseBody);
            }

            // Extraer el texto de la respuesta de forma segura
            JsonNode choices = root.get("choices");
            JsonNode firstChoice = choices.get(0);

            if (firstChoice == null) {
                throw new Exception("El array 'choices' está vacío en la respuesta de OpenRouter API");
            }

            JsonNode message = firstChoice.get("message");
            if (message == null) {
                throw new Exception("No se encontró 'message' en la respuesta de OpenRouter API");
            }

            JsonNode content = message.get("content");
            if (content == null) {
                throw new Exception("No se encontró 'content' en el mensaje de OpenRouter API");
            }

            String respuesta = content.asText();
            System.out.println("✅ Recomendación generada exitosamente (" + respuesta.length() + " caracteres)");

            return respuesta;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ Error HTTP al llamar a OpenRouter API: " + e.getStatusCode());
            System.err.println("   Body: " + e.getResponseBodyAsString());
            throw new Exception("Error HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
        } catch (org.springframework.web.client.ResourceAccessException e) {
            System.err.println("❌ Error de conexión a OpenRouter API: " + e.getMessage());
            throw new Exception("No se pudo conectar con el servicio de IA. Verifica tu conexión a Internet.");
        } catch (Exception e) {
            System.err.println("❌ Error al llamar a OpenRouter API: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al generar recomendación con IA: " + e.getMessage());
        }
    }

    /**
     * Construye el contexto completo para enviar a la IA
     *
     * @param jugadores Lista de todos los jugadores disponibles
     * @param presupuesto Presupuesto máximo disponible
     * @return String con el prompt completo para la IA
     */
    private String construirContextoParaIA(List<Jugador> jugadores, Long presupuesto) {
        StringBuilder contexto = new StringBuilder();

        // Instrucciones para la IA (más concisas)
        contexto.append("Recomienda la MEJOR alineación de 5 jugadores (1 portero + 4 campo) ");
        contexto.append("basándote en estadísticas. Presupuesto máximo: ").append(presupuesto).append("€\n\n");

        contexto.append("REGLAS:\n");
        contexto.append("1. EXACTAMENTE 1 portero + 4 jugadores\n");
        contexto.append("2. Coste total ≤ ").append(presupuesto).append("€\n");
        contexto.append("3. Prioriza mejores estadísticas (goles, asistencias, puntos)\n");
        contexto.append("4. Evita tarjetas rojas y amarillas excesivas\n\n");

        // Separar porteros y jugadores de campo
        List<Jugador> porteros = jugadores.stream()
            .filter(Jugador::isEsPortero)
            .collect(Collectors.toList());

        List<Jugador> jugadoresCampo = jugadores.stream()
            .filter(j -> !j.isEsPortero())
            .collect(Collectors.toList());

        // Se envían TODOS los jugadores sin limitaciones
        System.out.println("📊 Estadísticas del prompt:");
        System.out.println("   Total jugadores: " + jugadores.size() +
                         " (Porteros: " + porteros.size() + ", Campo: " + jugadoresCampo.size() + ")");

        // Información de porteros (formato compacto)
        contexto.append("PORTEROS:\n");
        for (Jugador portero : porteros) {
            contexto.append(formatearJugadorParaIA(portero, true));
        }

        // Información de jugadores de campo (formato compacto)
        contexto.append("\nCAMPO:\n");
        for (Jugador jugador : jugadoresCampo) {
            contexto.append(formatearJugadorParaIA(jugador, false));
        }

        // Formato de respuesta (simplificado)
        contexto.append("\nRESPUESTA (formato):\n\n");
        contexto.append("🎯 ALINEACIÓN:\n\n");
        contexto.append("PORTERO: [Nombre] - [Equipo] - [X]€ - Razón: [breve]\n\n");
        contexto.append("CAMPO:\n");
        contexto.append("1. [Nombre] - [Equipo] - [X]€ - Razón: [breve]\n");
        contexto.append("2-4. [repetir]\n\n");
        contexto.append("💰 TOTAL: [X]€ / ").append(presupuesto).append("€ | SALDO: [X]€\n\n");
        contexto.append("📊 [Análisis breve]\n");

        return contexto.toString();
    }

    /**
     * Formatea la información de un jugador para el contexto de la IA
     *
     * @param jugador Jugador a formatear
     * @param esPortero Indica si es portero
     * @return String formateado con la información del jugador
     */
    private String formatearJugadorParaIA(Jugador jugador, boolean esPortero) {
        StringBuilder info = new StringBuilder();
        Map<String, Integer> stats = calcularEstadisticasTotales(jugador);

        // Formato compacto: Nombre (Equipo) - €X | Stats
        info.append("• ").append(jugador.getNombreJugador())
            .append(" (").append(jugador.getEquipo().getNombreEquipo())
            .append(") - ").append(jugador.getPrecioMercado()).append("€ | ");

        if (esPortero) {
            info.append("GR:").append(stats.get("golesRecibidos"));
        } else {
            info.append("G:").append(stats.get("goles"))
                .append(" A:").append(stats.get("asistencias"));
        }

        info.append(" Pts:").append(stats.get("puntos"))
            .append(" TA:").append(stats.get("amarillas"));

        if (stats.get("rojas") > 0) {
            info.append(" TR:").append(stats.get("rojas"));
        }

        info.append(" PJ:").append(stats.get("partidos")).append("\n");

        return info.toString();
    }

    /**
     * Calcula las estadísticas totales de un jugador sumando todos sus partidos
     *
     * @param jugador Jugador del que calcular estadísticas
     * @return Map con las estadísticas totales
     */
    private Map<String, Integer> calcularEstadisticasTotales(Jugador jugador) {
        Map<String, Integer> totales = new HashMap<>();

        List<EstadisticaJugadorPartido> estadisticas =
            estadisticaRepository.findByJugadorIdJugador(jugador.getIdJugador());

        int goles = 0;
        int asistencias = 0;
        int amarillas = 0;
        int rojas = 0;
        int puntos = 0;
        int golesRecibidos = 0;
        int partidos = estadisticas.size();

        for (EstadisticaJugadorPartido est : estadisticas) {
            goles += est.getGolesAnotados();
            asistencias += est.getAsistencias();
            amarillas += est.getTarjetaAmarillas();
            if (est.isTarjetaRojas()) rojas++;
            puntos += est.getPuntosJornada();
            golesRecibidos += est.getGolesRecibidos();
        }

        totales.put("goles", goles);
        totales.put("asistencias", asistencias);
        totales.put("amarillas", amarillas);
        totales.put("rojas", rojas);
        totales.put("puntos", puntos);
        totales.put("golesRecibidos", golesRecibidos);
        totales.put("partidos", partidos);

        return totales;
    }

    /**
     * Método alternativo para obtener recomendación con información del usuario
     *
     * @param ligaId ID de la liga
     * @param nombreUsuario Nombre del usuario que solicita la recomendación
     * @return Respuesta personalizada de la IA
     * @throws Exception si hay problemas con la API de OpenRouter
     */
    public String generarRecomendacionPersonalizada(Long ligaId, String nombreUsuario) throws Exception {
        String recomendacion = generarRecomendacionAlineacion(ligaId);

        // Personalizar el saludo inicial
        String saludoPersonalizado = "¡Hola " + nombreUsuario + "! " +
            "Soy Guardiol-IA, tu asistente personal de Fantasy Fútbol Sala.\n\n" +
            "He analizado todos los jugadores disponibles usando IA avanzada (Nemotron) " +
            "y aquí está mi recomendación de alineación para maximizar tus puntos en la próxima jornada:\n\n";

        return saludoPersonalizado + recomendacion;
    }
}

