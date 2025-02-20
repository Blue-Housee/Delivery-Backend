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
          return GeminiResponseDto.builder()
                  .id(gemini.getId())
                  .requestText(gemini.getRequestText())
                  .responseText(gemini.getResponseText())
                  .storeId(gemini.getStore().getId())
                  .createdAt(gemini.getCreatedAt())
                  .createdBy(gemini.getCreatedBy())
                  .build();

    }
}
