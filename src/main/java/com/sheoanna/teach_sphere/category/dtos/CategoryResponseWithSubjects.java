package com.sheoanna.teach_sphere.category.dtos;

import com.sheoanna.teach_sphere.subject.Subject;

import java.util.Set;

public record CategoryResponseWithSubjects(Long id,
                                           String name,
                                           Set<Subject> subjects) {
}
