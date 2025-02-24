package com.spring.delivery.domain.service;

import com.spring.delivery.domain.config.IntegrationTestBase;
import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.*;
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

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class StoreServiceTest extends IntegrationTestBase {

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


    @Test
    @DisplayName("가게 등록 - 권한 있음")
    void testStoreCreationSuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-1234-5678",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto response = storeService.createStore(masterUserDetails, requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("요청이 성공적으로 처리되었습니다.", response.getMessage());

        UUID storeId = (UUID) response.getData();
        Store createdStore = storeRepository.findById(storeId).orElse(null);
        assertNotNull(createdStore);
        assertEquals(requestDto.getName(), createdStore.getName());
        assertEquals(requestDto.getAddress(), createdStore.getAddress());
        assertEquals(requestDto.getTel(), createdStore.getTel());
        assertEquals(requestDto.isOpenStatus(), createdStore.isOpenStatus());
        assertEquals(requestDto.getStartTime(), createdStore.getStartTime());
        assertEquals(requestDto.getEndTime(), createdStore.getEndTime());

        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        assertNotNull(storeCategories);
        assertEquals(1, storeCategories.size());
        assertEquals(testCategoryId, storeCategories.get(0).getCategory().getId());
    }

    @Test
    @DisplayName("가게 등록 - 권한 없음")
    void testStoreCreationFailDueToInsufficientPermissions() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-1234-5678",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto response = storeService.createStore(customerUserDetails, requestDto);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("가게를 등록할 권한이 없습니다.", response.getMessage());
    }

    @Test
    @DisplayName("가게 조회 - 전체 목록")
    void testGetAllStores() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest1 = new StoreCreateRequestDto(
                "테스트 가게 1",
                List.of(testCategoryId),
                "테스트 주소 1",
                "010-1111-2222",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        storeService.createStore(masterUserDetails, createRequest1);

        StoreCreateRequestDto createRequest2 = new StoreCreateRequestDto(
                "테스트 가게 2",
                List.of(testCategoryId),
                "테스트 주소 2",
                "010-3333-4444",
                true,
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );

        storeService.createStore(masterUserDetails, createRequest2);

        ApiResponseDto<Page<StoreListResponseDto>> response = storeService.getAllStores(0, 10, "createdAt", true);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().getContent().size() > 0);

        StoreListResponseDto firstStore = response.getData().getContent().get(0);
        assertNotNull(firstStore);
        assertEquals("테스트 가게 1", firstStore.getName());
        assertEquals("테스트 주소 1", firstStore.getAddress());
        assertEquals("010-1111-2222", firstStore.getTel());
        assertTrue(firstStore.isOpenStatus());
    }

    @Test
    @DisplayName("가게 조회 - 특정 가게 ID로 조회")
    void testGetStoreById() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-5555-6666",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto<UUID> createResponse = storeService.createStore(masterUserDetails, createRequest);
        UUID storeId = createResponse.getData();

        ApiResponseDto<StoreDetailResponseDto> response = storeService.getStoreById(storeId);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        StoreDetailResponseDto storeDetail = response.getData();
        assertEquals(storeId, storeDetail.getStoreId());
        assertEquals("테스트 가게", storeDetail.getName());
        assertEquals("테스트 주소", storeDetail.getAddress());
        assertEquals("010-5555-6666", storeDetail.getTel());
        assertTrue(storeDetail.isOpenStatus());
    }

    @Test
    @DisplayName("가게 수정 - 권한 있음")
    void testUpdateStoreSuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-7777-8888",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto<UUID> createResponse = storeService.createStore(masterUserDetails, createRequest);
        UUID storeId = createResponse.getData();

        StoreUpdateRequestDto updateRequest = new StoreUpdateRequestDto(
                "수정된 가게 이름",
                List.of(testCategoryId),
                "수정된 주소",
                "010-9999-0000",
                true,
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );

        ApiResponseDto<StoreUpdateResponseDto> response = storeService.updateStore(masterUserDetails, storeId, updateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        StoreUpdateResponseDto updatedStore = response.getData();
        assertEquals(storeId, updatedStore.getId());
        assertEquals("수정된 가게 이름", updatedStore.getName());
        assertEquals("수정된 주소", updatedStore.getAddress());
        assertEquals("010-9999-0000", updatedStore.getTel());
        assertEquals(LocalTime.of(10, 0), updatedStore.getStartTime());
        assertEquals(LocalTime.of(21, 0), updatedStore.getEndTime());
    }

    @Test
    @DisplayName("가게 수정 - 권한 없음")
    void testUpdateStoreFailDueToInsufficientPermissions() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-4444-5555",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto<UUID> createResponse = storeService.createStore(masterUserDetails, createRequest);
        UUID storeId = createResponse.getData();

        StoreUpdateRequestDto updateRequest = new StoreUpdateRequestDto(
                "수정된 가게 이름",
                List.of(testCategoryId),
                "수정된 주소",
                "010-9999-0000",
                true,
                LocalTime.of(10, 0),
                LocalTime.of(21, 0)
        );

        ApiResponseDto response = storeService.updateStore(customerUserDetails, storeId, updateRequest);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("가게를 수정할 권한이 없습니다.", response.getMessage());
    }

    @Test
    @DisplayName("가게 삭제 - 권한 있음")
    void testDeleteStoreSuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-8888-9999",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto<UUID> createResponse = storeService.createStore(masterUserDetails, createRequest);
        UUID storeId = createResponse.getData();

        ApiResponseDto response = storeService.deleteStore(masterUserDetails, storeId);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("요청이 성공적으로 처리되었습니다.", response.getMessage());

        Store deletedStore = storeRepository.findById(storeId).orElse(null);
        assertNotNull(deletedStore);
        assertNotNull(deletedStore.getDeletedAt());
    }

    @Test
    @DisplayName("가게 삭제 - 권한 없음")
    void testDeleteStoreFailDueToInsufficientPermissions() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트용 카테고리 생성
        Category testCategory = Category.of("testcategory");
        testCategory = categoryRepository.save(testCategory);
        UUID testCategoryId = testCategory.getId();

        StoreCreateRequestDto createRequest = new StoreCreateRequestDto(
                "테스트 가게",
                List.of(testCategoryId),
                "테스트 주소",
                "010-0000-1111",
                true,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );

        ApiResponseDto<UUID> createResponse = storeService.createStore(masterUserDetails, createRequest);
        UUID storeId = createResponse.getData();

        ApiResponseDto response = storeService.deleteStore(customerUserDetails, storeId);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("가게를 삭제할 권한이 없습니다.", response.getMessage());
    }
}

