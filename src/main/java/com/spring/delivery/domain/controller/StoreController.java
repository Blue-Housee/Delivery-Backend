package com.spring.delivery.domain.controller;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreCreateRequestDto;
import com.spring.delivery.domain.controller.dto.store.StoreDetailResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreListResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreUpdateRequestDto;
import com.spring.delivery.domain.service.StoreService;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> createStore(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody StoreCreateRequestDto requestDto) {
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

    @GetMapping("/{id}") // 단건 조회를 위한 메서드
    public ResponseEntity<ApiResponseDto<StoreDetailResponseDto>> getStoreById(@PathVariable UUID id) {
        ApiResponseDto<StoreDetailResponseDto> responseDto = storeService.getStoreById(id);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateStore(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id, @RequestBody StoreUpdateRequestDto requestDto) {
        ApiResponseDto responseDto = storeService.updateStore(userDetails, id, requestDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @DeleteMapping("/{id}") // 소프트 삭제 메서드
    public ResponseEntity<ApiResponseDto> deleteStore(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        ApiResponseDto responseDto = storeService.deleteStore(userDetails, id);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<Page<StoreListResponseDto>>> searchStores(
            @RequestParam String query,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sortBy") String sortBy,
            @RequestParam(value = "isAsc") boolean isAsc) {
        ApiResponseDto<Page<StoreListResponseDto>> responseDto = storeService.searchStores(query, page, size, sortBy, isAsc);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }


}
