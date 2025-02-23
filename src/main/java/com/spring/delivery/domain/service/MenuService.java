package com.spring.delivery.domain.service;


import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.menu.MenuResponseDto;
import com.spring.delivery.domain.domain.entity.Menu;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.MenuRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import com.spring.delivery.infra.gemini.Gemini;
import com.spring.delivery.infra.gemini.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

        // 권한 확인
        Set<String> allowedRoles = Set.of("ROLE_OWNER", "ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
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

        // 권한 확인
        Set<String> allowedRoles = Set.of("ROLE_OWNER", "ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
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

        // 권한 확인
        Set<String> allowedRoles = Set.of("ROLE_OWNER", "ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
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
    public ApiResponseDto<MenuResponseDto> getMenuDetail(UserDetailsImpl userDetails, UUID menuId) {

        try {
            // 권한 확인
            Set<String> allowedRoles = Set.of("ROLE_CUSTOMER","ROLE_OWNER","ROLE_MANAGER","ROLE_MASTER");

            if (!lacksAuthority(userDetails, allowedRoles)) {
                return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
            }

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
    public ApiResponseDto<Map<String, Object>> getMenusByStore(UserDetailsImpl userDetails,UUID storeId, int page, int size, String sort, String order) {
        try {
            // 권한 확인
            Set<String> allowedRoles = Set.of("ROLE_CUSTOMER","ROLE_OWNER","ROLE_MANAGER","ROLE_MASTER");

            if (!lacksAuthority(userDetails, allowedRoles)) {
                return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
            }

            // 정렬 방향 설정 (desc or asc)
            Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));

            Page<Menu> menuPage;
            if (storeId == null) {
                menuPage = menuRepository.findActiveMenusByStoreId(storeId, pageable);
            } else {
                menuPage = menuRepository.findActiveMenus(pageable);
            }

            Map<String, Object> response = createPagedResponse(menuPage);

            return ApiResponseDto.success(response);
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 예외 발생: {}", e.getMessage(), e);
            return ApiResponseDto.fail(500, "서버 내부 오류가 발생했습니다.");
        }
    }

    /* 검색 */
    @Transactional
    public ApiResponseDto<Map<String, Object>> searchMenus(
            UserDetailsImpl userDetails, UUID storeId, String keyword,
            int page, int size, String sort, String order) {

        // 권한 확인
        Set<String> allowedRoles = Set.of("ROLE_CUSTOMER","ROLE_OWNER","ROLE_MANAGER","ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
        }

        // 정렬 방향 설정 (desc or asc)
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));

        Page<Menu> MenuPage;
        if (storeId == null && (keyword == null || keyword.isBlank())) {
            MenuPage = menuRepository.findAll(pageable); // 가게 x, 키워드 o
        } else if (storeId == null) {
            MenuPage = menuRepository.findByNameContaining(keyword, pageable); // 키워드 o,  가게 x
        } else if (keyword == null || keyword.isBlank()) {
            MenuPage = menuRepository.findByStoreId(storeId, pageable); // 키워드 x, 가게 o
        } else {
            MenuPage = menuRepository.findByStoreIdAndNameContaining(storeId, keyword, pageable); // 가게 o, 키워드 x
        }

        Map<String, Object> response = createPagedResponse(MenuPage);

        return ApiResponseDto.success(response);
    }

    private boolean lacksAuthority(UserDetails userDetails, Set<String> requiredRoles) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        log.info("현재 사용자 권한: {}", authorities);

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredRoles::contains);
    }

    // 페이징된 데이터를 반환
    private Map<String, Object> createPagedResponse(Page<Menu> menuPage) {
        List<MenuResponseDto> menuList = menuPage.getContent().stream()
                .map(MenuResponseDto::from)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalMenus", menuPage.getTotalElements());
        response.put("currentPage", menuPage.getNumber() + 1);
        response.put("totalPages", menuPage.getTotalPages());
        response.put("pageSize", menuPage.getSize());
        response.put("menus", menuList);

        return response;
    }
}

