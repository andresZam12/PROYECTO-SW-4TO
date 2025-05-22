package com.university.educationPackage.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "programs")
public class Program {
    @Id
    private String id;

    private String name;
    private String description;
    private int totalSemesters; // Ej: 12 semestres
    private double price; // Precio total de la carrera

    @ElementCollection
    private List<String> curriculum; // Pénsum: ["Semestre 1: Anatomía, Biología", ...]

    private String facultyLocation; // Ej: "Bloque A, Piso 3"
    private String contactEmail;
    private String bankAccount; // Cuenta para pagos

    // Getters y Setters
    public String getName() {
        return this.name;
    }

    public Object getPrice() {
        return null;
    }

    public String getTotalSemesters() {
        return null;
    }

    public String getContactEmail() {
        return "";
    }
}
