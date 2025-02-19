package com.spring.delivery.domain.controller.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

    @NotBlank(message = "rating은 필수 입력값입니다.")
    private double rating;

    @NotBlank(message = "comment는 필수 입력값입니다.")
    private String comment;

    @NotBlank(message = "orderId는 필수 입력값입니다.")
    private Long orderId;
}
