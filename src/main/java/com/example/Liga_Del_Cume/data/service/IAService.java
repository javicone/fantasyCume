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
        RestTemplate restTemplate = new RestTemplate();
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
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Parsear la respuesta JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // Extraer el texto de la respuesta
            String respuesta = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

            return respuesta;

        } catch (Exception e) {
            System.err.println("Error al llamar a OpenRouter API: " + e.getMessage());
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

        // Instrucciones para la IA
        contexto.append("Eres Guardiol-IA, un asistente experto en Fantasy Fútbol Sala. ");
        contexto.append("Tu objetivo es recomendar la MEJOR alineación posible de 5 jugadores ");
        contexto.append("(1 portero + 4 jugadores de campo) basándote en las estadísticas y rendimiento.\n\n");

        contexto.append("REGLAS OBLIGATORIAS:\n");
        contexto.append("1. La alineación DEBE tener EXACTAMENTE 1 PORTERO y 4 JUGADORES DE CAMPO\n");
        contexto.append("2. El coste total NO puede superar el presupuesto: ").append(presupuesto).append("€\n");
        contexto.append("3. Prioriza jugadores con MEJORES estadísticas (más goles, asistencias, puntos)\n");
        contexto.append("4. Evita jugadores con tarjetas rojas o muchas amarillas\n");
        contexto.append("5. Para porteros, prioriza los que menos goles reciben\n\n");

        // Separar porteros y jugadores de campo
        List<Jugador> porteros = jugadores.stream()
            .filter(Jugador::isEsPortero)
            .collect(Collectors.toList());

        List<Jugador> jugadoresCampo = jugadores.stream()
            .filter(j -> !j.isEsPortero())
            .collect(Collectors.toList());

        // Información de porteros
        contexto.append("========== PORTEROS DISPONIBLES ==========\n");
        for (Jugador portero : porteros) {
            contexto.append(formatearJugadorParaIA(portero, true));
        }

        // Información de jugadores de campo
        contexto.append("\n========== JUGADORES DE CAMPO DISPONIBLES ==========\n");
        for (Jugador jugador : jugadoresCampo) {
            contexto.append(formatearJugadorParaIA(jugador, false));
        }

        // Instrucciones finales
        contexto.append("\n========== FORMATO DE RESPUESTA ==========\n");
        contexto.append("Responde EXACTAMENTE en este formato:\n\n");
        contexto.append("🎯 ALINEACIÓN RECOMENDADA\n\n");
        contexto.append("PORTERO:\n");
        contexto.append("• [Nombre] - [Equipo] - Precio: [X]€\n");
        contexto.append("  Razón: [Breve explicación]\n\n");
        contexto.append("JUGADORES DE CAMPO:\n");
        contexto.append("• [Nombre] - [Equipo] - Precio: [X]€\n");
        contexto.append("  Razón: [Breve explicación]\n");
        contexto.append("[Repetir para los 4 jugadores]\n\n");
        contexto.append("💰 RESUMEN ECONÓMICO:\n");
        contexto.append("Coste Total: [X]€\n");
        contexto.append("Presupuesto Disponible: ").append(presupuesto).append("€\n");
        contexto.append("Saldo Restante: [X]€\n\n");
        contexto.append("📊 ANÁLISIS:\n");
        contexto.append("[Breve análisis de por qué esta alineación es la mejor opción]\n");

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

        info.append("• ").append(jugador.getNombreJugador())
            .append(" (").append(jugador.getEquipo().getNombreEquipo()).append(")\n");
        info.append("  Precio: ").append(jugador.getPrecioMercado()).append("€\n");

        // Calcular estadísticas totales
        Map<String, Integer> estadisticasTotales = calcularEstadisticasTotales(jugador);

        if (esPortero) {
            info.append("  Goles Recibidos: ").append(estadisticasTotales.get("golesRecibidos")).append("\n");
        } else {
            info.append("  Goles: ").append(estadisticasTotales.get("goles")).append("\n");
            info.append("  Asistencias: ").append(estadisticasTotales.get("asistencias")).append("\n");
        }

        info.append("  Tarjetas Amarillas: ").append(estadisticasTotales.get("amarillas")).append("\n");
        info.append("  Tarjetas Rojas: ").append(estadisticasTotales.get("rojas")).append("\n");
        info.append("  Puntos Totales: ").append(estadisticasTotales.get("puntos")).append("\n");
        info.append("  Partidos Jugados: ").append(estadisticasTotales.get("partidos")).append("\n\n");

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

