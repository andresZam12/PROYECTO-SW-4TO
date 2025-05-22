package com.university.educationPackage.ai;

import com.university.educationPackage.models.Program;
import com.university.educationPackage.prompts.AdmissionPromptFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GeminiAIService {

    @Autowired
    @Qualifier("lawAdmissionPromptFactory") // o "medicineAdmissionPromptFactory"
    private AdmissionPromptFactory promptFactory;

    public String generateAdmissionResponse(String question, Program program) {
        String prompt = promptFactory.createPrompt(question, program);
        // Aquí podrías integrar Gemini AI con el prompt generado
        return "Respuesta generada para: " + prompt;
    }
}
