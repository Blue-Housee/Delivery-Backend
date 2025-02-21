package com.spring.delivery.domain.domain.repository.search;

import com.spring.delivery.domain.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreSearch {
    Page<Store> searchStores(String category, String storeName, Pageable pageable);
}
