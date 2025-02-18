package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.review.*;

import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Review;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.entity.enumtype.Role;
import com.spring.delivery.domain.domain.repository.ReviewRepository;

import com.spring.delivery.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final String REVIEW_DELETE_MESSAGE = "리뷰가 삭제(숨김 처리)되었습니다.";

    private final ReviewRepository reviewRepository;

    //리뷰 생성 기능 (수정 필요)
    public ReviewResponseDto createReview(UUID storeId, ReviewRequestDto dto, UserDetailsImpl userDetails) {

        //storeid, orderid를 존재하는지 확인 필요
        //추후에 받아와서 해당 에러 핸들링 처리.
        //수정 필요
        Order order = null;
        Store store =null;

        User user = userDetails.getUser();
        Role currentUserRole = userDetails.getUser().getRole();

        // CUSTOM만 접근 가능
        if (!currentUserRole.equals(Role.CUSTOMER)) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        Review review = reviewRepository.save(
                Review.builder()
                        .score(dto.getRating())
                        .contents(dto.getComment())
                        .order(order)
                        .store(store)
                        .user(user)
                        .build());

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
                () -> new IllegalArgumentException("해당되는 리뷰가 없습니다.")
        );

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

    //상점의 리뷰들 전체 검색 기능(수정 필요)
    public ReviewStoreResponseDto getStoreReview(UUID storeId, int page, int size) {

        /*
        store에 대한 에러 핸들링 추가
        */
        //상의 후 sort 추가
        Pageable pageable = PageRequest.of(page, size);

        Page<Review> storeReview = reviewRepository.findByStore_Id(storeId, pageable);

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다"));

        Role currentUserRole = userDetails.getUser().getRole();
        //다른 유저면 에러
        if(!review.getUser().getId().equals(userDetails.getUser().getId()) ||
                !currentUserRole.equals(Role.CUSTOMER)
        ){
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다"));

        Role currentUserRole = userDetails.getUser().getRole();

        if (!(
                (currentUserRole.equals(Role.CUSTOMER)
                        && review.getUser().getId().equals(userDetails.getUser().getId()))
                        // 관리자인 경우: 아이디 비교 없이 허용.
                        || currentUserRole.equals(Role.MASTER)
        )) {
            throw new IllegalArgumentException("계정 정보가 다르거나 존재하지 않는 권한입니다.");
        }

        //삭제된 정보가 있으면 에러 발생
        if(review.getDeletedBy() != null){
            throw new IllegalArgumentException("이미 삭제된 리뷰입니다.");
        }

        review.delete(userDetails.getUser().getUsername());


        return ReviewDeleteResponseDto.builder()
                .message(REVIEW_DELETE_MESSAGE)
                .delete_at(review.getDeletedAt())
                .build();
    }
}
