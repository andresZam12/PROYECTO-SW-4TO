package com.university.educationPackage.services.voice;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.stereotype.Service;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class TextToSpeechService {

    public byte[] convertTextToSpeech(String text) throws Exception {
        // 1. Configurar solicitud de síntesis de voz
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("es-ES")
                    .setSsmlGender(SsmlVoiceGender.FEMALE) // Voz femenina en español
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16) // Formato WAV
                    .build();

            // 2. Sintetizar voz
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(
                    input, voice, audioConfig
            );

            // 3. Reproducir audio en el sistema
            byte[] audioData = response.getAudioContent().toByteArray();
            InputStream audioStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new AudioFormat(16000, 16, 1, true, false),
                    AudioSystem.getAudioInputStream(audioStream)
            );

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000); // Esperar a que termine
        }
        return null;
    }

    public byte[] convertToSpeech(String aiResponse) {
        return null;
    }
}
