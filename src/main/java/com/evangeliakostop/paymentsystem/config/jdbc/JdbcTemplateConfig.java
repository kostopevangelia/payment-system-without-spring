package com.evangeliakostop.paymentsystem.config.jdbc;


import com.evangeliakostop.paymentsystem.config.framework.config.ApplicationConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateConfig {

    public static JdbcTemplate createJdbcTemplate(ApplicationConfig config) {

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(config.getDatabaseUrl());
        dataSource.setUsername(config.getDatabaseUsername());
        dataSource.setPassword(config.getDatabasePassword());

        return new JdbcTemplate(dataSource);
    }
}