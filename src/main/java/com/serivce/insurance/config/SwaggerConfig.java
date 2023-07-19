package com.serivce.insurance.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info =    @Info(contact = @Contact(
                    name="ashitkumarrai",
                    email="ashit@virtusa.com",
                    url = ""
    ),
    description="Backend Rest APIs for Insurance Portal",
    title = "swagger documentation of insurance portal APIs",
    version = "1.0",
    termsOfService = "Terms of Service"
    ),
        servers = {
                @Server(
            description="LOCAL ENV",
                        url = "http://localhost:8080"
        )
    }


)
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {
    
}
