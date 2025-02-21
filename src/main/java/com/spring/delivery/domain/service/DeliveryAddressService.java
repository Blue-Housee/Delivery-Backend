package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressMessageRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.DeliveryAddressRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;

    //주문지 생성
    public DeliveryAddressMessageRequestDto createDeliveryAddress(DeliveryAddressRequestDto dto,
                                                            UserDetailsImpl userDetails) {

        List<DeliveryAddress> existsDeliveryAddress = deliveryAddressRepository.findByUser_Id(userDetails.getUser().getId());

        // 배송지 중복 에러
        if(existsDeliveryAddress.stream().anyMatch(addr -> addr.getAddress().equals(dto.getAddress()))){
            log.warn("배송지가 이미 존재합니다. : {}",dto.getAddress());
            throw new IllegalArgumentException("이미 존재하는 배송지입니다.");
        }

        // 배송지 최대 갯수 제한 에러
        if(existsDeliveryAddress.size() >= 3){
            log.warn("최대 배송지는 3개입니다.: {}",existsDeliveryAddress.size());
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

        //계정이 다르거나, 권한이 CUSTOMER이 아니면 에러
        if(deliveryAddress.getUser().getId() != userDetails.getUser().getId() ||
                currentUserRole != Role.CUSTOMER){
            log.warn("계정 정보가 다릅니다. : {}",deliveryAddress.getUser().getUsername());
            log.warn("권한 정보가 다릅니다. : {}",deliveryAddress.getUser().getRole().getAuthority());
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        //수정핣 배송지와 기존 배송지가 같으면 에러
        if(deliveryAddress.getAddress().equals(dto.getAddress())){
            log.warn("수정할 배송지와 기존 배송지가 같습니다. : {}",dto.getAddress());
            throw new IllegalArgumentException("수정할 배송지와 기존 배송지가 같습니다.");
        }

        deliveryAddress.update(dto.getAddress());

        return DeliveryAddressMessageRequestDto.builder()
                .message("배송지가 수정되었습니다.").build();
    }

    public List<DeliveryAddressResponseDto> selectAllDeliveryAddress(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<DeliveryAddress> deliveryAddress = deliveryAddressRepository.findByUser_Id(user.getId());

        Role currentUserRole = userDetails.getUser().getRole();

        //권한이 CUSTOMER이 아니면 에러
        if(currentUserRole != Role.CUSTOMER){
            log.warn("권한 정보가 다릅니다. : {}", user.getRole().getAuthority());
            throw new IllegalArgumentException("존재하지 않는 권한입니다.");
        }

        return deliveryAddress.stream()
                .map(delivery -> DeliveryAddressResponseDto.builder()
                            .id(delivery.getId())
                            .address(delivery.getAddress())
                            .request(delivery.getRequest())
                            .build())
                .collect(Collectors.toList());
    }

    public DeliveryAddressResponseDto selectDeliveryAddress(UUID id, UserDetailsImpl userDetails) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당되는 배송지가 없습니다."));

        Role currentUserRole = userDetails.getUser().getRole();

        //삭제된 데이터면 검색 X
        if(deliveryAddress.getDeletedBy() != null){
            log.warn("삭제된 데이터입니다 : {}", deliveryAddress.getId());
            throw new NoSuchElementException("삭제된 데이터입니다");
        }

        //계정이 다르거나 권한이 CUSTOMER이 아니면 에러
        if(deliveryAddress.getUser().getId() != userDetails.getUser().getId() ||
                currentUserRole != Role.CUSTOMER){
            log.warn("계정 정보가 다릅니다. : {}",deliveryAddress.getUser().getUsername());
            log.warn("권한 정보가 다릅니다. : {}",deliveryAddress.getUser().getRole().getAuthority());
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        return DeliveryAddressResponseDto.builder()
                .id(deliveryAddress.getId())
                .address(deliveryAddress.getAddress())
                .request(deliveryAddress.getRequest())
                .build();
    }

    @Transactional
    public DeliveryAddressMessageRequestDto deleteDeliveryAddress(UUID id, UserDetailsImpl userDetails) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당되는 배송지가 없습니다."));

        String username = userDetails.getUser().getUsername();
        Role currentUserRole = userDetails.getUser().getRole();

        //계정이 다르거나 권한이 CUSTOMER이 아니면 에러
        if(deliveryAddress.getUser().getId() != userDetails.getUser().getId() ||
                currentUserRole != Role.CUSTOMER){
            log.warn("계정 정보가 다릅니다. : {}",deliveryAddress.getUser().getUsername());
            log.warn("권한 정보가 다릅니다. : {}",deliveryAddress.getUser().getRole().getAuthority());
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        //이미 제거된 데이터면 에러
        if(deliveryAddress.getDeletedBy() != null){
            log.warn("삭제된 데이터입니다 : {}", deliveryAddress.getId());
            throw new NoSuchElementException("이미 삭제된 데이터입니다");
        }

        deliveryAddress.delete(username);

        return DeliveryAddressMessageRequestDto.builder()
                .message("배송지 삭제되었습니다.")
                .build();
    }
}
