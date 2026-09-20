package com.evangeliakostop.paymentsystem.config.logging;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.EnumSet;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestResponseLoggingInterceptor());
    }

    @Bean
    public ServletContextInitializer filterRegistration() {
        return servletContext -> {

            FilterRegistration.Dynamic cachingFilter =
                    servletContext.addFilter("cachingFilter", new CachingFilter());

            cachingFilter.addMappingForUrlPatterns(
                    EnumSet.of(DispatcherType.REQUEST),
                    false,
                    "/*"
            );

            FilterRegistration.Dynamic correlationIdFilter =
                    servletContext.addFilter("correlationIdFilter", new CorrelationIdFilter());

            correlationIdFilter.addMappingForUrlPatterns(
                    EnumSet.of(DispatcherType.REQUEST),
                    false,
                    "/*"
            );
        };
    }
}

