package com.spring.delivery.domain.service;

import com.spring.delivery.domain.config.IntegrationTestBase;
import com.spring.delivery.domain.controller.dto.review.*;
import com.spring.delivery.domain.domain.entity.Review;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.entity.User;
import com.spring.delivery.domain.domain.repository.ReviewRepository;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.domain.domain.repository.UserRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest extends IntegrationTestBase {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;


    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        User user = userDetails.getUser();

        Store dummyStore = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        dummyStore = storeRepository.save(dummyStore);
        UUID dummyStoreId = dummyStore.getId();

        ReviewRequestDto requestDto = new ReviewRequestDto();
        requestDto.setRating(5.0);
        requestDto.setComment("Excellent service");

        ReviewResponseDto response = reviewService.createReview(dummyStoreId, requestDto, userDetails);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Excellent service", response.getComment());
        assertNotNull(response.getCreated_at());
    }

    @Test
    @DisplayName("리뷰 단건 검색 성공")
    void getReviewDetails_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        User user = userDetails.getUser();

        Store dummyStore = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        dummyStore = storeRepository.save(dummyStore);
        UUID dummyStoreId = dummyStore.getId();

        Review review = Review.builder()
                .score(4.5)
                .contents("Good product")
                .user(user)
                .store(dummyStore) // 더미 store 할당
                .build();

        review = reviewRepository.save(review);

        ReviewDetailsResponseDto details = reviewService.getReviewDetails(review.getId());

        assertNotNull(details);
        assertEquals(review.getId(), details.getId());
        assertEquals(4.5, details.getRating());
        assertEquals("Good product", details.getComment());
        assertEquals(user.getId(), details.getCustomer_uuid());
    }

    @Test
    @DisplayName("상점 리뷰 전체 검색 성공")
    void getStoreReview_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        User user = userDetails.getUser();

        Store dummyStore = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        dummyStore = storeRepository.save(dummyStore);
        UUID dummyStoreId = dummyStore.getId();

        for (int i = 0; i < 5; i++) {
            Review review = Review.builder()
                    .score(0.0+i)
                    .contents("Review " + i)
                    .user(user)
                    .store(dummyStore)
                    .build();
            reviewRepository.save(review);
        }

        ReviewStoreResponseDto storeReviews = reviewService.getStoreReview(dummyStoreId, 0, 3, "createdAt", "ESC");
        assertNotNull(storeReviews);
        assertEquals(0, storeReviews.getPage());
        assertEquals(3, storeReviews.getSize());
        assertTrue(storeReviews.getReviews().size() <= 3);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        User user = userDetails.getUser();

        Store dummyStore = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        dummyStore = storeRepository.save(dummyStore);
        UUID dummyStoreId = dummyStore.getId();

        ReviewRequestDto createDto = new ReviewRequestDto ();
        createDto.setRating(3.5);
        createDto.setComment("Average product");

        UUID id = UUID.randomUUID();
        createDto.setOrderId(id);
        ReviewResponseDto createResponse = reviewService.createReview(dummyStoreId, createDto, userDetails);
        UUID reviewId = createResponse.getId();

        ReviewUpdateRequestDto updateDto = ReviewUpdateRequestDto.builder()
                .rating(4.0)
                .comment("Good product")
                .build();
        ReviewUpdateResponseDto updateResponse = reviewService.updateReview(reviewId, updateDto, userDetails);
        assertNotNull(updateResponse);
        assertEquals(reviewId, updateResponse.getId());
        assertEquals(4, updateResponse.getRating());
        assertEquals("Good product", updateResponse.getComment());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_success() {
        UserDetailsImpl userDetails = userFixtureGenerator.createdPrincipalFixture();

        User user = userDetails.getUser();

        Store dummyStore = Store.of("testStore", "test","010-1234-1234",true, LocalTime.now(),LocalTime.now(), user);
        dummyStore = storeRepository.save(dummyStore);
        UUID dummyStoreId = dummyStore.getId();

        ReviewRequestDto createDto = new ReviewRequestDto();
        createDto.setRating(2);
        createDto.setComment("Not satisfied");

        ReviewResponseDto createResponse = reviewService.createReview(dummyStoreId, createDto, userDetails);
        UUID reviewId = createResponse.getId();

        ReviewDeleteResponseDto deleteResponse = reviewService.deleteReview(reviewId, userDetails);
        assertNotNull(deleteResponse);
        assertEquals("리뷰가 삭제(숨김 처리)되었습니다.", deleteResponse.getMessage());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                reviewService.deleteReview(reviewId, userDetails)
        );
        assertEquals("이미 삭제된 리뷰입니다.", exception.getMessage());
    }
}
