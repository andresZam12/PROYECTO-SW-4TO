package com.Hermes.Conversacion.Controller;

import com.Hermes.Conversacion.Service.GeminiService;
import com.Hermes.Conversacion.DTO.PreguntaDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "*") // Permite llamadas desde Postman o cualquier frontend
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/preguntar")
    public String preguntarAGemini(@RequestBody PreguntaDTO preguntaDTO) {
        return geminiService.generarContenido(preguntaDTO.getPregunta());
    }
}
