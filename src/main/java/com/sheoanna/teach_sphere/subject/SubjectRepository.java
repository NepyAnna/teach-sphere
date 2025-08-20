package com.sheoanna.teach_sphere.subject;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);

    Optional<Subject> findByName(@NotBlank(message = "Subject name is required") String name);
}
