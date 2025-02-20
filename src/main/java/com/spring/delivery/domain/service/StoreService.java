package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.*;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.StoreCategory;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import com.spring.delivery.domain.domain.repository.StoreCategoryRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final CategoryRepository categoryRepository;

    public StoreService(StoreRepository storeRepository, UserRepository userRepository,
                        StoreCategoryRepository storeCategoryRepository, CategoryRepository categoryRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.storeCategoryRepository = storeCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public ApiResponseDto createStore(UserDetailsImpl userDetails, StoreCreateRequestDto requestDto) {
        // User 객체를 가져오는 로직
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // 권한 확인 (MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "가게를 등록할 권한이 없습니다.");
        }

        // Store 객체 생성
        Store store = Store.of(
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getTel(),
                requestDto.isOpenStatus(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                user // User 객체
        );

        // 저장 로직
        storeRepository.save(store); // Store 객체를 저장

        // 카테고리 검증 및 StoreCategory 객체 생성
        List<StoreCategory> storeCategories = requestDto.getCategoryIds().stream()
                .map(categoryId -> {
                    // 카테고리 검증: ID로 Category 찾기
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID: " + categoryId));
                    return StoreCategory.of(store, category); // StoreCategory 객체 생성
                })
                .collect(Collectors.toList());

        // 각 StoreCategory 객체 저장
        storeCategoryRepository.saveAll(storeCategories); // StoreCategoryRepository를 통해 저장

        // 성공적인 응답 반환
        return ApiResponseDto.success(store.getId());
    }

    @Transactional(readOnly = true)
    public ApiResponseDto<Page<StoreListResponseDto>> getAllStores(int page, int size, String sortBy, boolean isAsc) {
        // 정렬 방향 설정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 활성 상태의 스토어 목록 조회
        Page<Store> storePage = storeRepository.findByDeletedAtIsNull(pageable); // 변경된 부분

        // Store 객체를 StoreListResponseDto로 변환
        Page<StoreListResponseDto> responseDtoPage = storePage.map(store -> {
            List<String> categories = store.getStoreCategories().stream()
                    .map(storeCategory -> storeCategory.getCategory().getName())
                    .collect(Collectors.toList());

            return new StoreListResponseDto(
                    store.getId(),
                    store.getName(),
                    store.getAddress(),
                    store.getTel(),
                    store.isOpenStatus(),
                    categories,
                    store.getStartTime(),
                    store.getEndTime()
            );
        });

        // ApiResponseDto로 응답 반환
        return ApiResponseDto.success(responseDtoPage);
    }

    @Transactional(readOnly = true)
    public ApiResponseDto<StoreDetailResponseDto> getStoreById(UUID id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가게 ID입니다."));

        List<String> categories = store.getStoreCategories().stream()
                .map(storeCategory -> storeCategory.getCategory().getName())
                .collect(Collectors.toList());

        StoreDetailResponseDto responseDto = new StoreDetailResponseDto(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getTel(),
                store.isOpenStatus(),
                store.getStartTime(), // 시작 시간
                store.getEndTime(),   // 종료 시간
                categories
        );

        return ApiResponseDto.success(responseDto);
    }

    @Transactional
    public ApiResponseDto updateStore(UserDetailsImpl userDetails, UUID storeId, StoreUpdateRequestDto requestDto) {
        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "가게를 수정할 권한이 없습니다.");
        }

        // 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가게 ID입니다."));

        // 가게 정보 수정
        store.update(requestDto);

        // 요청에서 categoryIds가 있을 때만 변경
        if (requestDto.getCategoryIds() != null) {
            List<UUID> existingCategoryIds = store.getStoreCategories().stream()
                    .map(storeCategory -> storeCategory.getCategory().getId())
                    .toList();

            List<UUID> newCategoryIds = requestDto.getCategoryIds();

            // 기존 카테고리 삭제 (요청에 없는 카테고리)
            existingCategoryIds.stream()
                    .filter(categoryId -> !newCategoryIds.contains(categoryId))
                    .forEach(categoryId -> {
                        StoreCategory storeCategory = storeCategoryRepository.findByStoreIdAndCategoryId(storeId, categoryId)
                                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID: " + categoryId));
                        storeCategoryRepository.delete(storeCategory);
                    });

            // 새로운 카테고리 추가
            newCategoryIds.stream()
                    .filter(categoryId -> !existingCategoryIds.contains(categoryId))
                    .forEach(categoryId -> {
                        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID: " + categoryId));
                        storeCategoryRepository.save(StoreCategory.of(store, category));
                    });
        }

        // 응답 DTO 생성
        StoreUpdateResponseDto responseDto = new StoreUpdateResponseDto(
                store.getId(),
                store.getName(),
                store.getStoreCategories().stream()
                        .map(sc -> sc.getCategory().getId())
                        .toList(), // 요청이 없으면 기존 카테고리 유지
                store.getAddress(),
                store.getTel(),
                store.getStartTime(),
                store.getEndTime()
        );

        return ApiResponseDto.success(responseDto);
    }

    @Transactional
    public ApiResponseDto deleteStore(UserDetailsImpl userDetails, UUID storeId) {
        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

        if (!isOwnerOrMaster) {
            return ApiResponseDto.fail(403, "가게를 삭제할 권한이 없습니다.");
        }

        // 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가게 ID입니다."));

        // 이미 삭제된 가게인지 확인
        if (store.getDeletedAt() != null) {
            return ApiResponseDto.fail(400, "이미 삭제된 가게입니다.");
        }

        // StoreCategory 삭제 (소프트 삭제)
        List<StoreCategory> storeCategories = storeCategoryRepository.findByStoreId(storeId);
        storeCategories.forEach(storeCategory -> {
            storeCategory.delete(userDetails.getUsername());
        });

        // 스토어 소프트 삭제
        store.delete(userDetails.getUsername());

        // 성공적인 응답 반환
        return ApiResponseDto.success("가게가 성공적으로 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public ApiResponseDto<Page<StoreListResponseDto>> searchStores(String query, int page, int size, String sortBy, boolean isAsc) {
        // 페이지당 노출 건수 제한
        if (size != 10 && size != 30 && size != 50) {
            size = 10; // 기본값으로 10으로 설정
        }

        // 정렬 방향 설정
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;

        // 기본 정렬 기준 설정
        if (!sortBy.equals("createdAt") && !sortBy.equals("updatedAt")) {
            sortBy = "createdAt"; // 기본값으로 생성일로 설정
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 검색 수행
        Page<Store> storePage = storeRepository.searchStores(query, pageable);

        // Store 객체를 StoreListResponseDto로 변환
        Page<StoreListResponseDto> responseDtoPage = storePage.map(store -> {
            // 카테고리 처리
            List<String> categories = store.getStoreCategories().stream()
                    .map(storeCategory -> storeCategory.getCategory().getName())
                    .collect(Collectors.toList());

            return new StoreListResponseDto(
                    store.getId(),
                    store.getName(),
                    store.getAddress(),
                    store.getTel(),
                    store.isOpenStatus(),
                    categories,
                    store.getStartTime(),
                    store.getEndTime()
            );
        });

        // ApiResponseDto로 응답 반환
        return ApiResponseDto.success(responseDtoPage);
    }

}
