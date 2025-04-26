package com.keakimleang.digital_menu.configs;


import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenAPIConfig {
    String schemeName = "bearerAuth";
    String bearerFormat = "JWT";
    String scheme = "bearer";

    @Bean
    public OpenAPI digitalMenuAPI() {
        return new OpenAPI()
                .info(new Info().title("Digital Menu API")
                        .description("This is a Digital Menu API")
                        .version("v0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Digital Menu API wiki documentation")
                        .url("https://www.keakimleang.com"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(scheme)
                                .bearerFormat(bearerFormat).in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                );
    }
}