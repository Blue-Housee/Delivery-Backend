package com.spring.delivery.domain.controller.dto.DeliveryAddress;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryAddressResponseDto {
    private UUID id;
    private String address;
    private String request;
}
