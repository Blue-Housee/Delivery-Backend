package com.spring.delivery.domain.service;

import com.spring.delivery.domain.config.IntegrationTestBase;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressMessageRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.repository.DeliveryAddressRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.spring.delivery.domain.fixture.DeliveryAddressFixtureGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryAddressServiceTest extends IntegrationTestBase {
    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Test
    @DisplayName("배송지 생성 성공")
    void createDeliveryAddress_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto requestDto = createDto(ADDRESS, REQUEST);

        DeliveryAddressMessageRequestDto responseDto =
                deliveryAddressService.createDeliveryAddress(requestDto, userDetails);

        assertNotNull(responseDto);
        assertEquals(CREATE_SUCCESS_MESSAGE, responseDto.getMessage());
    }

    @Test
    @DisplayName("배송지 중복 생성 실패")
    void createDeliveryAddress_failure_duplicate() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto requestDto = createDto(ADDRESS, REQUEST);

        // 첫 번째 생성은 성공
        deliveryAddressService.createDeliveryAddress(requestDto, userDetails);

        // 동일 주소로 두 번째 생성 시도하면 예외 발생
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deliveryAddressService.createDeliveryAddress(requestDto, userDetails)
        );
        assertEquals(ALREADY_DATA_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("배송지 최대 개수 초과 실패")
    void createDeliveryAddress_failure_maximumNumber() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto dto1 = createDto("345 Test Street", "Leave at door");
        DeliveryAddressRequestDto dto2 = createDto("3456 Test Street", "Leave at door");
        DeliveryAddressRequestDto dto3 = createDto("3457 Test Street", "Leave at door");
        DeliveryAddressRequestDto dto4 = createDto("34578 Test Street", "Leave at door");

        deliveryAddressService.createDeliveryAddress(dto1, userDetails);
        deliveryAddressService.createDeliveryAddress(dto2, userDetails);
        deliveryAddressService.createDeliveryAddress(dto3, userDetails);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                deliveryAddressService.createDeliveryAddress(dto4, userDetails)
        );
        assertEquals(MAXIMUM_DATA_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("배송지 수정 성공")
    void updateDeliveryAddress_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        // 수정 요청 생성
        DeliveryAddressUpdateRequestDto updateDto = new DeliveryAddressUpdateRequestDto();
        updateDto.setAddress(UPDATE_ADDRESS);

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        DeliveryAddressMessageRequestDto updateResponse =
                deliveryAddressService.updateDeliveryAddress(deliveryAddress.getId(), updateDto, userDetails);

        assertNotNull(updateResponse);
        assertEquals(UPDATE_SUCCESS_MESSAGE, updateResponse.getMessage());
    }

    @Test
    @DisplayName("기존 배송지와 수정할 배송지 같을 경우 실패")
    void updateDeliveryAddress_failure_duplicate() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        // 수정 요청 생성
        DeliveryAddressUpdateRequestDto updateDto = new DeliveryAddressUpdateRequestDto();
        updateDto.setAddress(ADDRESS);

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                deliveryAddressService.updateDeliveryAddress(deliveryAddress.getId(), updateDto, userDetails));

        assertNotNull(illegalArgumentException);
        assertEquals(SAME_DATA_MESSAGE, illegalArgumentException.getMessage());
    }

    @Test
    @DisplayName("배송지 검색")
    void selectDeliveryAddress_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);

        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        DeliveryAddressResponseDto selectDto = deliveryAddressService.selectDeliveryAddress(deliveryAddress.getId(), userDetails);

        assertNotNull(selectDto);
        assertEquals(ADDRESS, selectDto.getAddress());
        assertEquals(REQUEST, selectDto.getRequest());
    }

    @Test
    @DisplayName("해당되는 배송지가 없을 경우 실패")
    void selectDeliveryAddress_failure_notFound() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        // 필요한 다른 필드도 설정합니다.

        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);

        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        // 검색할 UUID(없는 데이터)
        UUID selectId = UUID.randomUUID();

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () ->
                deliveryAddressService.selectDeliveryAddress(selectId, userDetails));

        assertNotNull(noSuchElementException);
        assertEquals(NO_DATA_MESSAGE, noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("이미 삭제된 데이터를 검색할 경우 에러")
    void selectDeliveryAddress_failure_deleteData() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        deliveryAddressService.deleteDeliveryAddress(deliveryAddress.getId(), userDetails);

        NoSuchElementException noSuchElementException = assertThrows(NoSuchElementException.class, () ->
                deliveryAddressService.selectDeliveryAddress(deliveryAddress.getId(), userDetails));

        assertNotNull(noSuchElementException);
        assertEquals(ALREADY_DELETE_DATA_MESSAGE, noSuchElementException.getMessage());
    }

    @Test
    @DisplayName("배송지 삭제 성공")
    @Transactional
    void deleteDeliveryAddress_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();
        User user = userDetails.getUser();

        // 필요한 다른 필드도 설정합니다.
        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        deliveryAddressService.deleteDeliveryAddress(deliveryAddress.getId(), userDetails);

        System.out.println("delivery : " + deliveryAddress.getDeletedBy());
        System.out.println("userDetailss : " + userDetails.getUser().getUsername());

        assertNotNull(deliveryAddress);
        assertEquals(user.getUsername(), deliveryAddress.getDeletedBy());
    }
}
