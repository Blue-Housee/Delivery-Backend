package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.user.SignUpRequestDto;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    private static String PASSWORD = "test1234";

    private User customer;
    private User manager;
    private User master;

    private UserDetailsImpl customerUserDetails;
    private UserDetailsImpl managerUserDetails;
    private UserDetailsImpl masterUserDetails;

    @BeforeAll
    void setUp() {
        // 공동 테스트 데이터 초기화
        User testCustomer = User.createUser(
                "customer1",
                "customer1@test.com",
                PASSWORD,
                Role.CUSTOMER
        );

        User testManager = User.createUser(
                "manager1",
                "manager1@test.com",
                PASSWORD,
                Role.MANAGER
        );

        User testMaster = User.createUser(
                "master1",
                "master1@test.com",
                PASSWORD,
                Role.MASTER
        );

        customer = userRepository.save(testCustomer);
        customerUserDetails = new UserDetailsImpl(customer);
        manager = userRepository.save(testManager);
        customerUserDetails = new UserDetailsImpl(manager);
        master = userRepository.save(testMaster);
        masterUserDetails = new UserDetailsImpl(master);

    }

    //-- 회원가입 --
    @Nested
    @DisplayName("회원가입")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SignUp {
        @BeforeAll
        void init() {
            userRepository.deleteAll();  // 기존 데이터 초기화
        }

        @Test
        @Order(1)
        @DisplayName("일반 회원가입 성공")
        @Transactional
        void signUp_customer_success() {
            //given
            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .username(customer.getUsername())
                    .email(customer.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();

            //when
            User savedUser = userService.signup(requestDto);

            //then
            assertEquals(customer.getUsername(), savedUser.getUsername());
        }

        @Test
        @Order(2)
        @DisplayName("관리자 회원가입 성공")
        @Transactional
        void signUp_manager_success() {
            //given
            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .username(manager.getUsername())
                    .email(manager.getEmail())
                    .password(manager.getPassword())
                    .role(Role.MANAGER)
                    .adminToken(ADMIN_TOKEN)
                    .build();
            //when
            User savedUser = userService.signup(requestDto);

            //then
            assertEquals(manager.getUsername(), savedUser.getUsername());
            assertEquals(savedUser.getRole(), Role.MANAGER);
        }

        @Test
        @Order(3)
        @DisplayName("회원가입 실패 - 관리자 암호 불일치")
        @Transactional
        void signUp_manager_fail() {
            //given
            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .username(manager.getUsername())
                    .email(manager.getEmail())
                    .password(manager.getPassword())
                    .role(Role.MANAGER)
                    .adminToken("")
                    .build();
            //when - then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(requestDto);
            });
            assertEquals("관리자 암호가 틀려 등록이 불가능합니다.", exception.getMessage());
        }

        @Test
        @Order(4)
        @DisplayName("회원가입 실패 - 중복된 username")
        @Transactional
        void signUp_fail_duplicated_username() {
            //given
            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .username(customer.getUsername())
                    .email(customer.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();
            SignUpRequestDto secondRequestDto = SignUpRequestDto.builder()
                    .username(customer.getUsername())
                    .email(manager.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();

            //when
            userService.signup(requestDto); //첫번째

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.signup(secondRequestDto); //두번째
            });

            //then
            assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
        }

        @Test
        @Order(5)
        @DisplayName("회원가입 실패 - 중복된 email")
        @Transactional
        void signUp_fail_duplicated_email() {
            //given
            SignUpRequestDto requestDto = SignUpRequestDto.builder()
                    .username(customer.getUsername())
                    .email(customer.getEmail())
                    .password(customer.getPassword())
                    .role(Role.CUSTOMER)
                    .build();

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
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Transactional
    class Read {
        //성공
        @Test
        void getUser_success() {

        }

    }

    @Nested
    @DisplayName("회원 변경")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Update {
        //성공
        @Test
        void updateUser_success() {

        }

    }

    @Nested
    @DisplayName("회원 삭제")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Delete {
        //성공
        @Test
        void deleteUser_success() {

        }

    }

    @Nested
    @DisplayName("회원 검색")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Search {
        //성공
        @Test
        void searchUsers_success() {

        }

    }

}