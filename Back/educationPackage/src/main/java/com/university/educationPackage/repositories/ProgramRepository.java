package com.university.educationPackage.repositories;

import com.university.educationPackage.models.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}
