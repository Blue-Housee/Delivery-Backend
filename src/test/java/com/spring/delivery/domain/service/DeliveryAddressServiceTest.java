package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressMessageRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressRequestDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressResponseDto;
import com.spring.delivery.domain.controller.dto.DeliveryAddress.DeliveryAddressUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.DeliveryAddress;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.DeliveryAddressRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 테스트 전용 프로파일 적용
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeliveryAddressServiceTest {
    private static final String ADDRESS = "345 Test Street";
    private static final String REQUEST = "Leave at door";
    private static final String UPDATE_ADDRESS = "123 Test Street";

    private static final String CREATE_SUCCESS_MESSAGE = "배송지가 생성되었습니다";
    private static final String UPDATE_SUCCESS_MESSAGE = "배송지가 수정되었습니다.";
    private static final String ALREADY_DELETE_DATA_MESSAGE = "삭제된 데이터입니다";
    private static final String ALREADY_DATA_MESSAGE = "이미 존재하는 배송지입니다.";
    private static final String MAXIMUM_DATA_MESSAGE = "최대 배송지는 3개입니다.";
    private static final String SAME_DATA_MESSAGE = "수정할 배송지와 기존 배송지가 같습니다.";
    private static final String NO_DATA_MESSAGE = "해당되는 배송지가 없습니다.";

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeAll
    void setUp() {
        // 테스트 데이터 초기화 (테스트용 유저 생성)
        userRepository.deleteAll(); // 다른 테스트 간의 간섭을 막기 위해 초기화
        User testUser = User.createUser("testUser", "test@example.com", "password", Role.CUSTOMER);
        user = userRepository.save(testUser);
        // 필요한 다른 필드도 설정합니다.
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    @Order(1)
    @DisplayName("배송지 생성 성공")
    @Transactional
    void createDeliveryAddress_success() {

        DeliveryAddressRequestDto requestDto = createDto(ADDRESS, REQUEST);

        DeliveryAddressMessageRequestDto responseDto =
                deliveryAddressService.createDeliveryAddress(requestDto, userDetails);

        assertNotNull(responseDto);
        assertEquals(CREATE_SUCCESS_MESSAGE, responseDto.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("배송지 중복 생성 실패")
    @Transactional
    void createDeliveryAddress_failure_duplicate() {

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
    @Order(3)
    @DisplayName("배송지 최대 개수 초과 실패")
    @Transactional
    void createDeliveryAddress_failure_maximumNumber() {
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
    @Order(4)
    @DisplayName("배송지 수정 성공")
    @Transactional
    void updateDeliveryAddress_success() {
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
    @Order(5)
    @DisplayName("기존 배송지와 수정할 배송지 같을 경우 실패")
    @Transactional
    void updateDeliveryAddress_failure_duplicate() {
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
    @Order(6)
    @DisplayName("배송지 검색")
    @Transactional
    void selectDeliveryAddress_success() {
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
    @Order(7)
    @DisplayName("해당되는 배송지가 없을 경우 실패")
    @Transactional
    void selectDeliveryAddress_failure_notFound() {

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
    @Order(8)
    @DisplayName("이미 삭제된 데이터를 검색할 경우 에러")
    @Transactional
    void selectDeliveryAddress_failure_deleteData() {

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
    @Order(9)
    @DisplayName("배송지 삭제 성공")
    @Transactional
    void deleteDeliveryAddress_success() {

        DeliveryAddressRequestDto createDto = createDto(ADDRESS, REQUEST);

        // 생성 테스트
        DeliveryAddressMessageRequestDto createResponse =
                deliveryAddressService.createDeliveryAddress(createDto, userDetails);
        assertNotNull(createResponse);
        assertEquals(CREATE_SUCCESS_MESSAGE, createResponse.getMessage());

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUser_IdAndAddress(user.getId(), ADDRESS);

        deliveryAddressService.deleteDeliveryAddress(deliveryAddress.getId(), userDetails);

        assertNotNull(deliveryAddress);
        assertEquals(user.getUsername(), deliveryAddress.getDeletedBy());

    }


    // 공통적으로 사용되는 DTO 생성 헬퍼 메소드
    private DeliveryAddressRequestDto createDto(String address, String request) {
        DeliveryAddressRequestDto dto = new DeliveryAddressRequestDto();
        dto.setAddress(address);
        dto.setRequest(request);
        return dto;
    }
}
