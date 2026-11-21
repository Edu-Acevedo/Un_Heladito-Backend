package cl.duoc.unheladito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI unHeladitoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Un Heladito - API REST")
                        .version("v1")
                        .description("Servicios REST para la tienda online Un Heladito."));
    }
}
