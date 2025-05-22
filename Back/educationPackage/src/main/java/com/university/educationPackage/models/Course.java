package com.university.educationPackage.models;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // Ej: "MED-101"
    private String name;
    private int credits;
    private String description;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;


    // Constructores
    public Course() {}

    public Course(String code, String name, int credits, Program program) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.program = program;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getCode() { return code; }
    // ... otros getters y setters

    // Método para representación
    public String getFullCode() {
        return program.getName().substring(0, 3).toUpperCase() + "-" + code;
    }
}

