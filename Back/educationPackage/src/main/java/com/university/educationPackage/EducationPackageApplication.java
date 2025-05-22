package com.university.educationPackage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EducationPackageApplication {
    private EducationPackageApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(EducationPackageApplication.class, args);
    }

    private static EducationPackageApplication createEducationPackageApplication() {
        return new EducationPackageApplication();
    }
}
