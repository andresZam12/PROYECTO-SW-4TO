package com.university.educationPackage.controllers;

import com.university.educationPackage.services.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {
    @Autowired
    private AdmissionService admissionService;

    @GetMapping("/program/{programId}")
    public String getProgramInfo(
            @PathVariable Long programId,
            @RequestParam String question
    ) {
        return admissionService.getProgramInfo(programId, question);
    }
}