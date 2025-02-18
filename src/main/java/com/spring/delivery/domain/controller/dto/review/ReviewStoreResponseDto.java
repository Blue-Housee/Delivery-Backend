package com.spring.delivery.domain.controller.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewStoreResponseDto{
    private int page;
    private int size;
    private int total;

    private List<ReviewResponseDto> reviews;
}
