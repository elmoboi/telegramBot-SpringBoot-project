package com.example.telegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {

    @Value("${telegram-bot.token}")
    private String tgToken;

    public ResponseEntity<String> sendPhoto(String artHref, String msg, String userId) {
        String url = "https://api.telegram.org/bot" +tgToken+ "/sendPhoto";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("accept", "application/json");
        httpHeaders.set("User-Agent", "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("photo", artHref);
        body.add("caption", msg);
        body.add("disable_notification", "false");
        body.add("chat_id", userId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity,String.class);
    }
}
