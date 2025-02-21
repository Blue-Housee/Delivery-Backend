package com.spring.delivery.infra.gemini;

import com.spring.delivery.domain.domain.entity.BaseEntity;
import com.spring.delivery.domain.domain.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Entity
@Table(name = "p_gemini")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 프록시 객체 때문에 사용해야하기때문에....
public class Gemini extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT", name = "request_text")
    private String requestText;

    @Column(nullable = false, columnDefinition = "TEXT", name = "response_text")
    private String responseText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private Gemini(String requestText, String responseText,  Store store) {
        this.requestText = requestText;
        this.responseText = responseText;
        this.store = store;
    }

    public static Gemini of(String requestText, String responseText, Store store) {
        return new Gemini(requestText, responseText, store);
    }

}
