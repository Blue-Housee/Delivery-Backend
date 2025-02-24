package com.spring.delivery.domain.controller.dto.DeliveryAddress;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAddressRequestDto {

    @NotBlank(message = "address는 필수 입력값입니다.")
    private String address;

    @NotBlank(message = "request는 필수 입력값입니다.")
    private String request;
}
