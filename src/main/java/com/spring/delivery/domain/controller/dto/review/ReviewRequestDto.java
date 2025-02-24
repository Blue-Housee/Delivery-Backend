package com.spring.delivery.domain.controller.dto.review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewRequestDto {

    @DecimalMin(value = "1.0", message = "rating은 최소 1.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "rating은 최대 5.0 이하이어야 합니다.")
    private double rating;

    @NotBlank(message = "comment는 필수 입력값입니다.")
    private String comment;

    @NotNull(message = "orderId는 필수 입력값입니다.")
    private UUID orderId;
}
