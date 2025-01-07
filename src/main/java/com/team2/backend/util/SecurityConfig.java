package com.team2.backend.util;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.team2.backend.enums.UserRole;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity (not recommended for production)
                .cors(cors -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    // If you need credentials (cookies), do NOT use "*"
                    configuration.setAllowedOrigins(List.of("http://localhost:8080"));
                    // Or if no credentials needed, you can do "*"
                    // configuration.setAllowedOrigins(List.of("*"));

                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
                    configuration.setAllowCredentials(true); // if using cookies or Auth headers

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);

                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/register", "/user/login").permitAll() // Allow public access to these
                                                                                      // endpoints
                        .requestMatchers("/mod/**").hasAuthority(UserRole.MODERATOR.toString()) // Restrict
                                                                                                // "/MODERATOR/**"
                        .requestMatchers("/con/**").hasAuthority(UserRole.CONTRIBUTOR.toString()) // Restrict //
                                                                                                  // "CONTROBUTOR/**"
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS)) // Stateless session
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before
                                                                                         // username/password auth
                                                                                         // filter

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
