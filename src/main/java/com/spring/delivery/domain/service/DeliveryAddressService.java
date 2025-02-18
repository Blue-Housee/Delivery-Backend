package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressResponseDto;
import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import com.spring.delivery.domain.domain.repository.DeliveryAddressRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;

    public DeliveryAddressResponseDto createDeliveryAddress(DeliveryAddressRequestDto dto,
                                                            UserDetailsImpl userDetails) {
        List<DeliveryAddress> existsDeliveryAddress = deliveryAddressRepository.findByUser_Id(userDetails.getUser().getId());

        if(existsDeliveryAddress.stream().anyMatch(addr -> addr.getAddress().equals(dto.getAddress()))){
            throw new IllegalArgumentException("이미 존재하는 배송지입니다.");
        }

        if(existsDeliveryAddress.size() >= 3){
            throw new IllegalArgumentException("최대 배송지는 3개입니다.");
        }

        DeliveryAddress deliveryAddress =DeliveryAddress.builder()
                .address(dto.getAddress())
                .request(dto.getRequest())
                .user(userDetails.getUser())
                .build();

        deliveryAddressRepository.save(deliveryAddress);

        return DeliveryAddressResponseDto.builder()
                .message("배송지가 생성되었습니다")
                .build();
    }
}
