package com.spring.delivery.domain.controller;


import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.menu.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.menu.MenuResponseDto;
import com.spring.delivery.domain.service.MenuService;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<MenuResponseDto>> createMenu(
            @RequestBody MenuRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        log.info("메뉴생성 메서드 컨트롤러 실행 됨");
        ApiResponseDto<MenuResponseDto> responseDto = menuService.createMenu(requestDto, userDetails);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }


    @PatchMapping("/{menuId}")
    public ResponseEntity<ApiResponseDto<Void>> updateMenu(
            @PathVariable UUID menuId,
            @RequestBody MenuRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ApiResponseDto<Void> responseDto = menuService.updateMenu(menuId, requestDto, userDetails);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }


    // 메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ApiResponseDto<Void> responseDto = menuService.deleteMenu(menuId, userDetails);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    // 메뉴 단건(상세) 조회
    @GetMapping("/{menuId}")
    public ResponseEntity<ApiResponseDto<MenuResponseDto>> getMenuDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID menuId
    ) {
        ApiResponseDto<MenuResponseDto> responseDto = menuService.getMenuDetail(userDetails, menuId);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    // 모든 메뉴 리스트
    @GetMapping
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getMenus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) UUID store_id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order
    ) {

        ApiResponseDto<Map<String, Object>> responseDto = menuService.getMenusByStore(userDetails, store_id, page, size, sort, order);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    /* 검색 */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> searchMenus(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order) {

        ApiResponseDto<Map<String, Object>> responseDto = menuService.searchMenus(userDetails, storeId, keyword, page, size, sort, order);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }


}
