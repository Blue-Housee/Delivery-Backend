package com.spring.delivery.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.delivery.domain.controller.dto.user.SignInRequestDto;
import com.spring.delivery.domain.controller.dto.user.SignUpRequestDto;
import com.spring.delivery.domain.controller.dto.user.UserUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTest {
    // 통합 테스트 시나리오
    /*

        test 사용자 :
        - customer
        - manager (로그인 한 사용자)
        - master

        1. 회원가입 (3 유저 모두 회원가입)

        2. manager가 customer 사용자의 정보 변경
        3. manager가 master 사용자의 정보 변경 (실패해야함)

        4. manager가 customer 사용자의 정보 확인
        5. manager가 master 사용자의 정보 확인

        6. manager가 customer 사용자의 정보 삭제(실패해야함)
        7. master가 customer 사용자의 정보 삭제

        8. manager가 전체 사용자 정보 확인

     */

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    private String customerToken;
    private String managerToken;
    private String masterToken;



    @BeforeAll
    void setUp() throws Exception {
        userRepository.deleteAll();
        customerToken = registerAndLogin("customer1", "customer1@test.com", Role.CUSTOMER);
        managerToken = registerAndLogin("manager1", "manager1@test.com", Role.MANAGER, ADMIN_TOKEN);
        masterToken = registerAndLogin("master1", "master1@test.com", Role.MASTER, ADMIN_TOKEN);
    }

    // 1. 회원가입 & 로그인 후 JWT 토큰을 저장
    private String registerAndLogin(String username, String email, Role role) throws Exception {
        return registerAndLogin(username, email, role, null);
    }

    private String registerAndLogin(String username, String email, Role role, String adminToken) throws Exception {
        SignUpRequestDto requestDto = SignUpRequestDto.builder()
                .username(username)
                .email(email)
                .password("test1234")
                .role(role)
                .adminToken(adminToken)
                .build();

        mockMvc.perform(post("/api/user/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        SignInRequestDto loginDto = SignInRequestDto.builder()
                .username(username)
                .password("test1234")
                .build();

        MvcResult result = mockMvc.perform(post("/api/user/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String token = new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("data").get("token").asText();
        System.out.println("token: " + token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        System.out.println("sliced token: " + token);
        return token;
    }

    // 2. manager가 customer 정보 수정
    @Test
    @Order(1)
    @DisplayName("manager가 customer 정보 변경 성공")
    void updateCustomerInfoByManager() throws Exception {
        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
                .email("customer1_updated@test.com")
                .build();

        MvcResult result = mockMvc.perform(patch("/api/user/" + "1")
                        .header("Authorization", "Bearer "+managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("result = " + result);
    }

    // 3. manager가 master 정보 수정 (실패해야 함)
    @Test
    @Order(2)
    @DisplayName("manager가 master 정보 변경 실패")
    void updateMasterInfoByManager_fail() throws Exception {
        UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
                .email("master1_updated@test.com")
                .build();

        mockMvc.perform(patch("/api/user/" + "3")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    // 4. manager가 customer 정보 확인
    @Test
    @Order(3)
    @DisplayName("manager가 customer 정보 조회")
    void getCustomerInfoByManager() throws Exception {
        mockMvc.perform(get("/api/user/" + "1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    // 5. manager가 master 정보 확인
    @Test
    @Order(4)
    @DisplayName("manager가 master 정보 조회")
    void getMasterInfoByManager() throws Exception {
        mockMvc.perform(get("/api/user/" + "3")
                        .header("Authorization","Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    // 6. manager가 customer 삭제 시도 (실패)
    @Test
    @Order(5)
    @DisplayName("manager가 customer 삭제 실패")
    void deleteCustomerByManager_fail() throws Exception {
        mockMvc.perform(delete("/api/user/" + "1")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    // 7. master가 customer 삭제 시도
    @Test
    @Order(6)
    @DisplayName("master가 customer 삭제 성공")
    void deleteCustomerByMaster_success() throws Exception {
        mockMvc.perform(delete("/api/user/" + "1")
                        .header("Authorization", "Bearer " + masterToken))
                .andExpect(status().isOk());
    }

    // 8. manager가 전체 사용자 정보 확인
    @Test
    @Order(7)
    @DisplayName("manager가 전체 사용자 조회")
    void getAllUsersByManager() throws Exception {
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }
}
