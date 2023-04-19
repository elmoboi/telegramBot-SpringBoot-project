package com.example.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.example.telegrambot.*")
public class TelegramBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    //TODO: разобраться с доставкой арта юзеру и сохранением его в бд
    //TODO: Добавить голосование среди лучших артов(свой не включать) (?) Может сделать связку бд один ко мноким, где пользователю дается возможность выбирать из своих фоток лучшую
    //TODO: Добавить чатГПТ к этому боту
    //TODO: (?) Сделать из этого бота микросервис. Далее написать другой веб микровервис, который будет общаться с ботом и закидывать фотку с веба в бд.


    //TODO: заменить текст на локальные переменные в проверти
    //TODO: Развернуть все на облаке RENDER https://dashboard.render.com/
}
