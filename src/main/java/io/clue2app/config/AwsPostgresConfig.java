package io.clue2app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.clue2solve.parameters.C2aConfig;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "clue2app.postgres.enabled", havingValue = "true")
public class AwsPostgresConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsPostgresConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.postgres.name}")
    private String postgresConfigName;

    @Bean
    public DataSource dataSource() {
        logger.debug("Configuring Postgres with config name: {}", postgresConfigName);

        final Map<String, String> config = clue2appConfig.getSection(postgresConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("Postgres configuration for '{}' is missing or empty", postgresConfigName);
            throw new IllegalStateException("Postgres configuration is not properly set up");
        }

        final String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", config.get("host"), config.get("port"), config.get("dbName"));
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.get("username"));
        hikariConfig.setPassword(config.get("password"));
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public Flyway flyway() {
        logger.debug("Configuring Postgres with config name: {}", postgresConfigName);

        final Map<String, String> config = clue2appConfig.getSection(postgresConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("Postgres configuration for '{}' is missing or empty", postgresConfigName);
            throw new IllegalStateException("Postgres configuration is not properly set up");
        }

        final String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", config.get("host"), config.get("port"), config.get("dbName"));

        final Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, config.get("username"), config.get("password"))
                .locations("classpath:flyway_migrations")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        logger.debug("✅ Flyway migration completed");

        return flyway;
    }
}