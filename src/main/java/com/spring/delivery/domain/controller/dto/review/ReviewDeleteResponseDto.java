package com.spring.delivery.domain.controller.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewDeleteResponseDto{
    private String message;
    private LocalDateTime delete_at;
}
