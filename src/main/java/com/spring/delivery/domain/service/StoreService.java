package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.store.StoreRequestDto;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.StoreCategory;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.Category;
import com.spring.delivery.domain.domain.repository.CategoryRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.domain.domain.repository.StoreCategoryRepository; // 추가
import com.spring.delivery.global.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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

    public ApiResponseDto createStore(UserDetailsImpl userDetails, StoreRequestDto requestDto) {
        // User 객체를 가져오는 로직
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // 권한 확인 (OWNER, MASTER만 가능)
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        boolean isOwnerOrMaster = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER") || auth.getAuthority().equals("ROLE_MASTER"));

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
}
