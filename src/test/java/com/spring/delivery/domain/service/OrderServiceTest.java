package com.spring.delivery.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.order.OrderMenuResponseDto;
import com.spring.delivery.domain.controller.dto.order.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.order.OrderResponseDto;
import com.spring.delivery.domain.domain.entity.*;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.OrderRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceTest.class);
    private User user;
    private UserDetailsImpl userDetails;

    private Store store;
    private Menu menu;

    private Order order;


    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    void setUp() {
        // 테스트용 master 계정 생성
        userRepository.deleteAll();
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        user = userRepository.save(testUser);
        userDetails = new UserDetailsImpl(user);

        // 주문 테스트용 더미 가게
        store = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        store = storeRepository.save(store);
        UUID storeId = store.getId();

        // 주문 테스트용 더미 메뉴
        MenuRequestDto menuRequestDto = new MenuRequestDto();
        menuRequestDto.setStoreId(storeId);
        menuRequestDto.setDescription("testDescription");
        menuRequestDto.setName("testName");
        menuRequestDto.setPrice(15000L);
        menuRequestDto.setPublicStatus(true);
        menu = Menu.of(menuRequestDto, store);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("주문 생성 성공")
    @Transactional
     void createOrder() {
        // given
        // orderRequestDto 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(user);
        orderRequestDto.setAddress("testAddress");
        orderRequestDto.setOrderType("testOrderType");
        orderRequestDto.setTotalPrice(15000L);

        // 메뉴 리스트
        List<Map<UUID, Long>> menuList= new ArrayList<>();
        Map<UUID, Long> menu1 = new HashMap<>();
        menu1.put(UUID.randomUUID(), 100L);
        menu1.put(UUID.randomUUID(), 200L);
        menuList.add(menu1);

        orderRequestDto.setMenuInfo(menuList);

        //when - then
        ApiResponseDto order= orderService.createOrder(orderRequestDto);

        // 객체 생성 확인 => assertNotNull 을 사용해도 되지만 눈으로 확인해보고 싶었음.
        try{log.info("확인: {}", objectMapper.writeValueAsString(order));}
        catch(Exception e){log.error(e.getMessage());}
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("주문 수정 성공")
    @Transactional
    void updateOrder() {
        Order order = Order.builder()
                .userId(user)
                .address("testAddress")
                .orderType("testOrderType")
                .totalPrice(15000L)
                .build();
        order = orderRepository.save(order);

        // given
        // orderRequestDto 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(user);
        orderRequestDto.setAddress("updateAddress");
        orderRequestDto.setOrderType("updateOrderType");
        orderRequestDto.setTotalPrice(16000L);
        orderRequestDto.setUpdateMenuIds(null);

        ApiResponseDto<OrderResponseDto> orderResponse = orderService.updateOrder(order.getId(), orderRequestDto, userDetails);

        assertNotNull(orderResponse);
        assertEquals("updateAddress", orderResponse.getData().getAddress());
        assertEquals("updateOrderType", orderResponse.getData().getOrderType());
        assertEquals(16000L, orderResponse.getData().getTotalPrice());

    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("주문 삭제 성공")
    @Transactional
    void deleteOrder() {
        Order order = Order.builder()
                .userId(user)
                .address("testAddress")
                .orderType("testOrderType")
                .totalPrice(15000L)
                .build();
        order = orderRepository.save(order);

        ApiResponseDto orderResponse = orderService.deleteOrder(order.getId(), userDetails);
        assertNotNull(orderResponse);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("주문 조회 성공")
    @Transactional
    void getOrder() {
        Order order = Order.builder()
                .userId(user)
                .address("testAddress")
                .orderType("testOrderType")
                .totalPrice(15000L)
                .build();
        order = orderRepository.save(order);

        ApiResponseDto<OrderMenuResponseDto> responseDto = orderService.getOrder(order.getId());

        assertNotNull(responseDto);
        assertEquals(user, responseDto.getData().getOrder().getUser());
        assertEquals("testAddress", responseDto.getData().getOrder().getAddress());
        assertEquals("testOrderType", responseDto.getData().getOrder().getOrderType());
        assertEquals(15000L, responseDto.getData().getOrder().getTotalPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("주문리스트 조회 성공")
    @Transactional
    void getOrders() {
        for (int i=0; i<5; i++) {
            Order order = Order.builder()
                    .userId(user)
                    .address("testAddress" + i)
                    .orderType("testOrderType" + i)
                    .totalPrice(15000L)
                    .build();
            orderRepository.save(order);
        }

        ApiResponseDto<List<OrderMenuResponseDto>> orders = orderService.getOrders(user.getId(), "testOrderType", "createdAt", "desc", 1, 3, userDetails);

        assertNotNull(orders);

    }

}