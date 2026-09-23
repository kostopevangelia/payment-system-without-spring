package com.evangeliakostop.paymentsystem.config.jdbc;

import com.evangeliakostop.paymentsystem.config.framework.config.ApplicationConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JdbcConfig {

    public static HikariDataSource createDataSource(ApplicationConfig config) {

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(config.getDatabaseUrl());
        dataSource.setUsername(config.getDatabaseUsername());
        dataSource.setPassword(config.getDatabasePassword());

        return dataSource;
    }
}