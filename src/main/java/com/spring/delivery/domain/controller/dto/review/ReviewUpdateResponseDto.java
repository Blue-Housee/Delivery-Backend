package com.spring.delivery.domain.controller.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReviewUpdateResponseDto {
    private UUID id;
    private Double rating;
    private String comment;
    private LocalDateTime update_at;
}
