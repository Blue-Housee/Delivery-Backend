package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.ReviewRequestDto;
import com.spring.delivery.domain.controller.dto.ReviewResponseDto;
import com.spring.delivery.domain.service.ReviewService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
