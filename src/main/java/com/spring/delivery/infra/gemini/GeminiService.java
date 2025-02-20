package com.spring.delivery.infra.gemini;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import com.spring.delivery.infra.exception.GeminiApiException;
import com.spring.delivery.infra.exception.GeminiServiceUnavailableException;
import com.spring.delivery.infra.exception.GeminiTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    /** Gemini 호출 후 가공하여 반환하는 담당*/

    // Access to API key and URL[Gemini]
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.create();
    private final GeminiRepository geminiRepository;
    private final StoreRepository storeRepository;

    /* 생성 */
    @Transactional
    public ApiResponseDto<GeminiResponseDto> saveAiSuggestion(String requestText, UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 권한 확인 (OWNER, MASTER만 가능)
        Set<String> allowedRoles = Set.of("ROLE_MASTER", "ROLE_OWNER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
        }

        // store entity check
        Store store =  storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return ApiResponseDto.fail(404, "해당 가게 정보를 찾을 수 없습니다.");
        }

        // 요청 데이터 생성
        // Construct the request payload
        // {"contents": [{"parts":[{"text": "질문할것"}]}]}
        String requestToGemini = ". Please write your answer as concisely as possible, no longer than 50 characters. If this question is not about the 'food menu,' reply that you should only ask questions related to the menu.";
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts",  new Object[] {
                                Map.of("text", requestText + requestToGemini)
                        })
                }
        );

        try {
            // call gemini
            String apiResponseJson = webClient.post()
                    .uri(geminiApiUrl + geminiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5)) // 응답시간 5초 초과 시 504 에러 처리
                    .block();

            // 응답 데이터 에서 필요한 필드만 추출. api response to Dto and text extraction
            String aiResponseText = extractResponseText(apiResponseJson);

            // Gemini 응답 저장
            Gemini gemini = Gemini.of(requestText, aiResponseText, store);
            geminiRepository.save(gemini);

            return ApiResponseDto.success(GeminiResponseDto.from(gemini));

        } catch (WebClientRequestException e) {
            log.warn("Gemini API 응답 시간 초과: {}", e.getMessage());
            throw new GeminiTimeoutException("Gemini API 추천 서비스 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.");
        } catch (WebClientResponseException e) {
            log.warn("Gemini API 요청 실패 - 상태 코드: {}, 메시지: {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().is5xxServerError()) {
                throw new GeminiServiceUnavailableException("현재 AI 추천 서비스를 이용할 수 없습니다. ");
            }
            throw new GeminiApiException("Gemini AI 추천 서비스 요청 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("Gemini API 호출 중 예상치 못한 오류 발생", e);
            throw new GeminiApiException("AI 추천 서비스 요청 중 오류가 발생했습니다.");
        }

    }

    /* 삭제 */
    @Transactional
    public ApiResponseDto<Void> deleteAiSuggestion(UUID geminiId, UserDetailsImpl userDetails) {
        // 권한 확인 (MASTER만 가능)
        Set<String> allowedRoles = Set.of("ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "삭제할 권한이 없습니다.");
        }

        Gemini gemini = geminiRepository.findById(geminiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 AI 추천 기록을 찾을 수 없습니다."));

        gemini.delete(userDetails.getUsername()); // soft delete

        return ApiResponseDto.success(null);
    }

    /* 단건 조회 */
    @Transactional(readOnly = true)
    public ApiResponseDto<GeminiResponseDto> getSuggestionById(UUID geminiId, UserDetailsImpl userDetails) {

        // 권한 확인 (MASTER만 가능)
        Set<String> allowedRoles = Set.of("ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
        }

        Gemini gemini = geminiRepository.findById(geminiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 추천 기록을 찾을 수 없습니다."));

        return ApiResponseDto.success(GeminiResponseDto.from(gemini));
    }

    /* 전체 조회 */
    @Transactional
    public ApiResponseDto<Map<String, Object>> getSuggestions(UserDetailsImpl userDetails, UUID storeId,  int page, int size, String sort, String order) {

        // 권한 확인 (MASTER만 가능)
        Set<String> allowedRoles = Set.of("ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
        }

        // 정렬 방향 설정 (desc or asc)
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));

        Page<Gemini> geminiPage;
        if (storeId != null) {
            geminiPage = geminiRepository.findByStoreId(storeId, pageable);
        } else {
            geminiPage = geminiRepository.findAll(pageable);
        }

        Map<String, Object> response = createPagedResponse(geminiPage);

        return ApiResponseDto.success(response);
    }

    /* 검색 */
    @Transactional
    public ApiResponseDto<Map<String, Object>> searchSuggestions(
            UserDetailsImpl userDetails, UUID storeId, String keyword,
            int page, int size, String sort, String order) {

        // 권한 확인 (MASTER만 가능)
        Set<String> allowedRoles = Set.of("ROLE_MASTER");

        if (!lacksAuthority(userDetails, allowedRoles)) {
            return ApiResponseDto.fail(403, "열람할 권한이 없습니다.");
        }

        // 정렬 방향 설정 (desc or asc)
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sort));

        Page<Gemini> geminiPage;
        if (storeId == null && (keyword == null || keyword.isBlank())) {
            geminiPage = geminiRepository.findAll(pageable); // 가게 x, 키워드 o
        } else if (storeId == null) {
            geminiPage = geminiRepository.findByResponseTextContaining(keyword, pageable); // 키워드 o,  가게 x
        } else if (keyword == null || keyword.isBlank()) {
            geminiPage = geminiRepository.findByStoreId(storeId, pageable); // 키워드 x, 가게 o
        } else {
            geminiPage = geminiRepository.findByStoreIdAndResponseTextContaining(storeId, keyword, pageable); // 가게 o, 키워드 x
        }

        Map<String, Object> response = createPagedResponse(geminiPage);

        return ApiResponseDto.success(response);
    }

    // gemini 응답 가공하는 메서드
    private String extractResponseText(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            log.error("Gemini API 응답 데이터 파싱 실패", e);
            throw new GeminiApiException("AI 추천 서비스 응답 데이터 처리 중 오류가 발생했습니다.");
        }
    }

    // 페이징된 데이터를 반환
    private Map<String, Object> createPagedResponse(Page<Gemini> geminiPage) {
        List<GeminiResponseDto> geminiList = geminiPage.getContent().stream()
                .map(GeminiResponseDto::from)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totals", geminiPage.getTotalElements());
        response.put("currentPage", geminiPage.getNumber() + 1);
        response.put("totalPages", geminiPage.getTotalPages());
        response.put("pageSize", geminiPage.getSize());
        response.put("suggestions", geminiList);

        return response;
    }

    private boolean lacksAuthority(UserDetails userDetails, Set<String> requiredRoles) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        log.info("현재 사용자 권한: {}", authorities);

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requiredRoles::contains);
    }

}
