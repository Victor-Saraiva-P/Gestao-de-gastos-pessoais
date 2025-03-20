package br.com.gestorfinanceiro.config;

import br.com.gestorfinanceiro.exceptions.ApiError;
import br.com.gestorfinanceiro.services.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll() // Rotas públicas
                        .requestMatchers("/users/admin/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/users/**").authenticated() // Rotas protegidas
                        .requestMatchers("/receitas/**").hasAnyRole(ADMIN_ROLE, USER_ROLE) // Apenas USER e ADMIN podem acessar as rotas de receitas
                        .requestMatchers("/despesas/**").hasAnyRole(ADMIN_ROLE, USER_ROLE)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // Trata 401 Unauthorized (usuário não autenticado)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");

                            ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,
                                    "Você não está autenticado para acessar este recurso.");
                            response.getWriter().write(apiError.toJson());
                        })

                        // Trata 403 Forbidden (usuário autenticado, mas sem permissão)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Acesso negado para este recurso.");
                            response.getWriter().write(apiError.toJson());
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
