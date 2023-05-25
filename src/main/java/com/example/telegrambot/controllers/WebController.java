package com.example.telegrambot.controllers;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.TelegramService;
import com.example.telegrambot.service.user.UserService;
import com.example.telegrambot.enums.Emojis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramService telegramService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("user", userService.getUsersWaitingStatus(BotState.WAITING_ART));
        return "index";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<User> data() {
        return userService.getUsersWaitingStatus(BotState.WAITING_ART);
    }

    @GetMapping("/sendArt/{userId}")
    @ResponseBody
    public String sendArtToUserChat(@PathVariable String userId, @RequestParam("artHref") String artHref) {
        String msg = Emojis.ROBOT + " Вот, что удалось сгенерировать ИИ Midjourney по вашему запросу.";

        ResponseEntity<String> response = telegramService.sendPhoto(artHref,msg,userId);

        if(response.getStatusCode().toString().equals("200 OK") && userService.getSentStatus(Long.parseLong(userId)).equals(AnswerEnum.NO)) {
            userService.setSentStatus(AnswerEnum.YES, Long.parseLong(userId));
            userService.setBotState(BotState.VALIDATED_ART_FROM_MIDJOURNEY, Long.parseLong(userId));
        }

        return response.getBody();
    }
}
