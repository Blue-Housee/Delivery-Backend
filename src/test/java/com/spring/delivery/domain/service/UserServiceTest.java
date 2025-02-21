package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.user.SignUpRequestDto;
import com.spring.delivery.domain.controller.dto.user.UserUpdateRequestDto;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    private static String PASSWORD = "test1234";
    private static String UPDATED_EMAIL = "updated@test.com";

    private User customer;
    private User manager;
    private User master;

    private UserDetailsImpl customerUserDetails;
    private UserDetailsImpl managerUserDetails;
    private UserDetailsImpl masterUserDetails;

    @BeforeAll
    void setUp() {
        // 공동 테스트 데이터 초기화
        customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
        manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
        master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);

        customerUserDetails = new UserDetailsImpl(customer);
        managerUserDetails = new UserDetailsImpl(manager);
        masterUserDetails = new UserDetailsImpl(master);
    }

    //-- 회원가입 --
    @Nested
    @DisplayName("회원가입")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SignUp {
        @BeforeAll
        void init() {
            userRepository.deleteAll();  // 기존 데이터 초기화
        }

        @Test
        @DisplayName("일반 회원가입 성공")
        @Transactional
        void signUp_customer_success() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(customer, Role.CUSTOMER, null);

            //when
            User savedUser = userService.signup(requestDto);

            //then
            assertEquals(customer.getUsername(), savedUser.getUsername());
        }

        @Test
        @DisplayName("관리자 회원가입 성공")
        @Transactional
        void signUp_manager_success() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(manager, Role.MANAGER, ADMIN_TOKEN);
            //when
            User savedUser = userService.signup(requestDto);

            //then
            assertEquals(manager.getUsername(), savedUser.getUsername());
            assertEquals(savedUser.getRole(), Role.MANAGER);
        }

        @Test
        @DisplayName("회원가입 실패 - 관리자 암호 불일치")
        @Transactional
        void signUp_manager_fail() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(manager, Role.MANAGER, "");

            //when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(requestDto);
            });
            assertEquals("관리자 암호가 틀려 등록이 불가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 username")
        @Transactional
        void signUp_fail_duplicated_username() {
            //given
            SignUpRequestDto firstRequestDto = createSignUpRequest(customer, Role.CUSTOMER, null);

            SignUpRequestDto secondRequestDto = SignUpRequestDto.builder()
                    .username(customer.getUsername())
                    .email(manager.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();

            //when
            userService.signup(firstRequestDto); //첫번째

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(secondRequestDto); //두번째
            });

            //then
            assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 email")
        @Transactional
        void signUp_fail_duplicated_email() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(customer, Role.CUSTOMER, null);

            SignUpRequestDto secondRequestDto = SignUpRequestDto.builder()
                    .username(manager.getUsername())
                    .email(customer.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();

            //when
            userService.signup(requestDto); //첫번째

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(secondRequestDto); //두번째
            });

            //then
            assertEquals("중복된 Email 입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 단건 조회")
    class Read {
        @Test
        @DisplayName("회원 단건 조회 성공")
        void getUser_success() {
            User user = userService.getUser(customer.getId(), customerUserDetails);

            assertEquals(user.getUsername(), customer.getUsername());
        }
    }

    @Nested
    @DisplayName("회원 변경")
    class Update {
        @Test
        @DisplayName("회원 변경 성공")
        @Transactional
        void updateUser_success() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .build();

            User updatedUser = userService.updateUser(customer.getId(), requestDto, customerUserDetails);

            assertEquals(UPDATED_EMAIL, updatedUser.getEmail());
        }
    }

    @Nested
    @DisplayName("회원 삭제")
    class Delete {
        @Test
        @DisplayName("회원 삭제 성공")
        @Transactional
        void deleteUser_success() {
            User deletedUser = userService.deleteUser(customer.getId(), customerUserDetails);

            assertEquals(deletedUser.getUsername(), customer.getUsername());
            assertEquals(deletedUser.getRole(), Role.CUSTOMER);
            assertNotNull(deletedUser.getDeletedAt());
            assertNotNull(deletedUser.getDeletedBy());
            assertEquals(deletedUser.getDeletedBy(), customerUserDetails.getUsername());
        }
    }

    @Nested
    @DisplayName("회원 검색")
    class Search {
        @Test
        @DisplayName("회원 검색 성공")
        void searchUsers_success() {
            Page<User> userList = userService.searchUsers(managerUserDetails, 0, 5, "ma");

            assertEquals(2, userList.getTotalElements());
            assertEquals(master.getUsername(), userList.getContent().get(0).getUsername());
            assertEquals(manager.getUsername(), userList.getContent().get(1).getUsername());
        }
    }


    // 내부 메서드
    private User createAndSaveUser(String username, String email, Role role) {
        User user = User.createUser(username, email, PASSWORD, role);
        return userRepository.save(user);
    }

    private SignUpRequestDto createSignUpRequest(User user, Role role, String adminToken) {
        return SignUpRequestDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(role)
                .adminToken(adminToken)
                .build();
    }

}