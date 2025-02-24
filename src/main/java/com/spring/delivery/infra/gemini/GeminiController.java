package com.spring.delivery.infra.gemini;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    // ai 추천 생성
    @PostMapping("/ai-suggestion")
    public ResponseEntity<ApiResponseDto<GeminiResponseDto>> createAiSuggestion(
            @RequestBody GeminiRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        ApiResponseDto<GeminiResponseDto> responseDto = geminiService.saveAiSuggestion(requestDto.getRequestText(), requestDto.getStoreId(), userDetails);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    // 응답 삭제
    @DeleteMapping("/ai-suggestion/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteAiSuggestion(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ApiResponseDto<Void> responseDto = geminiService.deleteAiSuggestion(id, userDetails);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }


    // 단건 조회
    @GetMapping("/ai-suggestion/{id}")
    public ResponseEntity<ApiResponseDto<GeminiResponseDto>> getAiSuggestion(@PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ApiResponseDto<GeminiResponseDto> responseDto = geminiService.getSuggestionById(id, userDetails);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    // 전체 조회
    @GetMapping("/ai-suggestion")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>>  getAllAiSuggestions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order
    ) {

        ApiResponseDto<Map<String, Object>> responseDto = geminiService.getSuggestions(userDetails,storeId, page, size, sort, order);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    /* 검색 */
    @GetMapping("/ai-suggestion/search")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> searchAiSuggestions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order) {

        ApiResponseDto<Map<String, Object>> responseDto = geminiService.searchSuggestions(userDetails, storeId, keyword, page, size, sort, order);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }



}
