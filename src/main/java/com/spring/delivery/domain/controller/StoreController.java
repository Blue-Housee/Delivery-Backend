package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreListResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreRequestDto;
import com.spring.delivery.domain.service.StoreService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> createStore(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody StoreRequestDto requestDto) {
        ApiResponseDto responseDto = storeService.createStore(userDetails, requestDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<StoreListResponseDto>>> getAllStores(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sortBy") String sortBy,
            @RequestParam(value = "isAsc") boolean isAsc) {
        ApiResponseDto<Page<StoreListResponseDto>> responseDto = storeService.getAllStores(page - 1, size, sortBy, isAsc);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

}
