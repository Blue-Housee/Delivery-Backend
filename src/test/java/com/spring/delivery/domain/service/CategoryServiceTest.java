package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
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
import org.springframework.test.context.ActiveProfiles;

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
    private CategoryRequestDto requestDto;
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
    }

    @Test
    @Order(1)
    @DisplayName("카테고리 생성 - 권한 있음")
    void testCreateCategorySuccess() {
        requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        // 카테고리 생성 메서드 호출
        ApiResponseDto<UUID> response = categoryService.createCategory(masterUserDetails, requestDto);

        // 결과 검증
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());

        // 데이터베이스에서 카테고리 조회
        testCategoryId = response.getData();
        Category createdCategory = categoryRepository.findById(testCategoryId).orElse(null);
        assertNotNull(createdCategory);
        assertEquals(requestDto.getName(), createdCategory.getName());
    }

    @Test
    @Order(2)
    @DisplayName("카테고리 생성 - 권한 없음")
    void testCreateCategoryFailDueToInsufficientPermissions() {
        requestDto = CategoryRequestDto.builder()
                .name("테스트 카테고리")
                .build();

        // 권한이 없는 유저로 카테고리 생성 메서드 호출
        ApiResponseDto response = categoryService.createCategory(customerUserDetails, requestDto);

        // 결과 검증
        assertNotNull(response);
        assertEquals(403, response.getStatus());
        assertEquals("권한이 없습니다.", response.getMessage());
    }

}