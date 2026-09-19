package com.evangeliakostop.paymentsystem.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Payment System API", version = "v1",
                description = "Endpoint for payment")
)
public class OpenApiConfig {
}
