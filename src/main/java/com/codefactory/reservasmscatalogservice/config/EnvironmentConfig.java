package com.codefactory.reservasmscatalogservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvironmentConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    private static final String PASSWORD_PATTERN = "password=[^&]*";
    private static final String PASSWORD_MASK = "password=***";

    private final Environment env;

    @Value("${spring.datasource.url:not-set}")
    private String dbUrl;

    @Value("${spring.datasource.username:not-set}")
    private String dbUsername;

    public EnvironmentConfig(Environment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("========================================");
        logger.info("MS-Catalog Environment Configuration Validation");
        logger.info("========================================");
        
        String[] activeProfiles = env.getActiveProfiles();
        String profiles = activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default";
        logger.info("Active Profile(s): {}", profiles);
        
        logger.info("Database URL: {}", maskSensitiveInfo(dbUrl));
        logger.info("Database Username: {}", dbUsername);
        
        String jwtSecret = env.getProperty("jwt.secret");
        boolean jwtConfigured = jwtSecret != null && !jwtSecret.contains("default");
        logger.info("JWT Configuration: {}", jwtConfigured ? "CONFIGURED" : "USING DEFAULT");
        
        logger.info("========================================");
    }

    private String maskSensitiveInfo(String url) {
        if (url == null) return url;
        return url.replaceAll(PASSWORD_PATTERN, PASSWORD_MASK);
    }
}
