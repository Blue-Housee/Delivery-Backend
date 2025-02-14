package com.spring.delivery.domain.controller;


import com.spring.delivery.domain.controller.dto.MenuRequestDto;
import com.spring.delivery.domain.controller.dto.MenuResponseDto;
import com.spring.delivery.domain.repository.MenuRepository;
import com.spring.delivery.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/test")
    public ResponseEntity<String> checkApi() {
        return ResponseEntity.ok("메뉴 api가 정상작동 중입니다.");
    }

    @PostMapping
    public ResponseEntity<MenuResponseDto> createMenu(@RequestBody MenuRequestDto requestDto) {
        MenuResponseDto responseDto = menuService.createMenu(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @PatchMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable Long menuId, @RequestBody MenuRequestDto requestDto) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, requestDto));
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }

}
