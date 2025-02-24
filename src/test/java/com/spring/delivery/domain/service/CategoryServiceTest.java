package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryListResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryRequestDto;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 생성 - 권한 있음")
    void testCreateCategorySuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto<UUID> response = categoryService.createCategory(masterUserDetails, requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        Category createdCategory = categoryRepository.findById(response.getData()).orElse(null);
        assertNotNull(createdCategory);
        assertEquals(requestDto.getName(), createdCategory.getName());
    }

    @Test
    @DisplayName("카테고리 생성 - 권한 없음")
    void testCreateCategoryFailDueToInsufficientPermissions() {
        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto response = categoryService.createCategory(customerUserDetails, requestDto);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());
    }

    @Test
    @DisplayName("카테고리 조회 - 전체 목록")
    void testGetAllCategories() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        for (int i = 1; i <= 5; i++) {
            CategoryRequestDto createRequest = CategoryRequestDto.builder()
                    .name("테스트 카테고리 " + i)
                    .build();
            categoryService.createCategory(masterUserDetails, createRequest);
        }

        ApiResponseDto response = categoryService.getAllCategories(masterUserDetails, 0, 10, "name", true);

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        List<CategoryListResponseDto> categories = ((Page<CategoryListResponseDto>) response.getData()).getContent();
        assertTrue(categories.size() >= 5);
    }

    @Test
    @DisplayName("카테고리 수정 - 권한 있음")
    void testUpdateCategorySuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        CategoryRequestDto updateRequest = CategoryRequestDto.builder()
                .name("수정된 카테고리 이름")
                .build();

        ApiResponseDto response = categoryService.updateCategory(masterUserDetails, categoryId, updateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        Category updatedCategory = categoryRepository.findById(categoryId).orElse(null);
        assertNotNull(updatedCategory);
        assertEquals(updateRequest.getName(), updatedCategory.getName());
    }

    @Test
    @DisplayName("카테고리 수정 - 권한 없음")
    void testUpdateCategoryFailDueToInsufficientPermissions() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        CategoryRequestDto updateRequest = CategoryRequestDto.builder()
                .name("수정된 카테고리 이름")
                .build();

        ApiResponseDto response = categoryService.updateCategory(customerUserDetails, categoryId, updateRequest);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());
    }

    @Test
    @DisplayName("카테고리 삭제 - 권한 있음")
    void testDeleteCategorySuccess() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        // 카테고리 생성
        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        // 카테고리 삭제 메서드 호출
        ApiResponseDto response = categoryService.deleteCategory(masterUserDetails, categoryId);

        // 결과 검증
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("요청이 성공적으로 처리되었습니다.", response.getMessage());

        // 데이터베이스에서 카테고리 조회 후 삭제 여부 확인
        Category deletedCategory = categoryRepository.findById(categoryId).orElse(null);
        assertNotNull(deletedCategory);
        assertNotNull(deletedCategory.getDeletedAt()); // 삭제된 카테고리의 deletedAt이 설정되어 있어야 함
    }

    @Test
    @DisplayName("카테고리 삭제 - 권한 없음")
    void testDeleteCategoryFailDueToInsufficientPermissions() {
        // MASTER 권한을 가진 유저 생성
        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        UserDetailsImpl masterUserDetails = new UserDetailsImpl(masterUser);

        // CUSTOMER 권한을 가진 유저 생성
        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        UserDetailsImpl customerUserDetails = new UserDetailsImpl(customerUser);

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        // 카테고리 생성
        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        // 권한이 없는 유저로 카테고리 삭제 메서드 호출
        ApiResponseDto response = categoryService.deleteCategory(customerUserDetails, categoryId);

        // 결과 검증
        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());

        // 삭제 후 카테고리 조회
        Category existingCategory = categoryRepository.findById(categoryId).orElse(null);
        assertNotNull(existingCategory); // 카테고리는 여전히 존재해야 함
        assertNull(existingCategory.getDeletedAt()); // 삭제되지 않아야 함
    }
}
