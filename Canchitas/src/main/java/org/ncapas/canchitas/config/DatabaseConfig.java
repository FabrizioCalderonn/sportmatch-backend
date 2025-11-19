package org.ncapas.canchitas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        String url = databaseUrl;

        // Convertir formato Railway a JDBC si es necesario
        if (url.startsWith("postgresql://") && !url.startsWith("jdbc:")) {
            url = "jdbc:" + url;
        }

        // Si DATABASE_URL está vacía, usar configuración por defecto
        if (url.isEmpty()) {
            return DataSourceBuilder.create().build();
        }

        // Parsear URL para extraer credenciales
        String username = "";
        String password = "";
        String jdbcUrl = url;

        try {
            // Formato: jdbc:postgresql://user:pass@host:port/db
            if (url.contains("@")) {
                String[] parts = url.split("@");
                String credentials = parts[0].substring(parts[0].indexOf("://") + 3);
                String[] creds = credentials.split(":");
                username = creds[0];
                password = creds.length > 1 ? creds[1] : "";
                jdbcUrl = "jdbc:postgresql://" + parts[1];
            }
        } catch (Exception e) {
            // Si falla el parseo, intentar con variables de entorno separadas
            return DataSourceBuilder.create().build();
        }

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username.isEmpty() ? System.getenv("PGUSER") : username)
                .password(password.isEmpty() ? System.getenv("PGPASSWORD") : password)
                .build();
    }
}
