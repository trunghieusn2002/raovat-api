package com.raovat.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${apiTitle}") String apiTitle,
            @Value("${apiDescription}") String apiDescription,
            @Value("${apiVersion}") String apiVersion,
            @Value("${apiContactName}") String apiContactName,
            @Value("${apiContactEmail}") String apiContactEmail,
            @Value("${apiContactUrl}") String apiContactUrl
    ) {
        /*return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key"))
                .info(new Info().title("My API")
                        .description("This is a sample API"));

         */
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact().name(apiContactName).email(apiContactEmail).url(apiContactUrl))
                ).tags(new ArrayList<>(Collections.singletonList(
                        new Tag().name("User authentication").description("APIs for user authentication")
                )));
    }

}
