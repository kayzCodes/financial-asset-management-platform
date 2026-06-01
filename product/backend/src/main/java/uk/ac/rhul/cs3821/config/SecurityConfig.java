package uk.ac.rhul.cs3821.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Security configuration for the application.
 * Sets up authentication, authorization, and required security filters.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final FirebaseTokenFilter firebaseTokenFilter;

  /**
   * Configures and builds the application's security filter chain.
   *
   * @param http the HttpSecurity configuration object
   * @return the configured SecurityFilterChain
   * @throws Exception if the security setup fails
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors(cors -> cors.configurationSource(request -> {
              CorsConfiguration config = new CorsConfiguration();
              config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
              config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
              config.setAllowedHeaders(List.of("*"));
              config.setExposedHeaders(List.of("*"));
              config.setAllowCredentials(true);
              return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    // ---- CORS PREFLIGHT (REQUIRED) ----
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    // ---- PUBLIC ENDPOINTS ----
                    .requestMatchers(HttpMethod.POST, "/api/user/registerUser").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/user/checkUserByFirebaseUid/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/user/getUserByUid/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/user/getUser").permitAll()
                    // for now
                    .requestMatchers("/api/news/**").permitAll()
                    // ---- STOCK ENDPOINTS (AUTH REQUIRED) ----
                    .requestMatchers("/api/userStock/**").authenticated()
                    // ---- CRYPTO ENDPOINTS ----
                    .requestMatchers("/api/userCrypto/**").authenticated()
                    // ---- GOAL ENDPOINTS ----
                    .requestMatchers("/api/userGoal/**").authenticated()
                    // ---- PORTFOLIO ENDPOINTS ----
                    .requestMatchers("/api/portfolio/**").authenticated()

                    // ---- SAVING ENDPOINTS ----
                    .requestMatchers("/api/savings/**").authenticated()
                    // Everything else
                    .anyRequest().authenticated()
            )
            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);
    return http.build();
  }
}