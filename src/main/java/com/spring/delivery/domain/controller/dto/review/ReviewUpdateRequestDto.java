package com.spring.delivery.domain.controller.dto.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateRequestDto {
    private Double rating;
    private String comment;
}
