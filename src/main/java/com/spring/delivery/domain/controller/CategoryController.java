package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.*;
import com.spring.delivery.domain.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponseDto> createCategory(@RequestBody CategoryRequestDto requestDto) {
        ApiResponseDto responseDto = categoryService.createCategory(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<CategoryListResponseDto>>> getAllCategories(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sortBy") String sortBy,
            @RequestParam(value = "isAsc") boolean isAsc) {

        Page<CategoryListResponseDto> categoryResponseDto = categoryService.getAllCategories(page - 1, size, sortBy, isAsc);
        return ResponseEntity.ok(ApiResponseDto.success(categoryResponseDto));
    }

}
