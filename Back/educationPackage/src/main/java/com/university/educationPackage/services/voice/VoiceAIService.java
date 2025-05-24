package com.university.educationPackage.services.voice;

import com.university.educationPackage.ai.GeminiAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class VoiceAIService {

    private final SpeechToTextService sttService;
    private final TextToSpeechService ttsService;
    private final GeminiAIService geminiAIService;
    private final String audioUploadPath;

    @Autowired
    public VoiceAIService(
            SpeechToTextService sttService,
            TextToSpeechService ttsService,
            GeminiAIService geminiAIService,
            @Value("${audio.upload.path:./uploads/audio/}") String audioUploadPath
    ) {
        this.sttService = sttService;
        this.ttsService = ttsService;
        this.geminiAIService = geminiAIService;
        this.audioUploadPath = audioUploadPath;
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(audioUploadPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio para audios", e);
        }
    }

    public byte[] processVoiceInput(MultipartFile audioFile) throws Exception {
        // 1. Validar archivo
        if (audioFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de audio está vacío");
        }

        // 2. Guardar temporalmente
        String tempFileName = saveTempAudioFile(audioFile);

        try {
            // 3. Procesar audio
            byte[] audioData = Files.readAllBytes(Paths.get(audioUploadPath, tempFileName));
            String text = sttService.convertToText(audioData);

            // 4. Generar respuesta con contexto
            String aiResponse = geminiAIService.generateResponse(
                    "Como asistente universitario, responde profesionalmente: " + text
            );

            // 5. Convertir a voz
            return ttsService.convertToSpeech(aiResponse);
        } finally {
            // 6. Limpiar archivo temporal
            Files.deleteIfExists(Paths.get(audioUploadPath, tempFileName));
        }
    }

    private String saveTempAudioFile(MultipartFile audioFile) throws IOException {
        String fileName = "temp_" + UUID.randomUUID() + "_" +
                audioFile.getOriginalFilename();
        Path filePath = Paths.get(audioUploadPath, fileName);

        Files.copy(audioFile.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public byte[] processVoiceInput(byte[] audioData) throws Exception {
        String text = sttService.convertToText(audioData);
        String aiResponse = geminiAIService.generateResponse(text);
        return ttsService.convertToSpeech(aiResponse);
    }
}