package com.sheoanna.teach_sphere.mentor_subject;

import org.springframework.data.jpa.domain.Specification;

public class MentorSpecification {
    public static Specification<MentorSubject> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("subject").get("category").get("id"),categoryId);
        };
    }

    public static Specification<MentorSubject> hasSubject(Long subjectId) {
        return (root, query, criteriaBuilder) -> {
            if (subjectId == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("subject").get("id"), subjectId);
        };
    }

    public static Specification<MentorSubject> hasLocation(String location) {
        return (root, query, criteriaBuilder) -> {
            if (location == null || location.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("mentor").get("profile").get("location")), location.toLowerCase() + "%");
        };
    }
}
