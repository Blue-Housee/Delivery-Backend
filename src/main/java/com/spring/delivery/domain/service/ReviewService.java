package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ReviewRequestDto;
import com.spring.delivery.domain.controller.dto.ReviewResponseDto;
import com.spring.delivery.domain.domain.entity.Order;
import com.spring.delivery.domain.domain.entity.Review;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

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

        return new ReviewResponseDto(review.getId(), review.getScore(),
                review.getContents(), review.getCreatedAt());
    }

    public ReviewResponseDto getReviewDetails(UUID reviewId)  {

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당되는 리뷰가 없습니다.")
        );

        return new ReviewResponseDto(review.getId(), review.getScore(), review.getContents(), review.getCreatedAt());
    }
}
