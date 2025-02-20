package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.*;
import com.spring.delivery.domain.controller.dto.order.OrderMenuResponseDto;
import com.spring.delivery.domain.controller.dto.order.OrderRequestDto;
import com.spring.delivery.domain.controller.dto.order.OrderResponseDto;
import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.MenuOrder;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Payment;
import com.spring.delivery.domain.domain.repository.MenuOrderRepository;
import com.spring.delivery.domain.domain.repository.MenuRepository;
import com.spring.delivery.domain.domain.repository.OrderRepository;
import com.spring.delivery.domain.domain.repository.PaymentRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuOrderRepository menuOrderRepository;
    private final MenuRepository menuRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public ApiResponseDto<OrderResponseDto> createOrder(OrderRequestDto orderRequestDto) {
        // Order table 에 들어갈 객체 생성
        Order order = Order.createOrder(orderRequestDto);
        // DB에 저장
        orderRepository.save(order);

        // 메뉴의 정보가 여러개이기 때문에 리스트로 반환
        List<Map<UUID, Long>> menuInfo = orderRequestDto.getMenuInfo();

        // 리스트의 정보를 MenuOrder table에 저장
        for (Map<UUID, Long> menuItems : menuInfo) {
            for (Map.Entry<UUID, Long> menuItem : menuItems.entrySet()) {
                UUID menuId = menuItem.getKey();
                Long amount = menuItem.getValue();
                Menu menu = menuRepository.findById(menuId).orElse(null);
                // 주문, 메뉴, 메뉴 수량
                MenuOrder menuOrder = MenuOrder.create(order, menu , amount);
                menuOrderRepository.save(menuOrder);
            }
        }

        // paymentData 도 함께 받아와서 저장하자! -> order 객체, 총가격, 카드번호
        Payment payment = Payment.createPayment(order, orderRequestDto.getCardNumber());
        paymentRepository.save(payment);

        // 새로운 주문생성 성공 반환 데이터 return
        return ApiResponseDto.success(OrderResponseDto.from(order));
    }

    @Transactional
    public ApiResponseDto<OrderResponseDto> updateOrder(UUID id, OrderRequestDto orderRequestDto, UserDetailsImpl userDetails) {

        // MASTER, MANAGER만 사용가능 -> userDetails에서 role확인
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // user의 권한이 MASETR , MANAGER 라면 TRUE값 리턴
        boolean isMangerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER") || auth.getAuthority().equals("ROLE_MASTER"));

        // isMangerOrMaster 가 False 라면 return 권한없음
        if (!isMangerOrMaster) {
            return ApiResponseDto.fail(403, "주문을 수정할 권한이 없습니다.");
        }

        // 들어온 주문 id가 주문 DB에 있는지 확인
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ApiResponseDto.fail(404, "해당 주문은 존재하지 않습니다.");
        }
        // 주문이 존재한다면 Order 수정
        Order.update(order, orderRequestDto);

        // orderRequestDto 에 updateMenuIds 가 있다면 MenuOrder 수정
        if (!orderRequestDto.getUpdateMenuIds().isEmpty()) {
            orderRequestDto.getUpdateMenuIds().forEach(orderMenuId -> {
                UUID updateKey = orderMenuId.keySet().iterator().next();
                Long updateValue = orderMenuId.values().iterator().next();

                MenuOrder updateMenuOrder = menuOrderRepository.findById(updateKey).orElse(null);

                MenuOrder.update(updateMenuOrder, updateValue);
            });
        }

        // 수정후 성공 메세지 return
        return ApiResponseDto.success(null);
    }

    @Transactional
    public ApiResponseDto<OrderResponseDto> deleteOrder(UUID id, UserDetailsImpl userDetails) {
        // MASTER, MANAGER만 사용가능 -> userDetails에서 role확인
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // user의 권한이 MASETR , MANAGER 라면 TRUE값 리턴
        boolean isMangerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER") || auth.getAuthority().equals("ROLE_MASTER"));

        // isMangerOrMaster 가 False 라면 return 권한없음
        if (!isMangerOrMaster) {
            return ApiResponseDto.fail(403, "주문을 수정할 권한이 없습니다.");
        }

        // 들어온 주문 id가 주문 DB에 있는지 확인
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ApiResponseDto.fail(404, "해당 주문은 존재하지 않습니다.");
        }

        // 주문이 존재한다면 주문삭제
        order.delete(userDetails.getUser().getUsername());

        return ApiResponseDto.success(null);
    }

    public ApiResponseDto<OrderMenuResponseDto> getOrder(UUID id) {
        // 들어온 주문 id가 주문 DB에 있는지 확인
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ApiResponseDto.fail(404, "해당 주문은 존재하지 않습니다.");
        }

        // 들어온 주문의 메뉴들들 db 에서 가져옴
        // 주문정보와, 메뉴 아이디들을 가져온다
        List<UUID> menuOrder = menuOrderRepository.findByOrderId(id).stream()
                .map(MenuOrder::getId)
                .toList();

        List<Menu> menus = new ArrayList<>();
        // 메뉴아이디들로 메뉴정보 조회를 한다.
        menuOrder.forEach(orderMenuId -> {
            Menu menu = menuRepository.findById(orderMenuId).orElse(null);
            menus.add(menu);
        });

        // 주문정보 + 메뉴정보들을 합쳐서 보내준다.
        return ApiResponseDto.success(OrderMenuResponseDto.from(order, menus));
    }

    public ApiResponseDto<List<OrderMenuResponseDto>> getOrders(Long userId,
                                                                String orderStatus,
                                                                String sort,
                                                                String order,
                                                                int page,
                                                                int size ,
                                                                UserDetailsImpl userDetails) {
        // MASTER, MANAGER만 사용가능 -> userDetails에서 role확인
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // user의 권한이 MASETR , MANAGER 라면 TRUE값 리턴
        boolean isMangerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER") || auth.getAuthority().equals("ROLE_MASTER"));

        // isMangerOrMaster 가 False 라면 return 권한없음
        if (!isMangerOrMaster) {
            return ApiResponseDto.fail(403, "주문목록을 조회할 권한이 없습니다.");
        }


        // 페이징
        Pageable pageable = PageRequest.of(page-1, size, "desc".equalsIgnoreCase(order)
                ? Sort.by(sort).descending() : Sort.by(sort).ascending());

        Page<Order> orderPage = orderRepository.findByUserIdAndOrderStatus(userId, orderStatus, pageable);

        // 주문 목록을 DTO로 변환
        List<OrderMenuResponseDto> orderMenuResponseDtos = orderPage.getContent().stream()
                .map(orders -> {
                    // 주문 아이디로 메뉴 리스트 조회
                    List<MenuOrder> menuOrders = menuOrderRepository.findByOrderId(orders.getId());

                    // 메뉴 아이디들로 메뉴 정보 조회
                    List<Menu> menus = menuOrders.stream()
                            .map(menuOrder -> menuRepository.findById(menuOrder.getId()).orElse(null))
                            .collect(Collectors.toList());

                    // 주문 정보와 메뉴 리스트를 DTO로 변환하여 반환
                    return new OrderMenuResponseDto(orders, menus);
                }).toList();

        return ApiResponseDto.success(orderMenuResponseDtos);
    }
}
