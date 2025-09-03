package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorResponse;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
public class MentorSearchController {
    private final MentorSearchService mentorSearchService;

    @GetMapping("/search")
    public List<MentorResponse> searchMentors(@RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) Long subjectId,
                                              @RequestParam(required = false) String location) {
        MentorSearchRequest request = new MentorSearchRequest(categoryId, subjectId, location);
        return mentorSearchService.searchMentors(request);
    }
}