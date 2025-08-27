package com.sheoanna.teach_sphere.subject;

import com.sheoanna.teach_sphere.subject.exceptions.SubjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sheoanna.teach_sphere.category.Category;
import com.sheoanna.teach_sphere.category.CategoryService;
import com.sheoanna.teach_sphere.subject.dtos.SubjectMapper;
import com.sheoanna.teach_sphere.subject.dtos.SubjectRequest;
import com.sheoanna.teach_sphere.subject.dtos.SubjectResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubjectServiceTest {
    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject;
    private SubjectRequest subjectRequest;
    private SubjectResponse subjectResponse;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder().id(1L).name("Math").build();
        subject = Subject.builder().id(1L).name("Algebra").category(category).build();
        subjectRequest = new SubjectRequest("Algebra", 1L);
        subjectResponse = new SubjectResponse(1L, "Algebra", category.getName());
    }

    @Test
    void findAllSubjects_ReturnsPage() {
        Page<Subject> subjects = new PageImpl<>(List.of(subject));
        when(subjectRepository.findAll(any(Pageable.class))).thenReturn(subjects);
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        Page<SubjectResponse> result = subjectService.findAllSubjects(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        verify(subjectMapper).toResponse(subject);
        verify(subjectRepository).findAll(any(Pageable.class));
    }

    @Test
    void findSubjectById_Success() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        SubjectResponse result = subjectService.findSubjectById(1L);

        assertEquals("Algebra", result.name());
        verify(subjectRepository).findById(1L);
        verify(subjectMapper).toResponse(subject);
    }

    @Test
    void findSubjectById_NotFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.findSubjectById(1L));
        verify(subjectRepository).findById(1L);
        verifyNoInteractions(subjectMapper);
    }

    @Test
    void createSubject_Success() {
        when(categoryService.findExistCategory(1L)).thenReturn(category);
        when(subjectRepository.existsByName("Algebra")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        SubjectResponse result = subjectService.createSubject(subjectRequest);

        assertEquals("Algebra", result.name());
        verify(categoryService).findExistCategory(1L);
        verify(subjectRepository).existsByName("Algebra");
        verify(subjectRepository).save(any(Subject.class));
        verify(subjectMapper).toResponse(subject);
    }

    @Test
    void updateSubject_Success() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(categoryService.findExistCategory(1L)).thenReturn(category);
        when(subjectMapper.toResponse(subject)).thenReturn(subjectResponse);

        SubjectResponse result = subjectService.updateSubject(1L, subjectRequest);

        assertEquals("Algebra", result.name());
        verify(subjectRepository).findById(1L);
        verify(categoryService).findExistCategory(1L);
        verify(subjectMapper).toResponse(subject);
    }

    @Test
    void updateSubject_NotFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.updateSubject(1L, subjectRequest));
        verify(subjectRepository).findById(1L);
        verifyNoInteractions(categoryService, subjectMapper);
    }

    @Test
    void deleteSubject_Success() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        subjectService.deleteSubject(1L);

        verify(subjectRepository).deleteById(1L);
    }

    @Test
    void deleteSubject_NotFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class, () -> subjectService.deleteSubject(1L));
        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).deleteById(anyLong());
    }
}
