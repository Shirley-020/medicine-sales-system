package medicine.example.medicine_sales_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(org.springframework.jdbc.core.JdbcTemplate.class)
public class DatabaseStartupValidator implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DatabaseStartupValidator.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseStartupValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (one != null && one == 1) {
                log.info("Database connectivity check passed.");
            } else {
                log.warn("Database connectivity check returned unexpected value: {}", one);
            }
        } catch (Exception e) {
            log.error("Failed to connect to the database: {}", e.getMessage());
            // rethrowing will stop the application from starting and surface DB problems early
            throw new IllegalStateException("Cannot connect to the configured datasource", e);
        }
    }
}
