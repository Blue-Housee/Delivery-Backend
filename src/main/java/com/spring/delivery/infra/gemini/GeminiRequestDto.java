package com.spring.delivery.infra.gemini;


import com.spring.delivery.domain.domain.entity.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class GeminiRequestDto {

    private String requestText;
    private String responseText;
    // 가게 ID
    private UUID storeId;

    private GeminiRequestDto(String requestText, String responseText, Store store) {
        this.requestText = requestText;
        this.responseText = responseText;
        this.storeId = store.getId();
    }

    public static GeminiRequestDto of(String requestText, String responseText, Store store) {
        return new GeminiRequestDto(requestText, responseText, store);
    }


}
