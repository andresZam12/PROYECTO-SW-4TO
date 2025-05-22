package com.university.educationPackage.services;

import com.university.educationPackage.ai.GeminiAIService;
import com.university.educationPackage.exceptions.AdmissionException;
import com.university.educationPackage.models.Program;
import com.university.educationPackage.repositories.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdmissionService {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private GeminiAIService geminiAIService;

    public String getProgramInfo(Long programId, String question) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new AdmissionException("Program not found"));

        return geminiAIService.generateAdmissionResponse(question, program);
    }
}
