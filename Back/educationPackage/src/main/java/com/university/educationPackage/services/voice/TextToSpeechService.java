package com.university.educationPackage.services.voice;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;
import ws.schild.jave.*;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

@Service
public class TextToSpeechService {

    private static final String LANGUAGE_CODE = "es-ES";
    private static final SsmlVoiceGender VOICE_GENDER = SsmlVoiceGender.FEMALE;
    private static final int SAMPLE_RATE = 16000; // Hz

    public byte[] convertTextToSpeech(String text) throws Exception {
        // 1. Generar audio en LINEAR16 (WAV) usando Google TTS
        byte[] wavAudio = generateLinear16Audio(text);

        // 2. Convertir WAV a WebM/Opus
        return convertWavToWebM(wavAudio);
    }

    public byte[] convertToSpeech(String text) {
        try {
            return convertTextToSpeech(text);
        } catch (Exception e) {
            throw new RuntimeException("Error converting text to speech", e);
        }
    }

    private byte[] generateLinear16Audio(String text) throws Exception {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(LANGUAGE_CODE)
                    .setSsmlGender(VOICE_GENDER)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(SAMPLE_RATE)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig);

            return response.getAudioContent().toByteArray();
        }
    }

    private byte[] convertWavToWebM(byte[] wavData) throws Exception {
        // Crear archivo temporal WAV
        File wavFile = File.createTempFile("tts-", ".wav");
        Files.write(wavFile.toPath(), wavData);

        // Archivo temporal de salida WebM
        File webmFile = File.createTempFile("tts-", ".webm");

        // Configurar conversión con JAVE
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libopus");
        audio.setBitRate(32000); // 32 kbps para voz
        audio.setChannels(1);
        audio.setSamplingRate(SAMPLE_RATE);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("webm");
        attrs.setAudioAttributes(audio);

        // Ejecutar conversión
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(wavFile), webmFile, attrs);

        // Leer resultado y limpiar
        byte[] webmData = Files.readAllBytes(webmFile.toPath());
        wavFile.delete();
        webmFile.delete();

        return webmData;
    }
}