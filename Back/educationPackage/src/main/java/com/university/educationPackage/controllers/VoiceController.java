package com.university.educationPackage.controllers;

import com.university.educationPackage.services.voice.SpeechToTextService;
import com.university.educationPackage.services.voice.TextToSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class VoiceController {

    private final SpeechToTextService sttService;
    private final TextToSpeechService ttsService;
    private static final int MAX_AUDIO_SIZE_MB = 2; // 2MB para WebM/Opus

    @Autowired
    public VoiceController(SpeechToTextService sttService,
                           TextToSpeechService ttsService) {
        this.sttService = sttService;
        this.ttsService = ttsService;
    }

    @MessageMapping("/voice/ask")
    @SendToUser("/queue/voice-response")
    public byte[] handleVoiceQuery(byte[] audioData, SimpMessageHeaderAccessor headerAccessor) {
        try {
            validateAudioData(audioData);

            MultipartFile audioFile = createAudioFile(audioData);
            String userQuestion = processSpeechToText(audioFile);
            String aiResponse = generateAIResponse(userQuestion);

            return convertTextToSpeech(aiResponse);

        } catch (IllegalArgumentException e) {
            return createErrorResponse("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Processing error: " + e.getMessage());
        }
    }

    // Métodos auxiliares mejor estructurados
    private void validateAudioData(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new IllegalArgumentException("Audio data cannot be empty");
        }

        if (audioData.length > MAX_AUDIO_SIZE_MB * 1024 * 1024) {
            throw new IllegalArgumentException(String.format(
                    "Audio exceeds maximum size of %dMB", MAX_AUDIO_SIZE_MB));
        }
    }

    private MultipartFile createAudioFile(byte[] audioData) {
        return new ByteArrayMultipartFile(
                audioData,
                "voice-message",
                "voice-message.webm",
                "audio/webm; codecs=opus"  // Especificación clara del codec
        );
    }

    private String processSpeechToText(MultipartFile audioFile) throws Exception {
        return sttService.convertVoiceToText(audioFile);
    }

    private String generateAIResponse(String userQuestion) throws Exception {
        // TODO: Integrar con tu servicio de IA
        return "Respuesta generada para: " + userQuestion;
    }

    private byte[] convertTextToSpeech(String text) throws Exception {
        return ttsService.convertTextToSpeech(text);
    }

    private byte[] createErrorResponse(String errorMessage) {
        return errorMessage.getBytes();
    }

    // Clase auxiliar optimizada
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] content, String name,
                                      String originalFilename, String contentType) {
            this.content = content;
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
        }

        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content == null || content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public ByteArrayInputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }
        @Override public void transferTo(java.io.File dest) throws IOException {
            new java.io.FileOutputStream(dest).write(content);
        }
    }
}