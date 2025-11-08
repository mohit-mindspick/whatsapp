package com.assetneuron.whatsapp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ConnectionPoolConfig {

    @Value("${spring.datasource.write.url}")
    private String writeUrl;

    @Value("${spring.datasource.write.username}")
    private String writeUsername;

    @Value("${spring.datasource.write.password}")
    private String writePassword;

    @Value("${spring.datasource.read.url}")
    private String readUrl;

    @Value("${spring.datasource.read.username}")
    private String readUsername;

    @Value("${spring.datasource.read.password}")
    private String readPassword;

    @Bean(name = "writeDataSource")
    public DataSource writeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(writeUrl);
        config.setUsername(writeUsername);
        config.setPassword(writePassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Write database connection pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setPoolName("WhatsAppWritePool");
        
        return new HikariDataSource(config);
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(readUrl);
        config.setUsername(readUsername);
        config.setPassword(readPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Read database connection pool settings
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(3);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setPoolName("WhatsAppReadPool");
        
        return new HikariDataSource(config);
    }
}

