package com.university.educationPackage.prompts;

import com.university.educationPackage.models.Program;

public interface AdmissionPromptFactory {
    String createPrompt(String question, Program program);
}
