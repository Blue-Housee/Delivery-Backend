package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.review.*;

import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Review;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.ReviewRepository;

import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final StoreRepository storeRepository;

    public ReviewResponseDto createReview(UUID storeId, ReviewRequestDto dto, UserDetailsImpl userDetails) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NoSuchElementException("해당되는 상점이 없습니다"));

        Order order = null;

        User user = userDetails.getUser();
        Role currentUserRole = userDetails.getUser().getRole();

        // CUSTOM만 접근 가능
        if (!currentUserRole.equals(Role.CUSTOMER)) {
            log.warn("권한이 없습니다 : {}", currentUserRole.getAuthority());

            throw new IllegalArgumentException("권한이 없습니다");
        }

        Review review = reviewRepository.save(
                Review.builder()
                        .score(dto.getRating())
                        .contents(dto.getComment())
                        .order(order)
                        .store(store)
                        .user(user)
                        .build()
        );

        return ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getScore())
                .comment(review.getContents())
                .created_at(review.getCreatedAt())
                .build();
    }

    //리뷰 단건 검색 기능
    public ReviewDetailsResponseDto getReviewDetails(UUID reviewId)  {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new NoSuchElementException("해당되는 리뷰가 없습니다.")
        );

        if(review.getDeletedBy() != null){
            throw new NoSuchElementException("삭제된 리뷰입니다.");
        }

        return ReviewDetailsResponseDto.builder()
                .id(review.getId())
                .rating(review.getScore())
                .comment(review.getContents())
                .customer_uuid(review.getUser().getId())
                .store_id(review.getStore().getId())
                .customer_id(review.getUser().getUsername())
                .created_at(review.getCreatedAt())
                .updated_at(review.getUpdatedAt())
                .deleted_at(review.getDeletedAt())
                .build();
    }

    //상점의 리뷰들 전체 검색 기능
    //생성일순, 수정일 , 10건, 30건, 50
    public ReviewStoreResponseDto getStoreReview(UUID storeId, int page, int size, String criteria, String sort) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new NoSuchElementException("해당되는 상점이 없습니다"));

        String pageCriteria  = criteria.equals("createdAt") ? "createdAt" : "updatedAt";

        Sort pageSort = sort.equals("ASC") ? Sort.by(Sort.Direction.ASC, pageCriteria)  : Sort.by(Sort.Direction.DESC, pageCriteria);

        Pageable pageable = PageRequest.of(page, size, pageSort);

        Page<Review> storeReview = reviewRepository.findByReview(store.getId(), pageable);

        return ReviewStoreResponseDto.builder()
                //페이지네이션 정보
                .page(storeReview.getNumber())
                .size(storeReview.getSize())
                .total(storeReview.getTotalPages())
                //상점의 리뷰들
                .reviews(
                        storeReview.stream()
                                .map(review -> ReviewResponseDto.builder()
                                        .id(review.getId())
                                        .rating(review.getScore())
                                        .comment(review.getContents())
                                        .created_at(review.getCreatedAt())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Transactional
    public ReviewUpdateResponseDto updateReview(UUID reviewId, ReviewUpdateRequestDto dto, UserDetailsImpl userDetails) {

        //리뷰 아이디랑 계정이 일치하는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다"));

        Role currentUserRole = userDetails.getUser().getRole();
        //다른 유저면 에러
        if(!review.getUser().getId().equals(userDetails.getUser().getId()) ||
                !currentUserRole.equals(Role.CUSTOMER)
        ){
            log.warn("계정 정보가 다릅니다. : {}", review.getUser().getId());
            log.warn("권한이 없습니다 : {}", currentUserRole.getAuthority());
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        review.update(dto.getRating(), dto.getComment());

        //일치하다면 변경 수행 일치하는것만 하는게 좋음
        return ReviewUpdateResponseDto.builder()
                .id(review.getId())
                .rating(review.getScore())
                .comment(review.getContents())
                .update_at(review.getCreatedAt())
                .build();
    }

    @Transactional
    public ReviewDeleteResponseDto deleteReview(UUID reviewId, UserDetailsImpl userDetails) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 리뷰입니다"));

        Role currentUserRole = userDetails.getUser().getRole();

        if (!(
                (currentUserRole.equals(Role.CUSTOMER)
                        && review.getUser().getId().equals(userDetails.getUser().getId()))
                        // 관리자인 경우: 아이디 비교 없이 허용.
                        || currentUserRole.equals(Role.MASTER)
        )) {
            log.warn("계정 정보가 다릅니다. : {}", review.getUser().getId());
            log.warn("권한이 없습니다 : {}", currentUserRole.getAuthority());
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        //삭제된 정보가 있으면 에러 발생
        if(review.getDeletedBy() != null){
            log.warn("이미 삭제된 리뷰입니다. : {}", review.getDeletedBy());
            throw new NoSuchElementException("이미 삭제된 리뷰입니다.");
        }

        review.delete(userDetails.getUser().getUsername());

        return ReviewDeleteResponseDto.builder()
                .message("리뷰가 삭제(숨김 처리)되었습니다.")
                .delete_at(review.getDeletedAt())
                .build();
    }

    //상점의 평점 평균 계산
    public Double selectStoreAverageRating(UUID storeId){
        Double averageRating = Math.round(reviewRepository.findByStoreAverageRating(storeId ) * 10.0) / 10.0;
        return averageRating;
    }
}
