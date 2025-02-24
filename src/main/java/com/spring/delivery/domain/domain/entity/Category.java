package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_category")
public class Category extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<StoreCategory> StoryCategories = new ArrayList<>();

    // 프라이빗 생성자
    private Category(String name) {
        this.name = name;
    }

    // 정적 팩토리 메서드
    public static Category of(String name) {
        return new Category(name);
    }

    public void updateName(String name) {
        this.name = name;
    }
}