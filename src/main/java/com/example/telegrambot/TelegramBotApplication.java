package com.example.telegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.example.telegrambot.*")
public class TelegramBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    //TODO: разобраться с доставкой арта юзеру и сохранением его в бд UPDATE: делаем сайт в котором админ будет отправлять арт(4 шт) + выбранный арт в базу
    //TODO: Добавить голосование среди лучших артов(свой не включать) (?) Может сделать связку бд один ко мноким, где пользователю дается возможность выбирать из своих фоток лучшую
    //TODO: Добавить чатГПТ к этому боту


    //TODO: заменить текст на локальные переменные в проверти
    //TODO: Развернуть все на облаке RENDER https://dashboard.render.com/
}
