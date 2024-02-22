package br.com.finance.manager.api.configs.springdoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(getApiSecurityComponents())
            .info(getApiInfo());
    }

    private Components getApiSecurityComponents() {
        return new Components()
            .addSecuritySchemes("bearer-key",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .scheme("JWT"));
    }

    private Info getApiInfo() {
        return new Info()
            .title("Finance Manager API")
            .version("0.0.1-SNAPSHOT")
            .description("API para gestão de finanças pessoais")
            .contact(new Contact()
                .name("Vitor Augusto Silva")
                .email("vitor.augsilva98@gmail.com")
                .url("https://github.com/vitorsilva98/"));
    }
}
