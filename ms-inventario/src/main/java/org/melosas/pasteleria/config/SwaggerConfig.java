package org.melosas.pasteleria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Inventario - Pastelería Las Melosas")
                        .description("Microservicio para el control de existencias, materias primas y movimientos de almacén.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pastelería Las Melosas")
                                .email("inventario@melosas.org")));
    }
}
