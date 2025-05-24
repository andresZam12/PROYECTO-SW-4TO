package com.university.educationPackage.prompts;

import com.university.educationPackage.models.Program;
import org.springframework.stereotype.Service;

@Service("medicineAdmissionPromptFactory")
public class MedicineAdmissionPromptFactory implements AdmissionPromptFactory {

    private static final String PROMPT_TEMPLATE =
            "Eres un asistente especializado en admisiones de la Universidad Cooperativa de Colombia para la facultad de Medicina. " +
                    "Proporciona respuestas precisas y profesionales basadas en estos datos:\n" +
                    "- Programa: %s\n" +
                    "- Duración: %d semestres\n" +
                    "- Costo total: $%.2f\n" +
                    "- Contacto: %s\n\n" +
                    "La pregunta del aspirante es: %s\n\n" +
                    "Respuesta sugerida:";

    @Override
    public String createPrompt(String question, Program program) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("La pregunta no puede estar vacía");
        }

        if (program == null) {
            throw new IllegalArgumentException("El programa no puede ser nulo");
        }

        return String.format(PROMPT_TEMPLATE,
                program.getName(),
                program.getTotalSemesters(),
                program.getPrice(),
                program.getContactEmail(),
                question.trim());
    }
}