package org.melosas.pasteleria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI pagoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Pagos - Pastelería Las Melosas")
                        .description("Microservicio para el procesamiento y administración de pagos y cobros.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pastelería Las Melosas")
                                .email("pagos@melosas.org")));
    }
}
