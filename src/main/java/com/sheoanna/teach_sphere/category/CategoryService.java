package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryMapper;
import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
import com.sheoanna.teach_sphere.category.exceptions.CategoryAlreadyExistsException;
import com.sheoanna.teach_sphere.category.exceptions.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Page<CategoryResponse> findAllCategories(Pageable pageable){
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    public CategoryResponse findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.findByName(request.name()) != null){
            throw new CategoryAlreadyExistsException(request.name());
        }
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(CategoryRequest request, Long id){
        Category existCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        existCategory.setName(request.name());
        return categoryMapper.toResponse(existCategory);
    }

    public void deleteCategory(Long id) {
        if(categoryRepository.findById(id).isEmpty()) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

}
