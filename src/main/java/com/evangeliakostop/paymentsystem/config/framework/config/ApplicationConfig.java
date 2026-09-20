package com.evangeliakostop.paymentsystem.config.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {

    private final String databaseUrl;
    private final String databaseUsername;
    private final String databasePassword;

    private final String stripeSecretKey;
    private final String stripeInitUrl;
    private final String stripeConfirmUrl;

    private final String fraudApiUrl;
    private final String fraudApiSecretKey;



    public ApplicationConfig(String databaseUrl,
                             String databaseUsername,
                             String databasePassword,
                             String stripeSecretKey,
                             String stripeInitUrl,
                             String stripeConfirmUrl,
                             String fraudApiUrl,
                             String fraudApiSecretKey) {

        this.databaseUrl = databaseUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.stripeSecretKey = stripeSecretKey;
        this.stripeInitUrl = stripeInitUrl;
        this.stripeConfirmUrl = stripeConfirmUrl;
        this.fraudApiUrl = fraudApiUrl;
        this.fraudApiSecretKey = fraudApiSecretKey;

    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getStripeSecretKey() {
        return stripeSecretKey;
    }

    public String getStripeInitUrl() {
        return stripeInitUrl;
    }

    public String getStripeConfirmUrl() {
        return stripeConfirmUrl;
    }

    public String getFraudApiUrl() {
        return fraudApiUrl;
    }

    public String getFraudApiSecretKey() {
        return fraudApiSecretKey;
    }

    public static ApplicationConfig load() throws IOException {
        Properties properties = new Properties();

        try (InputStream input = ApplicationConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IllegalStateException("application.properties not found");
            }

            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load application.properties", e);
        }

        return new ApplicationConfig(
                properties.getProperty("spring.datasource.jdbcUrl"),
                properties.getProperty("spring.datasource.username"),
                properties.getProperty("spring.datasource.password"),
                properties.getProperty("stripe.secret.key"),
                properties.getProperty("stripe.init.url"),
                properties.getProperty("stripe.confirm.url"),
                properties.getProperty("fraud.api.url"),
                properties.getProperty("fraud.api.secret.key")
        );
    }
}
