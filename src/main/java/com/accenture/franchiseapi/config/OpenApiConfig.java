package com.accenture.franchiseapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franchiseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise API")
                        .description("""
                                API reactiva (Spring WebFlux + MongoDB) para la gestión de franquicias. \
                                Una franquicia se compone de un nombre y una lista de sucursales; \
                                cada sucursal tiene un nombre y un listado de productos con su stock.""")
                        .version("v1")
                        .contact(new Contact()
                                .name("Andres Prada")
                                .email("andres123prada123@gmail.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .tags(List.of(
                        new Tag().name("Franquicias").description("Operaciones sobre franquicias, sucursales y productos")));
    }
}
