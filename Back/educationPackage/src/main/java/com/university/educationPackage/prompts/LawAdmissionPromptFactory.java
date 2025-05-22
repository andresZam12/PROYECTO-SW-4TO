package com.university.educationPackage.prompts;

import com.university.educationPackage.models.Program;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component("lawAdmissionPromptFactory")
public class LawAdmissionPromptFactory implements AdmissionPromptFactory {
    @Override
    public String createPrompt(String question, Program program) {
        return "Eres un asistente especializado en Derecho. Información clave:\n" +
                "- Duración: " + program.getTotalSemesters() + " semestres\n" +
                "- Áreas: Derecho Penal, Civil, Laboral\n" +
                "- Costo: $" + program.getPrice() + "\n" +
                "- Contacto: " + program.getContactEmail() + "\n\n" +
                "Pregunta: " + question;
    }
}
