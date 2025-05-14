package com.Hermes.Conversacion.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyAoCjYgs4n77MVikqaJJJmDER52vjhH0ZY"; // Tu API Key
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    public String generarContenido(String textoPrompt) {
        try {
            // JSON de entrada
            String inputJson = """
                {
                  "contents": [{
                    "parts": [{
                      "text": "%s"
                    }]
                  }]
                }
                """.formatted(textoPrompt);

            // Conexión HTTP
            URL url = new URL(ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Enviar JSON
            try (OutputStream os = conn.getOutputStream()) {
                os.write(inputJson.getBytes(StandardCharsets.UTF_8));
            }

            // Leer respuesta
            StringBuilder respuesta = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNextLine()) {
                    respuesta.append(scanner.nextLine());
                }
            }

            // Extraer texto de la respuesta
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(respuesta.toString());
            JsonNode partes = root.path("candidates").get(0).path("content").path("parts");
            return partes.get(0).path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al conectarse a Gemini: " + e.getMessage();
        }
    }
}
