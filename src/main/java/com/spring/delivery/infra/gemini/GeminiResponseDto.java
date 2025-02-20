package com.spring.delivery.infra.gemini;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class GeminiResponseDto {

    private UUID id;
    private String requestText;
    private String responseText;
    private UUID storeId;
    private LocalDateTime createdAt;
    private String createdBy;

    @Builder
    private GeminiResponseDto(UUID id, String requestText, String responseText, UUID storeId, LocalDateTime createdAt, String createdBy) {
        this.id = id;
        this.requestText = requestText;
        this.responseText = responseText;
        this.storeId = storeId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public static GeminiResponseDto from(Gemini gemini) {
        return new GeminiResponseDto(
                gemini.getId(),
                gemini.getRequestText(),
                gemini.getResponseText(),
                gemini.getStore().getId(),
                gemini.getCreatedAt(),
                gemini.getCreatedBy()
        );
    }
}
