package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.user.*;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    private static String PASSWORD = "test1234";
    private static String UPDATED_PASSWORD = "update1234";
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

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();  // 테스트 시작 전에 SecurityContext 초기화
    }

    //-- 회원가입 --
    @Nested
    @DisplayName("회원가입")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SignUp {
        @BeforeAll
        void init() {
            userRepository.deleteAll();// 기존 데이터 초기화
        }

        @Test
        @DisplayName("일반 회원가입 성공")
        @Transactional
        void signUp_customer_success() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(customer, Role.CUSTOMER, null);

            //when
            SignUpResponseDto savedUser = userService.signup(requestDto);
            User user = userRepository.findById(savedUser.getUserId()).orElse(null);

            //then
            if (user != null) {
                assertEquals(customer.getUsername(), user.getUsername());
            }
        }

        @Test
        @DisplayName("관리자 회원가입 성공")
        @Transactional
        void signUp_manager_success() {
            //given
            SignUpRequestDto requestDto = createSignUpRequest(manager, Role.MANAGER, ADMIN_TOKEN);
            //when
            SignUpResponseDto savedUser = userService.signup(requestDto);
            User user = userRepository.findById(savedUser.getUserId()).orElse(null);

            //then
            assertEquals(manager.getUsername(), user.getUsername());
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
        @DisplayName("회원 단건 조회 성공 - customer 본인")
        void getUser_success_customer() {
            setUpSecurityContext("customer");

            UserDetailsResponseDto user = userService.getUser(customer.getId(), customerUserDetails);

            assertEquals(user.getUsername(), customer.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 성공 - manager")
        void getUser_success_manager() {
            setUpSecurityContext("manager");

            UserDetailsResponseDto user = userService.getUser(manager.getId(), managerUserDetails);

            assertEquals(user.getUsername(), manager.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 성공 - master")
        void getUser_success_master() {
            setUpSecurityContext("master");

            UserDetailsResponseDto user = userService.getUser(master.getId(), masterUserDetails);

            assertEquals(user.getUsername(), master.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 실패 - 본인 제외 customer")
        void getUser_fail_customer() {
            setUpSecurityContext("customer");
            User customer2 = createAndSaveUser("customer2", "customer2@test.com", Role.CUSTOMER);

            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.getUser(customer2.getId(), customerUserDetails);
            });

            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 단건 조회 실패 - 존재하지 않는 id")
        void getUser_fail_not_found_id() {
            Long userId = 100L;
            setUpSecurityContext("manager");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getUser(userId, managerUserDetails);
            });

            assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 변경")
    class Update {
        @Test
        @DisplayName("회원 변경 성공 - 본인, 이메일 변경")
        @Transactional
        void updateUser_success_customer_self_email() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .build();

            setUpSecurityContext("customer");

            UserDetailsResponseDto updatedUser = userService.updateUser(customer.getId(), requestDto, customerUserDetails);

            assertEquals(UPDATED_EMAIL, updatedUser.getEmail());
        }

        @Test
        @DisplayName("회원 변경 성공 - MANAGER -> CUSTOMER 비밀번호 변경")
        @Transactional
        void updateUser_success_mc_password() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext("manager");

            UserDetailsResponseDto updatedUser = userService.updateUser(customer.getId(), requestDto, managerUserDetails);

            assertEquals(customer.getId(), updatedUser.getUserId());
        }

        @Test
        @DisplayName("회원 변경 성공 - MASTER -> MANAGER 모든 정보 변경")
        @Transactional
        void updateUser_success_mm_all() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(manager.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext("master");

            UserDetailsResponseDto updatedUser = userService.updateUser(manager.getId(), requestDto, managerUserDetails);

            assertEquals(UPDATED_EMAIL, updatedUser.getEmail());
            assertEquals(manager.getId(), updatedUser.getUserId());
        }

        @Test
        @DisplayName("회원 변경 실패 - MANAGER -> MASTER (상위 권한자) 정보 변경 시도")
        @Transactional
        void updateUser_fail_update_upper_lever_user() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(master.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext("manager");

            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.updateUser(master.getId(), requestDto, managerUserDetails);
            });

            assertEquals("접근 권한이 없는 사용자입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 기존 비밀번호 매칭 실패")
        @Transactional
        void updateUser_fail_password_does_not_match() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(UPDATED_PASSWORD)
                    .build();

            setUpSecurityContext("customer");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(customer.getId(), requestDto, customerUserDetails);
            });

            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 본인 제외 customer")
        @Transactional
        void updateUser_fail_customer() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();
            setUpSecurityContext("customer");
            User customer2 = createAndSaveUser("customer2", "customer2@test.com", Role.CUSTOMER);

            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.updateUser(customer2.getId(), requestDto, customerUserDetails);
            });

            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 존재하지 않는 id")
        @Transactional
        void updateUser_fail_not_found_id() {
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            Long userId = 100L;
            setUpSecurityContext("manager");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(userId, requestDto, customerUserDetails);
            });

            assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 삭제")
    class Delete {
        @Test
        @DisplayName("회원 삭제 성공 - CUSTOMER 본인")
        @Transactional
        void deleteUser_success_customer_self() {
            setUpSecurityContext("customer");

            UserDeleteResponseDto deletedUser = userService.deleteUser(customer.getId(), customerUserDetails);

            assertEquals(deletedUser.getUserId(), customer.getId());

            User user = userRepository.findById(deletedUser.getUserId()).orElse(null);
            if (user != null) {
                assertEquals(user.getRole(), Role.CUSTOMER);
                assertNotNull(user.getDeletedAt());
                assertNotNull(user.getDeletedBy());
                assertEquals(user.getDeletedBy(), customerUserDetails.getUsername());
            }
        }

        @Test
        @DisplayName("회원 삭제 성공 - MASTER")
        @Transactional
        void deleteUser_success_master() {
            setUpSecurityContext("master");

            UserDeleteResponseDto deletedUser = userService.deleteUser(customer.getId(), masterUserDetails);

            assertEquals(deletedUser.getUserId(), customer.getId());

            User user = userRepository.findById(deletedUser.getUserId()).orElse(null);
            if (user != null) {
                assertEquals(user.getRole(), Role.CUSTOMER);
                assertNotNull(user.getDeletedAt());
                assertNotNull(user.getDeletedBy());
                assertEquals(user.getDeletedBy(), masterUserDetails.getUsername());
            }
        }

        @Test
        @DisplayName("회원 삭제 실패 - MANAGER")
        @Transactional
        void deleteUser_fail_manager() {
            setUpSecurityContext("manager");

            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.deleteUser(customer.getId(), managerUserDetails);
            });

            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 실패 - 존재하지 않는 id")
        @Transactional
        void deleteUser_fail_id_does_not_found() {
            Long userId = 100L;
            setUpSecurityContext("master");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteUser(userId, masterUserDetails);
            });

            assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 실패 - 삭제된 id")
        @Transactional
        void deleteUser_fail_id_deleted_in_advance() {
            setUpSecurityContext("master");

            userService.deleteUser(customer.getId(), masterUserDetails);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteUser(customer.getId(), masterUserDetails);
            });

            assertEquals("이미 삭제된 유저입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 검색")
    class Search {
        @Test
        @DisplayName("회원 검색 성공 - MANAGER")
        void searchUsers_success_manager() {
            setUpSecurityContext("manager");

            UserPageResponseDto userList = userService.searchUsers(managerUserDetails, 0, 5, "createdAt", "DESC", "ma");

            assertEquals(master.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(manager.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 성공 - MASTER")
        void searchUsers_success_master() {
            setUpSecurityContext("master");

            UserPageResponseDto userList = userService.searchUsers(masterUserDetails, 0, 5, "createdAt", "DESC", "ma");

            assertEquals(master.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(manager.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 성공 - 정렬 ASC로")
        void searchUsers_success_orderby_createdAt_sort_asc() {
            setUpSecurityContext("master");

            UserPageResponseDto userList = userService.searchUsers(masterUserDetails, 0, 5, "createdAt", "ASC", "ma");

            assertEquals(manager.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(master.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 실패 - CUSTOMER")
        void searchUsers_success_customer() {
            setUpSecurityContext("customer");

            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                UserPageResponseDto userList = userService.searchUsers(customerUserDetails, 0, 5, "createdAt", "ASC", "ma");
            });

            assertEquals("Access Denied", exception.getMessage());
        }
    }


    // 내부 메서드
    private User createAndSaveUser(String username, String email, Role role) {
        User user = User.createUser(username, email, passwordEncoder.encode(PASSWORD), role);
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

    void setUpSecurityContext(String userType) {
        if (userType.equals("customer")) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(customerUserDetails, null, customerUserDetails.getAuthorities())
            );
        } else if (userType.equals("manager")) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(managerUserDetails, null, managerUserDetails.getAuthorities())
            );
        } else {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(masterUserDetails, null, masterUserDetails.getAuthorities())
            );
        }
    }

}