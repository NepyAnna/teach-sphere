package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public Page<CategoryResponse> findAllCategories(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAllCategories(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.findCategoryById(id));
    }
}
