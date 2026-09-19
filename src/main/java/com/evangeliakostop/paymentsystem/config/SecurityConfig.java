package com.evangeliakostop.paymentsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // This creates a PasswordEncoder bean using BCryptPasswordEncoder, which is used to encode passwords
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        CorsConfigurationSource configurationSource = request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList(("*"))); // Allows requests from all origins
            config.setAllowedMethods(Collections.singletonList(("*"))); // Allows all HTTP methods
            config.setAllowedHeaders(Collections.singletonList(("*"))); //Allows all headers to be included in the requests
            config.setMaxAge(3600L); // Sets the maximum time (in seconds) that the CORS configuration will be cached by the browser
            return config;
        };

        http
                .cors(cors -> cors.configurationSource(configurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll() // This allows all requests (any endpoint in the application) to be accessed without authentication.
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());

        return http.build();
    }
}
