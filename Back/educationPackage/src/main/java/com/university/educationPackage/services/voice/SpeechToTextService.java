package com.university.educationPackage.services.voice;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class SpeechToTextService {

    private static final int SAMPLE_RATE = 16000; // Hz
    private static final String LANGUAGE_CODE = "es-ES";

    public String convertVoiceToText(MultipartFile audioFile) throws Exception {
        // 1. Convertir WebM/Opus a formato compatible (LINEAR16)
        byte[] rawAudio = convertWebmToLinear16(audioFile.getBytes());

        // 2. Configuración para Google Speech-to-Text
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(SAMPLE_RATE)
                .setLanguageCode(LANGUAGE_CODE)
                .build();

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(ByteString.copyFrom(rawAudio))
                .build();

        // 3. Procesar con Google Cloud
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognizeResponse response = speechClient.recognize(config, audio);
            return extractBestTranscript(response);
        }
    }

    public String convertToText(byte[] webmAudioData) {
        try {
            byte[] linear16Data = convertWebmToLinear16(webmAudioData);
            return processWithGoogleSpeech(linear16Data);
        } catch (Exception e) {
            throw new RuntimeException("Error processing audio", e);
        }
    }

    private byte[] convertWebmToLinear16(byte[] webmData) throws IOException {
        // Implementación con FFmpeg (requiere dependencia)
        Process process = new ProcessBuilder(
                "ffmpeg",
                "-i", "pipe:0",          // Entrada desde stdin
                "-f", "s16le",           // Formato LINEAR16
                "-ar", String.valueOf(SAMPLE_RATE),
                "-ac", "1",             // Mono
                "pipe:1"                 // Salida a stdout
        ).start();

        process.getOutputStream().write(webmData);
        process.getOutputStream().close();

        ByteArrayOutputStream convertedAudio = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = process.getInputStream().read(buffer)) != -1) {
            convertedAudio.write(buffer, 0, bytesRead);
        }

        try {
            if (process.waitFor() != 0) {
                throw new IOException("FFmpeg conversion failed");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return convertedAudio.toByteArray();
    }

    private String processWithGoogleSpeech(byte[] linear16Audio) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(SAMPLE_RATE)
                    .setLanguageCode(LANGUAGE_CODE)
                    .build();

            RecognizeResponse response = speechClient.recognize(
                    config,
                    RecognitionAudio.newBuilder()
                            .setContent(ByteString.copyFrom(linear16Audio))
                            .build()
            );
            return extractBestTranscript(response);
        }
    }

    private String extractBestTranscript(RecognizeResponse response) {
        List<SpeechRecognitionResult> results = response.getResultsList();
        if (results.isEmpty()) {
            return "";
        }
        return results.get(0)
                .getAlternativesList()
                .stream()
                .findFirst()
                .map(SpeechRecognitionAlternative::getTranscript)
                .orElse("");
    }
}