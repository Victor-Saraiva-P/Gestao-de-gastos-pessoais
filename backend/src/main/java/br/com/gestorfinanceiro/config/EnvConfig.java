package br.com.gestorfinanceiro.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    private final Dotenv dotenv = Dotenv.load();

    @Bean
    public String jwtSecret() {
        return dotenv.get("JWT_SECRET");
    }

    @Bean
    public Long jwtExpiration() {
        return Long.parseLong(dotenv.get("JWT_EXPIRATION"));
    }
}