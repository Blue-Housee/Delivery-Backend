package com.spring.delivery.domain.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateRequestDto {
    private Double rating;
    private String comment;
}
