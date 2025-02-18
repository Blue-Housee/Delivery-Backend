package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressMessageRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.DeliveryAddressRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;

    public DeliveryAddressMessageRequestDto createDeliveryAddress(DeliveryAddressRequestDto dto,
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

        return DeliveryAddressMessageRequestDto.builder()
                .message("배송지가 생성되었습니다")
                .build();
    }

    @Transactional
    public DeliveryAddressMessageRequestDto updateDeliveryAddress(UUID id,
                                                            DeliveryAddressUpdateRequestDto dto,
                                                            UserDetailsImpl userDetails) {

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당되는 배송지가 없습니다."));

        Role currentUserRole = userDetails.getUser().getRole();


        if(deliveryAddress.getUser().getId() != userDetails.getUser().getId() ||
                currentUserRole != Role.CUSTOMER){
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        if(deliveryAddress.getAddress().equals(dto.getAddress())){
            throw new IllegalArgumentException("수정할 배송지와 기존 배송지가 같습니다.");
        }

        deliveryAddress.update(dto.getAddress());

        return DeliveryAddressMessageRequestDto.builder()
                .message("배송지가 수정되었습니다.").build();
    }

    public DeliveryAddressResponseDto selectDeliveryAddress(UUID id, UserDetailsImpl userDetails) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당되는 배송지가 없습니다."));

        Role currentUserRole = userDetails.getUser().getRole();

        if(deliveryAddress.getUser().getId() != userDetails.getUser().getId() ||
                currentUserRole != Role.CUSTOMER){
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        return DeliveryAddressResponseDto.builder()
                .id(deliveryAddress.getId())
                .address(deliveryAddress.getAddress())
                .request(deliveryAddress.getRequest())
                .build();
    }
}
