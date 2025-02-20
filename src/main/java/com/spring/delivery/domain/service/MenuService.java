package com.spring.delivery.domain.service;


import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.order.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.order.MenuResponseDto;
import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.MenuRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;


    @Transactional
    public ApiResponseDto<MenuResponseDto> createMenu(MenuRequestDto requestDto, UserDetailsImpl userDetails) {

        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "메뉴를 생성할 권한이 없습니다.");
        }

        // 가게 정보 가져오기
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게 입니다."));

        // 메뉴 생성
        Menu menu = Menu.of(requestDto, store);
        menuRepository.save(menu);

        return ApiResponseDto.success(MenuResponseDto.from(menu));
    }


    @Transactional
    public ApiResponseDto<Void> updateMenu(UUID menuId, MenuRequestDto requestDto, UserDetailsImpl userDetails) {

        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "메뉴를 수정할 권한이 없습니다.");
        }

        Menu menu = menuRepository.findById(menuId)
                .orElse(null);

        if (menu == null) {
            return ApiResponseDto.fail(404, "메뉴를 찾을 수 없습니다.");
        }

        Menu.update(menu, requestDto);

        return ApiResponseDto.success(null);

    }

    @Transactional
    public ApiResponseDto<Void> deleteMenu(UUID menuId, UserDetailsImpl userDetails) {

        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "메뉴를 삭제할 권한이 없습니다.");
        }

        Menu menu = menuRepository.findById(menuId)
                .orElse(null);

        if (menu == null) {
            return ApiResponseDto.fail(404, "메뉴를 찾을 수 없습니다.");
        }

        menu.delete(userDetails.getUsername()); // soft delete

        return ApiResponseDto.success(null);
    }

    // 메뉴 단건 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<MenuResponseDto> getMenuDetail(UUID menuId) {

        try {
            Menu menu = menuRepository.findActiveMenuById(menuId)
                    .orElse(null);

            if (menu == null) {
                log.error("메뉴를 찾을 수 없음: {}", menuId);
                return ApiResponseDto.fail(404, "메뉴가 존재하지 않거나 삭제되었습니다.");
            }

            return ApiResponseDto.success(MenuResponseDto.from(menu));
        } catch (Exception e) {
            log.error("메뉴 조회 중 예외 발생: {}", e.getMessage(), e);
            return ApiResponseDto.fail(500, "서버 내부 오류가 발생했습니다.");
        }
    }

    // 메뉴 전체 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<Map<String, Object>> getMenusByStore(UUID storeId, int page, int size, String sort, String order) {
        try {
            // 정렬 방향 설정 (desc or asc)
            Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));

            Page<Menu> menuPage = menuRepository.findActiveMenusByStoreId(storeId, pageable);

            List<MenuResponseDto> menuList = menuPage.getContent().stream()
                    .map(MenuResponseDto::from)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("totalMenus", menuPage.getTotalElements());
            response.put("currentPage", menuPage.getNumber() + 1);
            response.put("totalPages", menuPage.getTotalPages());
            response.put("pageSize", menuPage.getSize());
            response.put("menus", menuList);

            return ApiResponseDto.success(response);
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 예외 발생: {}", e.getMessage(), e);
            return ApiResponseDto.fail(500, "서버 내부 오류가 발생했습니다.");
        }
    }
}

