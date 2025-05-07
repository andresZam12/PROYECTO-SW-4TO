package com.university.educationPackage.model;

import java.util.List;

public class EducationalPackage {
    private Long id;
    private String title;
    private String description;
    private Program program;
    private List<Course> includedCourses;
    private double price;
}