package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreCreateRequestDto;
import com.spring.delivery.domain.controller.dto.store.StoreDetailResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreListResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.domain.service.StoreService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        User user = User.createUser("test", "test@test.com", "1234", Role.MASTER);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    }

    @WithMockUser(username = "test", roles = "MASTER")
    @DisplayName("스토어 등록 테스트")
    @Test
    void createStore() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID(); // 카테고리 ID 생성
        StoreCreateRequestDto requestDto = new StoreCreateRequestDto(
                "Test Store",
                Collections.singletonList(categoryId), // 카테고리 ID 추가
                "123 Test St.",
                "010-1234-5678",
                true,
                LocalTime.of(9, 0), // 시작 시간
                LocalTime.of(21, 0)  // 종료 시간
        );

        ApiResponseDto responseDto = ApiResponseDto.success("스토어가 성공적으로 생성되었습니다.");
        when(storeService.createStore(any(UserDetailsImpl.class), any(StoreCreateRequestDto.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(post("/api/stores")  // 스토어 생성 API 경로
                        .with(csrf())  // CSRF 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Store\", " +
                                "\"categoryIds\":[\"" + categoryId + "\"], " + // 카테고리 ID 포함
                                "\"address\":\"123 Test St.\", " +
                                "\"tel\":\"010-1234-5678\", " +
                                "\"openStatus\":true, " +
                                "\"startTime\":\"09:00\", " +
                                "\"endTime\":\"21:00\"}")) // 시작 및 종료 시간
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."));
    }

    @WithMockUser(username = "test")
    @DisplayName("스토어 전체 조회 테스트")
    @Test
    void getAllStores() throws Exception {
        // Given
        UUID storeId1 = UUID.randomUUID();
        UUID storeId2 = UUID.randomUUID();

        StoreListResponseDto store1 = new StoreListResponseDto(
                storeId1,
                "테스트 가게 1", // 가게 이름
                "테스트 주소 1", // 가게 주소
                "010-1111-2222", // 전화번호
                true,            // 운영 상태
                List.of("카테고리 1", "카테고리 2"), // 카테고리 리스트
                LocalTime.of(9, 0),  // 시작 시간
                LocalTime.of(22, 0), // 종료 시간
                4.5 // 평균 평점 더미 데이터 추가
        );

        StoreListResponseDto store2 = new StoreListResponseDto(
                storeId2,
                "테스트 가게 2", // 가게 이름
                "테스트 주소 2", // 가게 주소
                "010-3333-4444", // 전화번호
                true,            // 운영 상태
                List.of("카테고리 1"), // 카테고리 리스트
                LocalTime.of(10, 0),  // 시작 시간
                LocalTime.of(21, 0),   // 종료 시간
                3.8 // 평균 평점 더미 데이터 추가
        );

        // PageImpl을 사용하여 페이지 객체 생성
        Page<StoreListResponseDto> page = new PageImpl<>(List.of(store1, store2), PageRequest.of(0, 10), 2);
        ApiResponseDto<Page<StoreListResponseDto>> responseDto = ApiResponseDto.success(page); // 페이지 초기화

        // StoreService의 메서드 모킹
        when(storeService.getAllStores(any(int.class), any(int.class), any(String.class), any(boolean.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(get("/api/stores?page=1&size=10&sortBy=name&isAsc=true") // 전체 조회 API 경로
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value(store1.getName())) // 첫 번째 스토어 이름 확인
                .andExpect(jsonPath("$.data.content[0].averageRating").value(4.5)) // 평균 평점 확인
                .andExpect(jsonPath("$.data.content[1].name").value(store2.getName())) // 두 번째 스토어 이름 확인
                .andExpect(jsonPath("$.data.content[1].averageRating").value(3.8)); // 평균 평점 확인
    }


    @WithMockUser(username = "test")
    @DisplayName("스토어 단건 조회 테스트")
    @Test
    void getStoreById() throws Exception {
        // Given
        UUID storeId = UUID.randomUUID();

        StoreDetailResponseDto storeDetail = new StoreDetailResponseDto(
                storeId,
                "테스트 가게", // 가게 이름
                "테스트 주소", // 가게 주소
                "010-5555-6666", // 전화번호
                true,            // 운영 상태
                LocalTime.of(9, 0), // 시작 시간
                LocalTime.of(22, 0), // 종료 시간
                List.of("카테고리 1", "카테고리 2") // 카테고리 리스트
        );

        ApiResponseDto<StoreDetailResponseDto> responseDto = ApiResponseDto.success(storeDetail);

        when(storeService.getStoreById(storeId)).thenReturn(responseDto);

        // When
        mockMvc.perform(get("/api/stores/" + storeId) // 단건 조회 API 경로
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.storeId").value(storeId.toString())); // 스토어 ID 확인
    }

    @WithMockUser(username = "test", roles = "MASTER") // MASTER 역할을 가진 사용자로 설정
    @DisplayName("스토어 수정 테스트")
    @Test
    void updateStore() throws Exception {
        // Given
        UUID storeId = UUID.randomUUID(); // 스토어 ID 생성
        StoreUpdateRequestDto requestDto = new StoreUpdateRequestDto(
                "수정된 가게 이름",
                List.of(UUID.randomUUID()), // 카테고리 ID 리스트
                "수정된 주소",
                "010-9999-0000",
                true, // 운영 상태
                LocalTime.of(10, 0), // 수정된 시작 시간
                LocalTime.of(21, 0)   // 수정된 종료 시간
        );

        ApiResponseDto responseDto = ApiResponseDto.success("스토어가 성공적으로 수정되었습니다.");
        when(storeService.updateStore(any(UserDetailsImpl.class), any(UUID.class), any(StoreUpdateRequestDto.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(patch("/api/stores/" + storeId) // 스토어 수정 API 경로
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"수정된 가게 이름\", " +
                                "\"categoryIds\":[\"" + UUID.randomUUID() + "\"], " + // 카테고리 ID 포함
                                "\"address\":\"수정된 주소\", " +
                                "\"tel\":\"010-9999-0000\", " +
                                "\"openStatus\":true, " +
                                "\"startTime\":\"10:00\", " +
                                "\"endTime\":\"21:00\"}")) // 수정된 시작 및 종료 시간
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."));
    }

    @WithMockUser(username = "test", roles = "MASTER") // MASTER 역할을 가진 사용자로 설정
    @DisplayName("스토어 삭제 테스트")
    @Test
    void deleteStore() throws Exception {
        // Given
        UUID storeId = UUID.randomUUID(); // 삭제할 스토어 ID 생성

        ApiResponseDto responseDto = ApiResponseDto.success("스토어가 성공적으로 삭제되었습니다.");
        when(storeService.deleteStore(any(UserDetailsImpl.class), any(UUID.class)))
                .thenReturn(responseDto);

        // When
        mockMvc.perform(delete("/api/stores/" + storeId) // 스토어 삭제 API 경로
                        .with(csrf()) // CSRF 토큰 포함
                        .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."));
    }


}
