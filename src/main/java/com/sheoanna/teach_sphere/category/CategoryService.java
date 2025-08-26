package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryMapper;
import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
import com.sheoanna.teach_sphere.category.exceptions.CategoryAlreadyExistsException;
import com.sheoanna.teach_sphere.category.exceptions.CategoryNotFoundException;
import com.sheoanna.teach_sphere.mentor_subject.MentorSubjectRepository;
import com.sheoanna.teach_sphere.subject.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Page<CategoryResponse> findAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    public CategoryResponse findCategoryById(Long id) {
        Category existCategory = findExistCategory(id);
        return categoryMapper.toResponse(existCategory);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.name()) != null) {
            throw new CategoryAlreadyExistsException(request.name());
        }
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        existCategory.setName(request.name());
        return categoryMapper.toResponse(existCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category existCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(existCategory);
    }

    public Category findExistCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
