package com.spring.delivery.domain.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "p_review")
public class Review extends BaseEntity{
    @Id
    @UuidGenerator
    private UUID id;

    private String contents;

    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Review (String contents, Double score, User user, Store store, Order order) {
        this.contents = contents;
        this.score = score;
        this.user = user;
        this.store = store;
        this.order = order;
    }

    public void update(Double score, String contents){
        this.score = score;
        this.contents = contents;
    }

}
