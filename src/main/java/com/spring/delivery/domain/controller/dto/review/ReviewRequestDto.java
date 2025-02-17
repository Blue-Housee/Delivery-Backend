package com.spring.delivery.domain.controller.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private double rating;
    private String comment;
    private Long orderId;
}
