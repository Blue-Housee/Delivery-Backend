package com.spring.delivery.domain.controller.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ReviewDetailsResponseDto {
    private UUID id;
    private UUID store_id;
    private String customer_id;
    private Long customer_uuid;
    private Double rating;
    private String comment;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

