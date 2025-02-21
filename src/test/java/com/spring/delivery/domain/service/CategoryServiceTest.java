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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    private UserDetailsImpl masterUserDetails;
    private UserDetailsImpl customerUserDetails;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User masterUser = User.createUser("masterUser", "masterUser@example.com", "password", Role.MASTER);
        userRepository.save(masterUser);
        masterUserDetails = new UserDetailsImpl(masterUser);

        User customerUser = User.createUser("customerUser", "customerUser@example.com", "password", Role.CUSTOMER);
        userRepository.save(customerUser);
        customerUserDetails = new UserDetailsImpl(customerUser);
    }

    @Test
    @Order(1)
    @DisplayName("카테고리 생성 - 권한 있음")
    @Transactional
    void testCreateCategorySuccess() {
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
    @Order(2)
    @DisplayName("카테고리 생성 - 권한 없음")
    @Transactional
    void testCreateCategoryFailDueToInsufficientPermissions() {
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto response = categoryService.createCategory(customerUserDetails, requestDto);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("카테고리 조회 - 전체 목록")
    @Transactional
    void testGetAllCategories() {
        for (int i = 1; i <= 5; i++) {
            CategoryRequestDto createRequest = CategoryRequestDto.builder()
                    .name("테스트 카테고리 " + i)
                    .build();
            categoryService.createCategory(masterUserDetails, createRequest);
        }

        ApiResponseDto<?> response = categoryService.getAllCategories(masterUserDetails, 0, 10, "name", true);

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        List<CategoryListResponseDto> categories = ((Page<CategoryListResponseDto>) response.getData()).getContent();
        assertTrue(categories.size() >= 5);
    }

    @Test
    @Order(4)
    @DisplayName("카테고리 수정 - 권한 있음")
    @Transactional
    void testUpdateCategorySuccess() {
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        CategoryRequestDto updateRequest = CategoryRequestDto.builder()
                .name("수정된 카테고리 이름")
                .build();

        ApiResponseDto<?> response = categoryService.updateCategory(masterUserDetails, categoryId, updateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatus());

        Category updatedCategory = categoryRepository.findById(categoryId).orElse(null);
        assertNotNull(updatedCategory);
        assertEquals(updateRequest.getName(), updatedCategory.getName());
    }

    @Test
    @Order(5)
    @DisplayName("카테고리 수정 - 권한 없음")
    @Transactional
    void testUpdateCategoryFailDueToInsufficientPermissions() {
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        ApiResponseDto<UUID> createResponse = categoryService.createCategory(masterUserDetails, requestDto);
        UUID categoryId = createResponse.getData();

        CategoryRequestDto updateRequest = CategoryRequestDto.builder()
                .name("수정된 카테고리 이름")
                .build();

        ApiResponseDto<?> response = categoryService.updateCategory(customerUserDetails, categoryId, updateRequest);

        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());
    }
}
