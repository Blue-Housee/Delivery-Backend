package com.spring.delivery.domain.service;

import com.spring.delivery.domain.config.IntegrationTestBase;
import com.spring.delivery.domain.controller.dto.user.*;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest extends IntegrationTestBase {

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

    @BeforeEach
    void SetSecurityContextHolder() {
        SecurityContextHolder.clearContext();  // 테스트 시작 전에 SecurityContext 초기화
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("일반 회원가입 성공")
        void signUp_customer_success() {
            //given
            User customer = User.createUser("customer1", "customer1@test.com", PASSWORD, Role.CUSTOMER);
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
        void signUp_manager_success() {
            //given
            User manager = User.createUser("manager1", "manager1@test.com", PASSWORD, Role.MANAGER);
            SignUpRequestDto requestDto = createSignUpRequest(manager, Role.MANAGER, ADMIN_TOKEN);
            //when
            SignUpResponseDto savedUser = userService.signup(requestDto);
            User user = userRepository.findById(savedUser.getUserId()).orElse(null);

            //then
            assertEquals(manager.getUsername(), user.getUsername());
        }

        @Test
        @DisplayName("회원가입 실패 - 관리자 암호 불일치")
        void signUp_manager_fail() {
            //given
            User manager = User.createUser("manager1", "manager1@test.com", PASSWORD, Role.MANAGER);
            SignUpRequestDto requestDto = createSignUpRequest(manager, Role.MANAGER, "");

            //when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(requestDto);
            });
            assertEquals("관리자 암호가 틀려 등록이 불가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 username")
        void signUp_fail_duplicated_username() {
            //given
            User customer = User.createUser("customer1", "customer1@test.com", PASSWORD, Role.CUSTOMER);
            User manager = User.createUser("manager1", "manager1@test.com", PASSWORD, Role.MANAGER);
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
        void signUp_fail_duplicated_email() {
            //given
            User customer = User.createUser("customer1", "customer1@test.com", PASSWORD, Role.CUSTOMER);
            User manager = User.createUser("manager1", "manager1@test.com", PASSWORD, Role.MANAGER);

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
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            setUpSecurityContext(customerUserDetails);

            //when
            UserDetailsResponseDto user = userService.getUser(customer.getId(), customerUserDetails);

            //then
            assertEquals(user.getUsername(), customer.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 성공 - manager")
        void getUser_success_manager() {
            //given
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            setUpSecurityContext(managerUserDetails);

            //when
            UserDetailsResponseDto user = userService.getUser(manager.getId(), managerUserDetails);

            //then
            assertEquals(user.getUsername(), manager.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 성공 - master")
        void getUser_success_master() {
            //given
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when
            UserDetailsResponseDto user = userService.getUser(master.getId(), masterUserDetails);

            //then
            assertEquals(user.getUsername(), master.getUsername());
        }

        @Test
        @DisplayName("회원 단건 조회 실패 - 본인 제외 customer")
        void getUser_fail_customer() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            setUpSecurityContext(customerUserDetails);
            User customer2 = createAndSaveUser("customer2", "customer2@test.com", Role.CUSTOMER);

            //when
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.getUser(customer2.getId(), customerUserDetails);
            });

            //then
            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 단건 조회 실패 - 존재하지 않는 id")
        void getUser_fail_not_found_id() {
            //given
            Long userId = 100L;
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            setUpSecurityContext(managerUserDetails);

            //when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getUser(userId, managerUserDetails);
            });

            //then
            assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("회원 변경")
    class Update {
        @Test
        @DisplayName("회원 변경 성공 - 본인, 이메일 변경")
        void updateUser_success_customer_self_email() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .build();

            setUpSecurityContext(customerUserDetails);

            //when
            UserDetailsResponseDto updatedUser = userService.updateUser(customer.getId(), requestDto, customerUserDetails);

            //then
            assertEquals(UPDATED_EMAIL, updatedUser.getEmail());
        }

        @Test
        @DisplayName("회원 변경 성공 - MANAGER -> CUSTOMER 비밀번호 변경")
        void updateUser_success_mc_password() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext(managerUserDetails);

            //when
            UserDetailsResponseDto updatedUser = userService.updateUser(customer.getId(), requestDto, managerUserDetails);

            //then
            assertEquals(customer.getId(), updatedUser.getUserId());
        }

        @Test
        @DisplayName("회원 변경 성공 - MASTER -> MANAGER 모든 정보 변경")
        void updateUser_success_mm_all() {
            //given
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(manager.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext(masterUserDetails);

            //when
            UserDetailsResponseDto updatedUser = userService.updateUser(manager.getId(), requestDto, managerUserDetails);

            //then
            assertEquals(UPDATED_EMAIL, updatedUser.getEmail());
            assertEquals(manager.getId(), updatedUser.getUserId());
        }

        @Test
        @DisplayName("회원 변경 실패 - MANAGER -> MASTER (상위 권한자) 정보 변경 시도")
        void updateUser_fail_update_upper_lever_user() {
            //given
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(master.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            setUpSecurityContext(managerUserDetails);

            //when -then
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.updateUser(master.getId(), requestDto, managerUserDetails);
            });

            assertEquals("접근 권한이 없는 사용자입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 기존 비밀번호 매칭 실패")
        void updateUser_fail_password_does_not_match() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(UPDATED_PASSWORD)
                    .build();

            setUpSecurityContext(customerUserDetails);

            //when -then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.updateUser(customer.getId(), requestDto, customerUserDetails);
            });

            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 본인 제외 customer")
        void updateUser_fail_customer() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();
            setUpSecurityContext(customerUserDetails);
            User customer2 = createAndSaveUser("customer2", "customer2@test.com", Role.CUSTOMER);

            //when - then
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.updateUser(customer2.getId(), requestDto, customerUserDetails);
            });

            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 변경 실패 - 존재하지 않는 id")
        void updateUser_fail_not_found_id() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                    .username(customer.getUsername())
                    .email(UPDATED_EMAIL)
                    .newPassword(UPDATED_PASSWORD)
                    .originPassword(PASSWORD)
                    .build();

            Long userId = 100L;
            setUpSecurityContext(managerUserDetails);

            //when - then
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
        void deleteUser_success_customer_self() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            setUpSecurityContext(customerUserDetails);

            //when
            UserDeleteResponseDto deletedUser = userService.deleteUser(customer.getId(), customerUserDetails);

            //then
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
        void deleteUser_success_master() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when
            UserDeleteResponseDto deletedUser = userService.deleteUser(customer.getId(), masterUserDetails);

            //then
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
        void deleteUser_fail_manager() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            setUpSecurityContext(managerUserDetails);

            //when - then
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
                userService.deleteUser(customer.getId(), managerUserDetails);
            });

            assertEquals("Access Denied", exception.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 실패 - 존재하지 않는 id")
        void deleteUser_fail_id_does_not_found() {
            //given
            Long userId = 100L;
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteUser(userId, masterUserDetails);
            });

            assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("회원 삭제 실패 - 삭제된 id")
        void deleteUser_fail_id_deleted_in_advance() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when - then
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
            //given
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            UserDetailsImpl managerUserDetails = new UserDetailsImpl(manager);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            setUpSecurityContext(managerUserDetails);

            //when
            UserPageResponseDto userList = userService.searchUsers(managerUserDetails, 0, 5, "createdAt", "DESC", "ma");

            //then
            assertEquals(master.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(manager.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 성공 - MASTER")
        void searchUsers_success_master() {
            //given
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when
            UserPageResponseDto userList = userService.searchUsers(masterUserDetails, 0, 5, "createdAt", "DESC", "ma");

            //then
            assertEquals(master.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(manager.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 성공 - 정렬 ASC로")
        void searchUsers_success_orderby_createdAt_sort_asc() {
            //given
            User manager = createAndSaveUser("manager1", "manager1@test.com", Role.MANAGER);
            User master = createAndSaveUser("master1", "master1@test.com", Role.MASTER);
            UserDetailsImpl masterUserDetails = new UserDetailsImpl(master);
            setUpSecurityContext(masterUserDetails);

            //when
            UserPageResponseDto userList = userService.searchUsers(masterUserDetails, 0, 5, "createdAt", "ASC", "ma");

            //then
            assertEquals(manager.getUsername(), userList.getUsers().get(0).getUsername());
            assertEquals(master.getUsername(), userList.getUsers().get(1).getUsername());
        }

        @Test
        @DisplayName("회원 검색 실패 - CUSTOMER")
        void searchUsers_success_customer() {
            //given
            User customer = createAndSaveUser("customer1", "customer1@test.com", Role.CUSTOMER);
            UserDetailsImpl customerUserDetails = new UserDetailsImpl(customer);
            setUpSecurityContext(customerUserDetails);

            //when - then
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

    void setUpSecurityContext(UserDetailsImpl userDetails) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );
    }

}