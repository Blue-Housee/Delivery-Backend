package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryDeleteResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryListResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryRequestDto;
import com.spring.delivery.domain.controller.dto.category.CategoryUpdateResponseDto;
import com.spring.delivery.domain.service.CategoryService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> createCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CategoryRequestDto requestDto) {
        ApiResponseDto responseDto = categoryService.createCategory(userDetails, requestDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<CategoryListResponseDto>>> getAllCategories(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sortBy") String sortBy,
            @RequestParam(value = "isAsc") boolean isAsc) {
        ApiResponseDto<Page<CategoryListResponseDto>> responseDto = categoryService.getAllCategories(userDetails, page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CategoryUpdateResponseDto>> updateCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id, @RequestBody CategoryRequestDto requestDto) {
        ApiResponseDto<CategoryUpdateResponseDto> response = categoryService.updateCategory(userDetails, id, requestDto);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<CategoryDeleteResponseDto>> deleteCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        ApiResponseDto<CategoryDeleteResponseDto> response = categoryService.deleteCategory(userDetails, id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
