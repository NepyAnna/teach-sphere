package com.sheoanna.teach_sphere.session_request;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRequestRepository extends JpaRepository<SessionRequest, Long> {
    List<SessionRequest> findByStudentId(Long studentId);
    List<SessionRequest> findByMentorSubjectMentorId(Long mentorId);
}
