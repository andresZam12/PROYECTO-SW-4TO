package com.university.educationPackage.models;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class EducationalPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    private Set<Program> includedPrograms;
    private double discountPercentage;

    // Getters y Setters
}