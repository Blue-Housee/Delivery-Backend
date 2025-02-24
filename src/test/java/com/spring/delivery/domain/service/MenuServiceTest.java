package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.menu.MenuResponseDto;
import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.MenuRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

import static com.spring.delivery.domain.domain.entity.User.createUser;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class MenuServiceTest {

    @Autowired
    MenuService menuService;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    UserRepository userRepository;

    User ownerUser;
    User customerUser;
    UserDetailsImpl ownerUserDetails;
    UserDetailsImpl customerUserDetails;

    Store store;
    MenuResponseDto createdMenu = null;
    Menu testMenu;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 생성
        ownerUser = userRepository.findByEmail("owner2@email.com")
                .orElseGet(() -> {
                    User ownerUser2 = createUser("owner2","owner2@eamil.com","1234", Role.OWNER);
                    return userRepository.save(ownerUser2);
                });

        customerUser = userRepository.findByEmail("customer2@email.com")
                .orElseGet(() -> {
                    User customerUser2 = createUser("customer2","customer2@eamil.com","1234", Role.CUSTOMER);
                    return userRepository.save(customerUser2);
                });

        ownerUserDetails = new UserDetailsImpl(ownerUser);
        customerUserDetails = new UserDetailsImpl(customerUser);

        // 테스트를 위한 스토어 생성
        store = Store.of(
                "테스트 가게",
                "테스트 주소",
                "010-1234-5678",
                true,
                LocalTime.now(),
                LocalTime.now(),
                ownerUser
                );
        storeRepository.save(store);


        testMenu = menuRepository.save(Menu.of(
                MenuRequestDto.of(
                        "테스트메뉴",
                        10000L,
                        "테스트메뉴입니다.",
                        "image.jpg",
                        true,
                        store.getId()),
                store
        ));
    }

    @Test
    @Order(1)
    @Transactional
    @DisplayName("신규 메뉴 등록")
    void testSuccessCreateMenu() {
        // given
        String name = "테스트 메뉴";
        Long price = 15000L;
        String description = "이것은 테스트 메뉴입니다.";
        String menuImage = "http://images.com/menu.jpg";
        Boolean publicStatus = true;

        MenuRequestDto requestDto = MenuRequestDto.of(
                name,
                price,
                description,
                menuImage,
                publicStatus,
                store.getId()
        );

        // when
        MenuResponseDto menu = menuService.createMenu(requestDto, ownerUserDetails).getData();

        // then
        assertNotNull(menu.getId());
        assertEquals(name, menu.getName());
        assertEquals(price, menu.getPrice());
        assertEquals(description, menu.getDescription());
        assertEquals(menuImage, menu.getMenuImage());
        assertEquals(publicStatus, menu.getPublicStatus());
        assertEquals(store.getId(), menu.getStoreId());

        createdMenu = menu;
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("신규 메뉴 등록 실패-권한없음")
    void testFailCreateMenu() {
        // given
        MenuRequestDto requestDto = MenuRequestDto.of(
                "테스트 메뉴",
                15000L,
                "이것은 테스트 메뉴입니다.",
                "http://images.com/menu.jpg",
                true,
                store.getId()
        );

        // when
        ApiResponseDto<MenuResponseDto> response = menuService.createMenu(requestDto, customerUserDetails);

        // then
        assertEquals(403, response.getStatus());
        assertEquals("메뉴를 생성할 권한이 없습니다.", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("메뉴 수정 성공")
    void testSuccessUpdateMenu() {
        // given
        UUID menuId = testMenu.getId();
        String updatedName = "수정된 메뉴";
        Long updatedPrice = 20000L;
        String updatedDescription = "이것은 수정된 메뉴입니다.";
        String updatedMenuImage = "http://images.com/updated-menu.jpg";
        Boolean updatedPublicStatus = false;

        MenuRequestDto requestDto = MenuRequestDto.of(
                updatedName,
                updatedPrice,
                updatedDescription,
                updatedMenuImage,
                updatedPublicStatus,
                store.getId()
        );

        // when
        menuService.updateMenu(menuId, requestDto, ownerUserDetails);

        // then
        Menu updatedMenu = menuRepository.findById(menuId)
                        .orElseThrow(() -> new IllegalStateException("수정된 메뉴를 찾을 수 없습니다."));

        assertEquals(updatedName, updatedMenu.getName());
        assertEquals(updatedPrice, updatedMenu.getPrice());
        assertEquals(updatedDescription, updatedMenu.getDescription());
        assertEquals(updatedMenuImage, updatedMenu.getMenuImage());
        assertEquals(updatedPublicStatus, updatedMenu.isPublicStatus());
    }


    @Test
    @Order(4)
    @DisplayName("메뉴 상세 조회 성공")
    void testGetMenuDetail() {
        // given
        UUID id = testMenu.getId();

        // when
        ApiResponseDto<MenuResponseDto> response = menuService.getMenuDetail(ownerUserDetails, id);

        // then
        assertEquals(200, response.getStatus());
    }



}

