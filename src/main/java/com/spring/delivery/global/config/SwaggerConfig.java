package com.spring.delivery.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi restApi(){ //rest controller

        return GroupedOpenApi.builder()
                .pathsToMatch("/api/**")
                .group("REST API")
                .build();
    }
    @Bean
    public GroupedOpenApi commonApi() { //general controller

        return GroupedOpenApi.builder()
                .pathsToMatch("/**/*")
                .pathsToExclude("/api/**/*") //exclude that begin with '/api'
                .group("COMMON API")
                .build();
    }



}
