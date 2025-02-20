package com.spring.delivery.domain.domain.repository;

import com.spring.delivery.domain.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query(value = "SELECT AVG(r.score) FROM Review r WHERE r.store.id = :storeId" )
    Double findByStoreAverageRating(UUID storeId);

    @Query(value = "SELECT r FROM Review r Where r.store.id = :storeId and r.deletedBy is null")
    Page<Review> findByReview(UUID storeId, Pageable pageable);
}
