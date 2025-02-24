package com.spring.delivery.domain.service;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.domain.entity.Store;
import com.spring.delivery.domain.domain.repository.StoreRepository;
import com.spring.delivery.global.security.UserDetailsImpl;
import com.spring.delivery.infra.exception.GeminiServiceUnavailableException;
import com.spring.delivery.infra.exception.GeminiTimeoutException;
import com.spring.delivery.infra.gemini.Gemini;
import com.spring.delivery.infra.gemini.GeminiRepository;
import com.spring.delivery.infra.gemini.GeminiResponseDto;
import com.spring.delivery.infra.gemini.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // 각 테스트마다 새 인스턴스 생성
@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private WebClient webClient; // Gemini API 호출 Mocking

    @Mock
    private GeminiRepository geminiRepository; // DB 저장 Mocking

    @Mock
    private StoreRepository storeRepository; // Store 조회 Mocking

    @Mock
    private UserDetailsImpl userDetails; // 사용자 권한 Mocking

    @Spy
    @InjectMocks
    private GeminiService geminiService;

    @Test
    @DisplayName("추천 응답 저장 성공")
    void testSaveAiSuggestion_Success() {
        // 모든 목 객체 초기화
        //Mockito.reset(webClient, storeRepository, geminiRepository, userDetails);
        Mockito.clearInvocations(webClient, storeRepository, geminiRepository, userDetails);
        // WebClient 필드를 목 객체로 강제 주입
        ReflectionTestUtils.setField(geminiService, "webClient", webClient);

        ReflectionTestUtils.setField(geminiService, "geminiApiUrl", "https://dummyApi.com/");
        ReflectionTestUtils.setField(geminiService, "geminiApiKey", "dummyKey");
        // given
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_MASTER"));
        doReturn(authorities).when(userDetails).getAuthorities();

        UUID storeId = UUID.randomUUID();
        String requestText = "테스트";
        String geminiResponse = "{ \"candidates\": [{ \"content\": { \"parts\": [{ \"text\": \"테스트\" }] } }] }";

        Store mockStore = mock(Store.class);
        when(mockStore.getId()).thenReturn(storeId);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));

        // 외부 api 호출
        WebClient.RequestBodyUriSpec requestBodySpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // 메서드 호출을 가로채서 목 객체를 반환
        // http post 요청 생성
        when(webClient.post()).thenAnswer(invocation -> requestBodySpec);
        // 요청할 url 지정
        when(requestBodySpec.uri(anyString())).thenAnswer(invocation -> requestBodySpec);
        // http 헤더 추가
        when(requestBodySpec.header(anyString(), any())).thenAnswer(invocation -> requestBodySpec);
        // 요청 본문 추가
        when(requestBodySpec.bodyValue(any())).thenAnswer(invocation -> requestHeadersSpec);
        // 요청을 보내고 응답 받음
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);
        // 응답을 json 문자열로 반환
        when(responseSpec.bodyToMono(String.class)).thenAnswer(invocation -> Mono.just(geminiResponse));

        Gemini mockGemini = mock(Gemini.class);

        when(geminiRepository.save(any(Gemini.class))).thenReturn(mockGemini);

        // when
        ApiResponseDto<GeminiResponseDto> response = geminiService.saveAiSuggestion(requestText, storeId, userDetails);

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getData().getResponseText());
        assertFalse(response.getData().getResponseText().isEmpty());

    }

    @Test
    @DisplayName("권한 부족 403")
    void testSaveAiSuggestion_Forbidden() {
        // 모든 목 객체 초기화
        //Mockito.reset(webClient, storeRepository, geminiRepository, userDetails);
        Mockito.clearInvocations(webClient, storeRepository, geminiRepository, userDetails);
        // WebClient 필드를 목 객체로 강제 주입
        ReflectionTestUtils.setField(geminiService, "webClient", webClient);

        ReflectionTestUtils.setField(geminiService, "geminiApiUrl", "https://dummyApi.com/");
        ReflectionTestUtils.setField(geminiService, "geminiApiKey", "dummyKey");
        // given
        // 기존 셋업에서 owner로 고정되어 있어 덮어쓰기가 필요
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        doReturn(authorities).when(userDetails).getAuthorities();

        // when
        ApiResponseDto<GeminiResponseDto> response = geminiService.saveAiSuggestion("추천할 메뉴는?", UUID.randomUUID(), userDetails);

        // then
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertEquals("생성할 권한이 없습니다.", response.getMessage());
    }


    @Test
    @DisplayName("gemini api 응답 지연 - 504")
    void testSaveAiSuggestion_GeminiTimeout() {
        // 모든 목 객체 초기화
        //Mockito.reset(webClient, storeRepository, geminiRepository, userDetails);
        Mockito.clearInvocations(webClient, storeRepository, geminiRepository, userDetails);
        // WebClient 필드를 목 객체로 강제 주입
        ReflectionTestUtils.setField(geminiService, "webClient", webClient);

        ReflectionTestUtils.setField(geminiService, "geminiApiUrl", "https://dummyApi.com/");
        ReflectionTestUtils.setField(geminiService, "geminiApiKey", "dummyKey");
        // given
        // 기존 셋업에서 owner로 고정되어 있어 덮어쓰기가 필요
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_OWNER"));
        doReturn(authorities).when(userDetails).getAuthorities();

        UUID storeId = UUID.randomUUID();
        Store mockStore = mock(Store.class);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenAnswer(invocation -> requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenAnswer(invocation -> requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), any())).thenAnswer(invocation -> requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);


        WebClientRequestException timeoutException = new WebClientRequestException(
                new IOException("Timeout Exception"),
                HttpMethod.POST,
                URI.create("https://dummy.com"),
                HttpHeaders.EMPTY
        );
        doThrow(timeoutException).when(responseSpec).bodyToMono(String.class);

        // when & then
        Exception thrown = assertThrows(Exception.class, () -> {
            geminiService.saveAiSuggestion("테스트", storeId, userDetails);
        });

        Throwable unwrapped = reactor.core.Exceptions.unwrap(thrown);
        unwrapped = reactor.core.Exceptions.unwrap(unwrapped); // 두 번 언랩!

        assertTrue(unwrapped instanceof GeminiTimeoutException);
        assertTrue(unwrapped.getMessage().contains("Gemini API 추천 서비스 응답 시간이 초과되었습니다."));
        assertTrue(unwrapped.getMessage().contains("Timeout Exception"));
    }

}


