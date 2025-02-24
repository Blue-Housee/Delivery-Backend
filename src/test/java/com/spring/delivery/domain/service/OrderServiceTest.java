package com.spring.delivery.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.delivery.domain.config.IntegrationTestBase;
import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.order.OrderMenuResponseDto;
import com.spring.delivery.domain.controller.dto.order.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.order.OrderResponseDto;
import com.spring.delivery.domain.domain.entity.*;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.MenuRepository;
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
class OrderServiceTest extends IntegrationTestBase {
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceTest.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private StoreRepository storeRepository;


    @Test
    @DisplayName("주문 생성 성공")
    void createOrder() {
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        User user = userRepository.save(testUser);

        // given
        // orderRequestDto 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(user);
        orderRequestDto.setAddress("testAddress");
        orderRequestDto.setOrderType("testOrderType");
        orderRequestDto.setTotalPrice(15000L);

        // 가게
        Store store = Store.of("testName", "testAddress", "010-1234-1234", true, LocalTime.now(), LocalTime.now(), user);
        storeRepository.save(store);

        // 메뉴
        MenuRequestDto menuRequestDto = MenuRequestDto.of("test", 15000L ,"testDescription", "d", true, store.getId());
        Menu menu = Menu.of(menuRequestDto, store);
        menuRepository.save(menu);

        // 메뉴 리스트
        List<Map<UUID, Long>> menuList= new ArrayList<>();
        Map<UUID, Long> menuIdMap = new HashMap<>();
        menuIdMap.put(menu.getId(), menu.getPrice());
        menuList.add(menuIdMap);

        orderRequestDto.setMenuInfo(menuList);

        //when - then
        ApiResponseDto<OrderResponseDto> order= orderService.createOrder(orderRequestDto);

        // 객체 생성 확인 => assertNotNull 을 사용해도 되지만 눈으로 확인해보고 싶었음.
        try{log.info("확인: {}", objectMapper.writeValueAsString(order));}
        catch(Exception e){log.error(e.getMessage());}
    }

    @Test
    @DisplayName("주문 수정 성공")
    void updateOrder() {
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        User user = userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

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
    @DisplayName("주문 삭제 성공")
    void deleteOrder() {
        // 테스트용 master 계정 생성
        userRepository.deleteAll();
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        User user = userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        Order order = Order.builder()
                .userId(user)
                .address("testAddress")
                .orderType("testOrderType")
                .totalPrice(15000L)
                .build();
        order = orderRepository.save(order);

        ApiResponseDto<OrderResponseDto> orderResponse = orderService.deleteOrder(order.getId(), userDetails);
        assertNotNull(orderResponse);
    }

    @Test
    @DisplayName("주문 조회 성공")
    void getOrder() {
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        User user = userRepository.save(testUser);

        Order order = Order.builder()
                .userId(user)
                .address("testAddress")
                .orderType("testOrderType")
                .totalPrice(15000L)
                .build();
        order = orderRepository.save(order);

        ApiResponseDto<OrderMenuResponseDto> responseDto = orderService.getOrder(order.getId());

        assertNotNull(responseDto);
        assertEquals(user.getId(), responseDto.getData().getOrder().getUser().getId());
        assertEquals("testAddress", responseDto.getData().getOrder().getAddress());
        assertEquals("testOrderType", responseDto.getData().getOrder().getOrderType());
        assertEquals(15000L, responseDto.getData().getOrder().getTotalPrice());
    }

    @Test
    @DisplayName("주문리스트 조회 성공")
    void getOrders() {
        User testUser = User.createUser("MasterUser", "test@test.com", "1234", Role.MASTER);
        User user = userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

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