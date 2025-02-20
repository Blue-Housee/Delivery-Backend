package com.spring.delivery.infra.gemini;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface GeminiRepository extends JpaRepository<Gemini, UUID> {

    // 가게별 AI 추천 내역 조회(전체)
    Page<Gemini> findByStoreId(UUID storeId, Pageable pageable);

    // 가게별 AI 추천 내역 중, 특정 키워드를 포함한 결과
    Page<Gemini> findByStoreIdAndResponseTextContaining(UUID storeId, String keyword, Pageable pageable);

    // 모든 추천 내역 중, 특정 키워드를 포함한 결과
    Page<Gemini> findByResponseTextContaining(String keyword, Pageable pageable);
}
