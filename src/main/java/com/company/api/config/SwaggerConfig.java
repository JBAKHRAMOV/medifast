package com.company.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket swaggerConfiguration() {

        Set<String> consumes = new HashSet<>();
        consumes.add(MediaType.APPLICATION_JSON_VALUE);

        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(consumes)
                .produces(consumes)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.company"))
                .build()
                .apiInfo(apiDetails());

    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "ADMIN DASHBOARD API",
                "API docs for ADMIN-DASHBOARD",
                "1.0",
                "Searching",
                new Contact("Akobir Abduganiev",
                        "eduspace.me",
                        "akobir.abduganiev@ya.ru"),
                "",
                "",
                Collections.emptyList()
        );
    }

}
