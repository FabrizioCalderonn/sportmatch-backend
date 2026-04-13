package org.ncapas.canchitas.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${PGUSER:postgres}")
    private String pgUser;

    @Value("${PGPASSWORD:admin}")
    private String pgPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        if (!databaseUrl.isEmpty()) {
            try {
                // Railway provee: postgresql://user:pass@host:port/db
                URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String db = uri.getPath().replaceFirst("/", "");

                config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + db);

                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    config.setUsername(userInfo.split(":")[0]);
                    config.setPassword(userInfo.split(":")[1]);
                } else {
                    config.setUsername(pgUser);
                    config.setPassword(pgPassword);
                }
            } catch (Exception e) {
                // Fallback a variables individuales
                config.setJdbcUrl("jdbc:postgresql://localhost:5432/canchitas_management_pruebas");
                config.setUsername(pgUser);
                config.setPassword(pgPassword);
            }
        } else {
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/canchitas_management_pruebas");
            config.setUsername(pgUser);
            config.setPassword(pgPassword);
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setInitializationFailTimeout(-1);   // no crashear si DB no está lista al inicio
        config.setConnectionTimeout(30000);
        config.setKeepaliveTime(30000);
        config.setMaxLifetime(600000);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }
}
