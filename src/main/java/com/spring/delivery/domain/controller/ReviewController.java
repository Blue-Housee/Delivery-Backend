package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;

import com.spring.delivery.domain.controller.dto.review.ReviewRequestDto;
import com.spring.delivery.domain.controller.dto.review.ReviewUpdateRequestDto;
import com.spring.delivery.domain.service.ReviewService;
import com.spring.delivery.global.security.UserDetailsImpl;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 단건 검색
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseDto> getReviewDetails(@PathVariable UUID reviewId){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(reviewService.getReviewDetails(reviewId));

        return ResponseEntity.ok(apiResponseDto);
    }

    //상점의 리뷰 전체 검색(페이지네이션)
    @GetMapping("/stores/{storeId}/reviews")
    public ResponseEntity<ApiResponseDto> getStoreReview(@PathVariable UUID storeId,
                                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                                         @RequestParam(value = "orderby", defaultValue = "createdAt") String criteria,
                                                         @RequestParam(value = "sort", defaultValue = "DESC") String sort
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(reviewService.getStoreReview(storeId, page, size, criteria, sort));

        return ResponseEntity.ok(apiResponseDto);
    }

    //리뷰 생성
    @PostMapping("/stores/{storeId}/reviews")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<ApiResponseDto> createReview (@PathVariable UUID storeId,
                                                        @Valid @RequestBody ReviewRequestDto dto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(reviewService.createReview(storeId, dto, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseDto> updateReview (@PathVariable UUID reviewId,
                                                        @Valid @RequestBody ReviewUpdateRequestDto dto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(reviewService.updateReview(reviewId, dto, userDetails));

        return ResponseEntity.ok(apiResponseDto);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','MASTER')")
    public ResponseEntity<ApiResponseDto> deleteReview (@PathVariable UUID reviewId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ApiResponseDto apiResponseDto = ApiResponseDto.success(reviewService.deleteReview(reviewId, userDetails));

        return ResponseEntity.ok(apiResponseDto);

    }
}
