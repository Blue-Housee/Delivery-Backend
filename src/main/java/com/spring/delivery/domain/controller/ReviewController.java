package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.ReviewRequestDto;
import com.spring.delivery.domain.service.ReviewService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //리뷰 단건 검색
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponseDto> getReviewDetails(@PathVariable UUID reviewId){
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reviewService.getReviewDetails(reviewId)
                )
        );
    }

    //상점의 리뷰 전체 검색(페이지네이션)
    @GetMapping("/stores/{storeId}/reviews")
    public ResponseEntity<ApiResponseDto> getStoreReview(@PathVariable UUID storeId,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("size") int size
                                                         ){
        System.out.println("page: " + page);
        System.out.println("size: " + size);
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reviewService.getStoreReview(storeId, page, size)
                )
        );
    }

    //리뷰 생성
    @PostMapping("/stores/{storeId}/reviews")
    public ResponseEntity<ApiResponseDto> createReview (@PathVariable UUID storeId,
                                                        @RequestBody ReviewRequestDto dto,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        reviewService.createReview(storeId, dto, userDetails.getUser())
                )
        );
    }


}
