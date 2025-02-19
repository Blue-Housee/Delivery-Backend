package com.spring.delivery.domain.controller.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReviewResponseDto {
    private UUID id;
    private Double rating;
    private String comment;
    private LocalDateTime created_at;
}
