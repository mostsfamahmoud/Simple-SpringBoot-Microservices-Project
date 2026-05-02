package com.techie.microservices.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the API Gateway.
 *
 * <p>This class is responsible for two concerns:
 * <ol>
 *   <li>Defining which endpoints are publicly accessible and which require a valid JWT token.</li>
 *   <li>Configuring Cross-Origin Resource Sharing (CORS) rules so that browsers
 *       can make cross-origin requests to this gateway.</li>
 * </ol>
 *
 * <p>This class is the single source of truth for security configuration.
 * Do NOT add a separate {@code WebMvcConfigurer} CORS config — Spring Security's
 * filter chain runs before Spring MVC and would override it anyway.
 */
@Configuration
public class SecurityConfig {

    /**
     * A list of URL patterns that are publicly accessible without authentication.
     *
     * <ul>
     *   <li>{@code /swagger-ui.html} — The main Swagger UI HTML page.</li>
     *   <li>{@code /swagger-ui/**} — Static assets (JS, CSS) used by Swagger UI.</li>
     *   <li>{@code /v3/api-docs/**} — OpenAPI 3.0 JSON docs exposed by SpringDoc.</li>
     *   <li>{@code /swagger-resources/**} — Legacy Swagger 2.x resource endpoints.</li>
     *   <li>{@code /aggregate/**} — Gateway routes used to fetch API docs from
     *       downstream services (Product, Order, Inventory).</li>
     * </ul>
     */
    private final String[] freeResourceUrls = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/aggregate/**"
    };

    /**
     * Configures the main security filter chain for the API Gateway.
     *
     * <p>Every incoming HTTP request passes through this filter chain in order:
     * <ol>
     *   <li><b>CORS check</b> — Validates cross-origin requests using
     *       {@link #corsConfigurationSource()}. Preflight OPTIONS requests
     *       are handled here and short-circuited before reaching auth checks.</li>
     *   <li><b>Authorization check</b> — URLs in {@code freeResourceUrls} are
     *       permitted without a token. All other requests must carry a valid JWT.</li>
     *   <li><b>JWT validation</b> — The JWT token is validated against the issuer
     *       URI configured in {@code application.yml} (your Keycloak server).</li>
     * </ol>
     *
     * @param httpSecurity the {@link HttpSecurity} builder provided by Spring Security
     * @return a fully configured {@link SecurityFilterChain}
     * @throws Exception if any configuration step fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                        // Allow public access to Swagger and API doc aggregation endpoints
                        .requestMatchers(freeResourceUrls).permitAll()
                        // Every other request must have a valid JWT token
                        .anyRequest().authenticated())
                // Use our custom CORS rules defined in corsConfigurationSource()
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                // Configure this service as an OAuth2 Resource Server that validates JWTs
                .oauth2ResourceServer(serverConfigurer -> serverConfigurer.jwt(Customizer.withDefaults()))
                .build();
    }

    /**
     * Defines the CORS policy applied to all incoming requests.
     *
     * <p>CORS (Cross-Origin Resource Sharing) is a browser security mechanism that
     * blocks requests made from one origin (e.g., {@code http://localhost:4200}) to a
     * different origin (e.g., {@code http://localhost:9000}) unless the server explicitly
     * permits it via CORS headers.
     *
     * <p>Before the actual request, browsers send a <b>preflight</b> OPTIONS request
     * to check if the server allows the cross-origin call. That is why {@code OPTIONS}
     * must always be included in the allowed methods.
     *
     * <p>This configuration:
     * <ul>
     *   <li>Allows requests from any origin ({@code *}).</li>
     *   <li>Allows the most common HTTP methods including {@code OPTIONS} for preflight.</li>
     *   <li>Allows any request headers (e.g., {@code Authorization}, {@code Content-Type}).</li>
     *   <li>Applies these rules to all URL paths ({@code /**}).</li>
     * </ul>
     *
     * <p><b>Note:</b> In production you should replace {@code "*"} in
     * {@code setAllowedOrigins} with your actual frontend origin(s) for better security,
     * e.g., {@code List.of("http://localhost:4200", "https://yourapp.com")}.
     *
     * @return a {@link CorsConfigurationSource} applied to all routes
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow requests from any origin.
        // In production, replace "*" with specific allowed origins.
        corsConfiguration.setAllowedOrigins(List.of("*"));

        // Allow these HTTP methods.
        // OPTIONS is required for browser preflight requests.
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow any request headers (e.g., Authorization, Content-Type)
        corsConfiguration.setAllowedHeaders(List.of("*"));

        // Apply this CORS configuration to all URL paths in the gateway
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return corsConfigurationSource;
    }
}