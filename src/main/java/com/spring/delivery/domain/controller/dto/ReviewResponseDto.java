package com.spring.delivery.domain.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewResponseDto {
    private UUID id;
    private Double rating;
    private String comment;
    private LocalDateTime created_at;

}
