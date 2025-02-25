package br.com.gestorfinanceiro;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestorfinanceiroApplication {

    public static void main(String[] args) {
        // Carrega as variáveis do .env
        Dotenv dotenv = Dotenv.load();

        // Define as variáveis de ambiente no sistema
        System.setProperty("POSTGRES_DB_URL", dotenv.get("POSTGRES_DB_URL"));
        System.setProperty("POSTGRES_USER", dotenv.get("POSTGRES_USER"));
        System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD"));
        System.setProperty("LOGGING_LEVEL", dotenv.get("LOGGING_LEVEL"));

        SpringApplication.run(GestorfinanceiroApplication.class, args);
    }

}
