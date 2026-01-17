package com.ejemplo.ddd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Pedidos - DDD Ejemplo")
                        .version("0.0.1")
                        .description("Documentación OpenAPI generada por springdoc-openapi")
                        .contact(new Contact().name("Desarrollador").email("david@example.com"))
                );
    }
}
