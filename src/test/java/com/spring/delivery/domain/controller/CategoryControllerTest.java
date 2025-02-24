package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryDeleteResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryListResponseDto;
import com.spring.delivery.domain.controller.dto.category.CategoryRequestDto;
import com.spring.delivery.domain.controller.dto.category.CategoryUpdateResponseDto;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.domain.service.CategoryService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)  // CSRF 필터 비활성화
@ActiveProfiles("test")
@Transactional
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // UserRepository를 모킹합니다.
    private UserRepository userRepository;

    @MockitoBean // CategoryService를 모킹합니다.
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        User user = User.createUser("test", "test@test.com", "1234", Role.MASTER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    }


    @WithMockUser(username = "test", roles = "MASTER") // MASTER 역할을 가진 사용자로 설정
    @DisplayName("카테고리 등록 테스트")
    @Test
    void createCategory() throws Exception {
        // Given
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("testCategory")
                .build();

        ApiResponseDto responseDto = ApiResponseDto.success("요청이 성공적으로 처리되었습니다.");
        when(categoryService.createCategory(any(UserDetailsImpl.class), any(CategoryRequestDto.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(post("/api/categories")
                        .with(csrf())  // CSRF 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"testCategory\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."));

    }

    @WithMockUser(username = "test", roles = "MASTER")
    @DisplayName("카테고리 조회 테스트")
    @Test
    void getAllCategories() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        CategoryListResponseDto categoryListResponseDto = new CategoryListResponseDto(
                categoryId,
                "testCategory",
                null
        );

        // 카테고리 목록을 포함하는 Page 객체 생성
        Page<CategoryListResponseDto> categoryList = new PageImpl<>(List.of(categoryListResponseDto), PageRequest.of(0, 10), 1);

        // ApiResponseDto에 Page 객체 설정
        ApiResponseDto<Page<CategoryListResponseDto>> responseDto = ApiResponseDto.success(categoryList);

        when(categoryService.getAllCategories(any(UserDetailsImpl.class), anyInt(), anyInt(), anyString(), anyBoolean()))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(get("/api/categories?page=1&size=10&sortBy=name&isAsc=true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("testCategory")); // 첫 번째 카테고리 이름 검증
    }

    @WithMockUser(username = "test", roles = "MASTER")
    @DisplayName("카테고리 수정 테스트")
    @Test
    void updateCategory() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();  // UUID 생성
        String updatedCategoryName = "updatedCategory";

        // Category 객체를 모킹
        Category category = Category.of("updatedCategory");

        // CategoryUpdateResponseDto 객체 생성
        CategoryUpdateResponseDto categoryUpdateResponseDto = new CategoryUpdateResponseDto(
                category.getId(),
                category.getName(),
                category.getUpdatedAt()
        );

        // ApiResponseDto에 CategoryUpdateResponseDto 설정
        ApiResponseDto<CategoryUpdateResponseDto> responseDto = ApiResponseDto.success(categoryUpdateResponseDto);

        when(categoryService.updateCategory(any(UserDetailsImpl.class), any(UUID.class), any(CategoryRequestDto.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(patch("/api/categories/" + categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + updatedCategoryName + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value(updatedCategoryName)); // 수정된 카테고리 이름 검증
    }

    @WithMockUser(username = "test", roles = "MASTER")
    @DisplayName("카테고리 삭제 테스트")
    @Test
    void deleteCategory() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();  // UUID 생성

        // 삭제된 카테고리의 삭제 시각
        LocalDateTime deletedAt = LocalDateTime.now();

        // CategoryDeleteResponseDto 객체 생성
        CategoryDeleteResponseDto categoryDeleteResponseDto = new CategoryDeleteResponseDto(
                "카테고리가 삭제(숨김 처리)되었습니다.",
                deletedAt // 삭제된 시각을 포함
        );

        // ApiResponseDto에 CategoryDeleteResponseDto 설정
        ApiResponseDto<CategoryDeleteResponseDto> responseDto = ApiResponseDto.success(categoryDeleteResponseDto);

        when(categoryService.deleteCategory(any(UserDetailsImpl.class), any(UUID.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.message").value("카테고리가 삭제(숨김 처리)되었습니다.")); // 삭제 메시지 검증
    }


}
