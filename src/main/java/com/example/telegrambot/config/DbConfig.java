package com.example.telegrambot.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DbConfig {
    @Bean
    @ConfigurationProperties(prefix = "app.db.bot-db")
    SpringDataJdbcProperties jdbcProperties() {
        return new SpringDataJdbcProperties();
    }
    @Bean
    public DataSource dataSource(SpringDataJdbcProperties properties) {
        log.info("настройки БД: [{}]", properties.toString());

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setDriverClassName(properties.getDriver());
        ds.setUsername(properties.getUser());
        ds.setPassword(properties.getPassword());
        ds.setMaximumPoolSize(Integer.parseInt(properties.getPoolSize()));
        return ds;
    }
    @Data
    @NoArgsConstructor
    public static class SpringDataJdbcProperties {
        String url;
        String driver;
        String user;
        String password;
        String poolSize;

        public SpringDataJdbcProperties(
                String url, String driver, String user, String password, String poolSize) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
        }
        @Override
        public String toString() {
            var props = new SpringDataJdbcProperties(
                    url, driver, user, ((password == null) || password.isEmpty()) ? "" : "*****", poolSize);
            return Json.encode(props);
        }
    }
}
