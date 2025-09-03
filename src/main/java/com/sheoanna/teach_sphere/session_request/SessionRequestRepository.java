package com.sheoanna.teach_sphere.session_request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRequestRepository extends JpaRepository<SessionRequest, Long> {
    Page<SessionRequest> findByStudentId(Long studentId, Pageable pageable);
    Page<SessionRequest> findByMentorSubjectMentorId(Long mentorId, Pageable pageable);
}
