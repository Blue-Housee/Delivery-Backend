package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreCreateRequestDto;
import com.spring.delivery.domain.controller.dto.store.StoreListResponseDto;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.StoreCategory;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import com.spring.delivery.domain.domain.repository.StoreCategoryRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreCategoryRepository storeCategoryRepository;

    @Autowired
    private StoreService storeService;

    private UserDetailsImpl masterUserDetails;
    private UserDetailsImpl customerUserDetails;
    private StoreCreateRequestDto requestDto;
    private Category testCategory;
    private UUID testCategoryId;

    @BeforeEach
    void setUp() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트용 카테고리 생성
        testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        testCategoryId = testCategory.getId();
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("가게 등록 - 권한 있음")
    void testStoreCreationSuccess() {
        // 요청 DTO 생성
        requestDto = StoreCreateRequestDto.builder()
                .name("테스트 가게")
                .categoryIds(List.of(testCategoryId))
                .address("테스트 주소")
                .tel("010-1234-5678")
                .openStatus(true)
                .startTime(LocalTime.of(9, 0)) // 09:00
                .endTime(LocalTime.of(22, 0))   // 22:00
                .build();

        // 가게 생성 메서드 호출
        ApiResponseDto response = storeService.createStore(masterUserDetails, requestDto);

        // 결과 검증
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("요청이 성공적으로 처리되었습니다.", response.getMessage());

        // 데이터베이스에서 가게 조회
        UUID storeId = (UUID) response.getData();
        Store createdStore = storeRepository.findById(storeId).orElse(null);
        assertNotNull(createdStore);
        assertEquals(requestDto.getName(), createdStore.getName());
        assertEquals(requestDto.getAddress(), createdStore.getAddress());
        assertEquals(requestDto.getTel(), createdStore.getTel());
        assertEquals(requestDto.isOpenStatus(), createdStore.isOpenStatus());
        assertEquals(requestDto.getStartTime(), createdStore.getStartTime());
        assertEquals(requestDto.getEndTime(), createdStore.getEndTime());

        // 카테고리 검증
        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        assertNotNull(storeCategories);
        assertEquals(1, storeCategories.size());
        assertEquals(testCategoryId, storeCategories.get(0).getCategory().getId());
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("가게 등록 - 권한 없음")
    void testStoreCreationFailDueToInsufficientPermissions() {
        // 요청 DTO 생성
        requestDto = StoreCreateRequestDto.builder()
                .name("테스트 가게")
                .categoryIds(List.of(testCategoryId))
                .address("테스트 주소")
                .tel("010-1234-5678")
                .openStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        // 권한이 없는 유저로 가게 생성 메서드 호출
        ApiResponseDto response = storeService.createStore(customerUserDetails, requestDto);

        // 결과 검증
        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("가게를 등록할 권한이 없습니다.", response.getMessage());
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("가게 조회 - 전체 목록")
    void testGetAllStores() {
        // 가게 등록 테스트를 통해 가게를 생성합니다.
        StoreCreateRequestDto createRequest = StoreCreateRequestDto.builder()
                .name("테스트 가게 1")
                .categoryIds(List.of(testCategoryId))
                .address("테스트 주소 1")
                .tel("010-1111-2222")
                .openStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        storeService.createStore(masterUserDetails, createRequest);

        createRequest = StoreCreateRequestDto.builder()
                .name("테스트 가게 2")
                .categoryIds(List.of(testCategoryId))
                .address("테스트 주소 2")
                .tel("010-3333-4444")
                .openStatus(true)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(21, 0))
                .build();

        storeService.createStore(masterUserDetails, createRequest);

        // 가게 목록 조회 메서드 호출
        ApiResponseDto<Page<StoreListResponseDto>> response = storeService.getAllStores(0, 10, "createdAt", true);

        // 결과 검증
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().getContent().size() > 0); // 가게가 있어야 함

        // 첫 번째 가게 검증
        StoreListResponseDto firstStore = response.getData().getContent().get(0);
        assertNotNull(firstStore);
        assertEquals("테스트 가게 1", firstStore.getName());
        assertEquals("테스트 주소 1", firstStore.getAddress());
        assertEquals("010-1111-2222", firstStore.getTel());
        assertTrue(firstStore.isOpenStatus());
    }


}

