package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.subject.dtos.SubjectRequest;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("")
    public Page<SubjectResponse> findAllSubjects(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return subjectService.findAllSubjects(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> findSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok().body(subjectService.findSubjectById(id));
    }

    @PostMapping("")
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.createSubject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable Long id,@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok()
                .body(subjectService.updateSubject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
