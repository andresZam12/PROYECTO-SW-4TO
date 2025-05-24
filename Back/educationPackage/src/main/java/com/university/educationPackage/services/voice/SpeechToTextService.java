package com.university.educationPackage.services.voice;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SpeechToTextService {

    public String convertVoiceToText(MultipartFile audioFile) throws Exception {
        // 1. Configuración del micrófono
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        // 2. Captura de audio (5 segundos)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        System.out.println("Escuchando... Habla ahora.");
        Thread.sleep(5000); // Graba por 5 segundos
        while (microphone.available() > 0) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            out.write(buffer, 0, bytesRead);
        }
        microphone.close();

        // 3. Enviar audio a Google Cloud Speech-to-Text
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(out.toByteArray());
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("es-ES") // Español
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // 4. Obtener transcripción
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();
            return results.isEmpty() ? "" : results.get(0).getAlternatives(0).getTranscript();
        }
    }

    public String convertToText(byte[] audioData) {
        return null;
    }
}