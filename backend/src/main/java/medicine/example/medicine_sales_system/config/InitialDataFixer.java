package medicine.example.medicine_sales_system.config;

import medicine.example.medicine_sales_system.entity.User;
import medicine.example.medicine_sales_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Component
public class InitialDataFixer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InitialDataFixer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataFixer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Users created by V3__init_data.sql: admin, seller1
        // Replace placeholder hashes with bcrypt hashes for known users if present
        List<String> adminLike = Arrays.asList("REPLACE_WITH_BCRYPT_HASH", "x", "", null);
        fixIfPlaceholder("admin", "admin123", adminLike);
        fixIfPlaceholder("seller1", "seller123", adminLike);
    }

    private void fixIfPlaceholder(String username, String rawPassword, List<String> placeholders) {
        try {
            userRepository.findByUsername(username).ifPresent(u -> {
                String ph = u.getPasswordHash();
                if (placeholders.contains(ph)) {
                    String encoded = passwordEncoder.encode(rawPassword);
                    u.setPasswordHash(encoded);
                    userRepository.save(u);
                    log.info("Replaced placeholder password for user '{}' with encoded default password.", username);
                    log.warn("Default password for '{}' set to '{}'. Please change it after first login.", username, rawPassword);
                }
            });
        } catch (org.springframework.dao.DataAccessException ex) {
            // Likely the table or schema isn't ready (migrations disabled). Log and continue.
            log.warn("Could not inspect or update user '{}': {}. Skipping initial password fix.", username, ex.getMessage());
        }
    }
}
