package com.evangeliakostop.paymentsystem.config.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    /**
     * Payment-System datasource.
     *
     * @return the data source.
     */
    @Bean("paymentsDbTemplate")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource paymentsDatasource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Jdbc Template.
     *
     * @param dataSource the data source
     * @return the jdbc template
     */
    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("paymentsDbTemplate") final DataSource dataSource) {
        return new JdbcTemplate(dataSource, false);
    }
}