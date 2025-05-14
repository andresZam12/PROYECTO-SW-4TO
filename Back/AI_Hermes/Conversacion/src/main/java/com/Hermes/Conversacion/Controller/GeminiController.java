package com.Hermes.Conversacion.Controller;

import com.Hermes.Conversacion.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "*") // Permite llamadas desde Postman o cualquier frontend
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    // Petición POST con JSON { "pregunta": "Tu pregunta aquí" }
    @PostMapping("/preguntar")
    public String preguntarAGemini(@RequestBody PreguntaDTO preguntaDTO) {
        return geminiService.generarContenido(preguntaDTO.getPregunta());
    }

    // DTO interno para recibir JSON con una propiedad "pregunta"
    public static class PreguntaDTO {
        private String pregunta;

        public String getPregunta() {
            return pregunta;
        }

        public void setPregunta(String pregunta) {
            this.pregunta = pregunta;
        }
    }
}
