package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryMapper;
import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
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

    @BeforeEach
    public void setUp(){
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
}