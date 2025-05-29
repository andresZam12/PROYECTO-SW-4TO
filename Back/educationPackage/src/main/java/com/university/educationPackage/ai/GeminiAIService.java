package com.university.educationPackage.ai;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.university.educationPackage.models.Program;
import com.university.educationPackage.prompts.AdmissionPromptFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GeminiAIService {

    private final GenerativeModel model;
    private final AdmissionPromptFactory promptFactory;

    @Autowired
    public GeminiAIService(
            @Qualifier("medicineAdmissionPromptFactory") AdmissionPromptFactory promptFactory,
            @Value("${gemini.project.id}") String projectId,
            @Value("${gemini.location}") String location) {
        this.promptFactory = promptFactory;
        VertexAI vertexAI = new VertexAI(projectId, location);
        this.model = new GenerativeModel("gemini-pro", vertexAI);
    }

  public GeminiAIService(GenerativeModel model, AdmissionPromptFactory promptFactory) {

    this.model = model;
    this.promptFactory = promptFactory;
  }

  public String generateAdmissionResponse(String question, Program program) {
        String prompt = promptFactory.createPrompt(question, program);
        return processGeminiResponse(prompt);
    }

    public String generateResponse(String text) {
        return processGeminiResponse(text);
    }

    private String processGeminiResponse(String prompt) {
        try {
            GenerateContentResponse response = model.generateContent(prompt);
            return extractTextFromResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar respuesta: " + e.getMessage(), e);
        }
    }

    private String extractTextFromResponse(GenerateContentResponse response) {
        // Navegación segura a través de la estructura de respuesta
        if (response == null || response.getCandidatesList().isEmpty()) {
            return "No se pudo generar una respuesta.";
        }

        return response.getCandidatesList().get(0)
                .getContent()
                .getPartsList().get(0)
                .getText();
    }
}
