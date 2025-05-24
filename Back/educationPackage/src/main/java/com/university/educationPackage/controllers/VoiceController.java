package com.university.educationPackage.controllers;

import com.university.educationPackage.services.voice.SpeechToTextService;
import com.university.educationPackage.services.voice.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final SpeechToTextService sttService;
    private final TextToSpeechService ttsService;

    @Autowired
    public VoiceController(SpeechToTextService sttService, TextToSpeechService ttsService) {
        this.sttService = sttService;
        this.ttsService = ttsService;
    }

    @PostMapping(value = "/ask", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> handleVoiceQuery(@RequestParam("audio") MultipartFile audioFile) {
        try {
            // 1. Convertir voz a texto (audio → texto)
            String userQuestion = sttService.convertVoiceToText(audioFile);

            // 2. Obtener respuesta de la IA (integrado con Gemini AI)
            String aiTextResponse = getAIResponse(userQuestion); // Método que integra con tu IA

            // 3. Convertir texto a voz (texto → audio)
            byte[] audioResponse = ttsService.convertTextToSpeech(aiTextResponse);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"response.mp3\"")
                    .body(audioResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getAIResponse(String userQuestion) throws Exception {
        // TODO: Integrar con tu servicio de Gemini AI existente
        // Ejemplo:
        // return geminiService.generateResponse(userQuestion);
        return "Respuesta generada por la IA para: " + userQuestion;
    }
}