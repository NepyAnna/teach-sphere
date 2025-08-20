package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    public ResponseEntity<SubjectResponse> findAllSubjects(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(subjectService.findAllSubjects(pageable));
    }
}
