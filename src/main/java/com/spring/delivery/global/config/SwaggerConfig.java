package com.spring.delivery.global.config;

import com.spring.delivery.domain.controller.dto.ApiResponseDto;
import com.spring.delivery.domain.controller.dto.user.SignInRequestDto;
import com.spring.delivery.domain.controller.dto.user.SignInResponseDto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        public GroupedOpenApi restApi(){ //rest controller

                return GroupedOpenApi.builder()
                        .pathsToMatch("/api/")
                        .group("REST API")
                        .build();
        }
        @Bean
        public GroupedOpenApi commonApi() { //general controller

                return GroupedOpenApi.builder()
                        .pathsToMatch("//")
                        .pathsToExclude("/api/**/") //exclude that begin with '/api'
                        .group("COMMON API")
                        .build();
        }

        private SecurityScheme createAPIKeyScheme() {
                return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer");
        }

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI().addSecurityItem(new SecurityRequirement().
                                addList("Bearer Authentication"))
                        .components(new Components().addSecuritySchemes
                                ("Bearer Authentication", createAPIKeyScheme()))
                        .info(new Info().title("My REST API")
                                .description("Some custom description of API.")
                                .version("1.0").contact(new Contact().name("Sallo Szrajbman")
                                        .email( "www.baeldung.com").url("salloszraj@gmail.com"))
                                .license(new License().name("License of API")
                                        .url("API license URL")))
                        .path("/api/user/signIn", new PathItem().post(
                                new Operation()
                                        .summary("로그인")
                                        .description("Spring Security 필터를 통해 로그인 수행")
                                        .tags(List.of("Auth"))
                                        .requestBody(new RequestBody()
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<SignInRequestDto>().example(new SignInRequestDto("testUser", "password123"))))))
                                        .responses(new ApiResponses()
                                                .addApiResponse("200", new ApiResponse().description("로그인 성공")
                                                        .content(new Content().addMediaType("application/json",
                                                                new MediaType().schema(new Schema<ApiResponseDto<SignInResponseDto>>()
                                                                        .example(new ApiResponseDto<>(
                                                                                200,
                                                                                "요청이 성공적으로 처리되었습니다.",
                                                                                SignInResponseDto.builder().token("Bearer eygb...").build()
                                                                        ))))))
                                                .addApiResponse("401", new ApiResponse().description("로그인 실패")
                                                        .content(new Content().addMediaType("application/json",
                                                                new MediaType().schema(new Schema<ApiResponseDto<SignInResponseDto>>()
                                                                        .example(new ApiResponseDto<>(
                                                                                401,
                                                                                "로그인 실패",
                                                                                null
                                                                        ))))))
                                        )
                        ));
        }
}