package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ReviewDetailsResponseDto;
import com.spring.delivery.domain.controller.dto.ReviewRequestDto;
import com.spring.delivery.domain.controller.dto.ReviewResponseDto;
import com.spring.delivery.domain.controller.dto.ReviewStoreResponseDto;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Review;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    //리뷰 생성 기능 (수정 필요)
    public ReviewResponseDto createReview(UUID storeId, ReviewRequestDto dto, User user) {

        //storeid, orderid를 존재하는지 확인 필요
        //추후에 받아와서 해당 에러 핸들링 처리.
        //수정 필요
        Order order = null;
        Store store =null;

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

}
