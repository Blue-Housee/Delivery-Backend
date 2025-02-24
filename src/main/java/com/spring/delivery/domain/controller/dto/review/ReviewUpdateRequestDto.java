package com.spring.delivery.domain.controller.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateRequestDto {

    @NotBlank(message = "rating은 필수 입력값입니다.")
    private Double rating;
    @NotBlank(message = "comment는 필수 입력값입니다.")
    private String comment;
}
