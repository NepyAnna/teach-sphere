package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectRequest;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSubjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor_subjects")
@RequiredArgsConstructor
public class MentorSubjectController {
    private final MentorSubjectService mentorSubjectService;

    @GetMapping("")
    public Page<MentorSubjectResponse> findALlMentorSubjects(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mentorSubjectService.findALlMentorSubjects(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorSubjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(mentorSubjectService.findById(id));
    }

    @PostMapping("")
    public ResponseEntity<MentorSubjectResponse> createMentorSubject(@Valid @RequestBody MentorSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorSubjectService.createMentorSubject(request));
    }

    @PutMapping("/{mentorSubjectId}")
    public ResponseEntity<MentorSubjectResponse> updateMentorSubject(@PathVariable Long mentorSubjectId, @Valid @RequestBody MentorSubjectRequest request) {
        return ResponseEntity.ok().body(mentorSubjectService.updateMentorSubject(mentorSubjectId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMentorSubject(@PathVariable Long id) {
        mentorSubjectService.deleteMentorSubject(id);
        return ResponseEntity.noContent().build();
    }
}
