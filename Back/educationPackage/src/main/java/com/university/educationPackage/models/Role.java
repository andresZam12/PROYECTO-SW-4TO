package com.university.educationPackage.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @OneToMany(mappedBy = "role")
    private List<User> student;


    public enum RoleType {
        STUDENT, ADMIN, ADMISSIONS_OFFICER, PROFESSOR
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<User> getStudent() {
        return (Set<User>) student;
    }

    public void setStudent(Set<User> student) {
        this.student = (List<User>) student;
    }
}