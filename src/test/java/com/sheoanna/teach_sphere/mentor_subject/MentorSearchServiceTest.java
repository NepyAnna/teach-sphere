package com.sheoanna.teach_sphere.mentor_subject;

import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorMapper;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorResponse;
import com.sheoanna.teach_sphere.mentor_subject.dtos.MentorSearchRequest;
import com.sheoanna.teach_sphere.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorSearchServiceTest {
    @Mock
    private MentorSubjectRepository mentorSubjectRepository;
    @Mock
    private MentorMapper mentorMapper;

    @InjectMocks
    private MentorSearchService mentorSearchService;

    private User mentor1;
    private User mentor2;
    private MentorSubject subject1;
    private MentorSubject subject2;

    @BeforeEach
    void setUp() {
        mentor1 = User.builder().id(1L).username("mentor1").build();
        mentor2 = User.builder().id(2L).username("mentor2").build();

        subject1 = MentorSubject.builder().id(10L).mentor(mentor1).build();
        subject2 = MentorSubject.builder().id(11L).mentor(mentor2).build();
    }

    @Test
    void searchMentors_shouldReturnMappedResponses() {
        MentorSearchRequest request = new MentorSearchRequest(1L, 2L, "Kyiv");

        MentorResponse response1 = new MentorResponse(1L, "mentor1", "My bio", "Kyiv", "avatarUrl1",List.of("Math"));
        MentorResponse response2 = new MentorResponse(2L, "mentor2","My bio", "Lviv", "avatarUrl2",List.of("Math"));

        when(mentorSubjectRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(subject1, subject2));
        when(mentorMapper.toResponse(mentor1)).thenReturn(response1);
        when(mentorMapper.toResponse(mentor2)).thenReturn(response2);

        List<MentorResponse> result = mentorSearchService.searchMentors(request);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("username")
                .containsExactlyInAnyOrder("mentor1", "mentor2");

        verify(mentorSubjectRepository).findAll(any(Specification.class));
        verify(mentorMapper).toResponse(mentor1);
        verify(mentorMapper).toResponse(mentor2);
    }

    @Test
    void searchMentors_shouldReturnEmptyList_WhenNoMatches() {
        MentorSearchRequest request = new MentorSearchRequest(99L, 88L, "Lviv");

        when(mentorSubjectRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        List<MentorResponse> result = mentorSearchService.searchMentors(request);

        assertThat(result).isEmpty();
        verify(mentorSubjectRepository).findAll(any(Specification.class));
        verifyNoInteractions(mentorMapper);
    }

    @Test
    void searchMentors_shouldRemoveDuplicateMentors() {
        MentorSearchRequest request = new MentorSearchRequest(null, null, null);

        MentorSubject subjectDup1 = MentorSubject.builder().id(20L).mentor(mentor1).build();
        MentorSubject subjectDup2 = MentorSubject.builder().id(21L).mentor(mentor1).build();

        MentorResponse response = new MentorResponse(1L, "mentor1", "My bio", "Kyiv", "avatarUrl1",List.of("Math"));

        when(mentorSubjectRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(subjectDup1, subjectDup2));
        when(mentorMapper.toResponse(mentor1)).thenReturn(response);

        List<MentorResponse> result = mentorSearchService.searchMentors(request);

        assertThat(result).hasSize(1);

        verify(mentorMapper).toResponse(mentor1);
    }
}
