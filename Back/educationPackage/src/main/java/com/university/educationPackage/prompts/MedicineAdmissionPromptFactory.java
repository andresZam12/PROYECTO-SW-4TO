package com.university.educationPackage.prompts;

import com.university.educationPackage.models.Program;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component("medicineAdmissionPromptFactory")
public class MedicineAdmissionPromptFactory implements AdmissionPromptFactory {
    @Override
    public String createPrompt(String question, Program program) {
        return "Eres un asistente de admisiones de Medicina. Datos clave:\n" +
                "- Duración: " + program.getTotalSemesters() + " semestres\n" +
                "- Costo: $" + program.getPrice() + "\n" +
                "- Contacto: " + program.getContactEmail() + "\n\n" +
                "Pregunta: " + question;
    }
}
