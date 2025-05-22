package com.university.educationPackage.config;

import com.university.educationPackage.avatars.AvatarService;
import com.university.educationPackage.prompts.AdmissionPromptFactory;
import com.university.educationPackage.prompts.LawAdmissionPromptFactory;
import com.university.educationPackage.prompts.MedicineAdmissionPromptFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public AdmissionPromptFactory medicinePromptFactory() {
        return new MedicineAdmissionPromptFactory();
    }

    @Bean
    public AdmissionPromptFactory lawPromptFactory() {
        return new LawAdmissionPromptFactory();
    }

    @Bean
    public AvatarService avatarService() {
        return new AvatarService();
    }
}
