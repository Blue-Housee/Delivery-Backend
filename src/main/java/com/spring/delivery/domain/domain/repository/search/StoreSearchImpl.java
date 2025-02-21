package com.spring.delivery.domain.domain.repository.search;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.spring.delivery.domain.domain.entity.QStore;
import com.spring.delivery.domain.domain.entity.QStoreCategory;
import com.spring.delivery.domain.domain.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StoreSearchImpl implements StoreSearch {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Store> searchStores(String categoryName, String storeName, Pageable pageable) {
        QStore store = QStore.store;
        QStoreCategory storeCategory = QStoreCategory.storeCategory; // 중간 테이블 Q타입 추가

        // 기본 쿼리
        JPAQuery<Store> query = queryFactory.selectFrom(store)
                .join(store.storeCategories, storeCategory)
                .where(store.deletedAt.isNull()); // 중간 테이블을 통한 조인

        // 카테고리 필터링
        if (categoryName != null && !categoryName.isEmpty()) {
            query.where(storeCategory.category.name.eq(categoryName)); // StoreCategory의 Category를 조인
        }

        // 지점 이름 필터링
        if (storeName != null && !storeName.isEmpty()) {
            query.where(store.name.contains(storeName));
        }

        // 페이징 처리
        List<Store> stores = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.fetchCount();

        return new PageImpl<>(stores, pageable, total);
    }
}

