package com.sheoanna.teach_sphere.category;

import com.sheoanna.teach_sphere.category.dtos.CategoryRequest;
import com.sheoanna.teach_sphere.category.dtos.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Operations related to category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    @Operation(summary = "Get all categories.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories returned successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public Page<CategoryResponse> findAllCategories(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAllCategories(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category returned successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/CategoryNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.findCategoryById(id));
    }

    @PostMapping("")
    @Operation(summary = "Create category.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/CategoryNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request){
        return ResponseEntity.ok().body(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
                    @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/CategoryNotFound"),
                    @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
            })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
