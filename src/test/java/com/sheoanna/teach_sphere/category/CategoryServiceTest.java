package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryMapper;
import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
import com.sheoanna.teach_sphere.category.exceptions.CategoryAlreadyExistsException;
import com.sheoanna.teach_sphere.category.exceptions.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;
    private  Long categoryId;

    @BeforeEach
    public void setUp(){
        categoryId = 1L;
        testCategory = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();
        categoryRequest = new CategoryRequest("Test Category");
        categoryResponse = new CategoryResponse(1L, "Test Category");
    }

    @Test
    public void findAllCategoriesTest_ReturnsPageOfCategories(){
        Page<Category> categories = new PageImpl<>(List.of(testCategory));

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categories);
        when(categoryMapper.toResponse(testCategory)).thenReturn(categoryResponse);

        Page<CategoryResponse> result = categoryService.findAllCategories(PageRequest.of(0, 2));

        assertEquals(1, result.getTotalElements());
        verify(categoryMapper).toResponse(testCategory);
    }

    @Test
    public void findCategoryByIdTest_Success(){
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toResponse(testCategory)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.findCategoryById(categoryId);

        assertEquals(categoryId, result.id());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void findCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.findCategoryById(categoryId));

        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.findByName(categoryRequest.name())).thenReturn(null);
        when(categoryMapper.toEntity(categoryRequest)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(categoryResponse);

        CategoryResponse result = categoryService.createCategory(categoryRequest);

        assertEquals(categoryResponse.id(), result.id());
        verify(categoryRepository).findByName(categoryRequest.name());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void createCategory_AlreadyExists_ThrowsException() {
        when(categoryRepository.findByName(categoryRequest.name())).thenReturn(testCategory);

        assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(categoryRequest));

        verify(categoryRepository).findByName(categoryRequest.name());
    }

    @Test
    void updateCategory_Success() {
        CategoryRequest updatedRequest = new CategoryRequest("Updated Category");
        Category updatedCategory = Category.builder().id(categoryId).name("Updated Category").build();
        CategoryResponse updatedResponse = new CategoryResponse(categoryId, "Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toResponse(testCategory)).thenReturn(updatedResponse);

        CategoryResponse result = categoryService.updateCategory(categoryId, updatedRequest);

        assertEquals("Updated Category", result.name());
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toResponse(testCategory);
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        CategoryRequest updatedRequest = new CategoryRequest("Updated Category");

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, updatedRequest));

        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository).findById(categoryId);
    }
}