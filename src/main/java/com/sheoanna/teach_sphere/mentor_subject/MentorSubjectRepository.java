package com.sheoanna.teach_sphere.mentor_subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MentorSubjectRepository extends JpaRepository<MentorSubject, Long>, JpaSpecificationExecutor<MentorSubject> {
}
