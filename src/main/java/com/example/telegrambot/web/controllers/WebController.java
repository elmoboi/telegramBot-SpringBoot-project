package com.example.telegrambot.web.controllers;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.user.UserService;
import com.example.telegrambot.utils.Emojis;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping()
public class WebController {
    //TODO: добавить сортировку на таболицу, чтобы наверху всегда были старые
    @Autowired
    private UserService userService;

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
    public String sendArtToUserChat(@PathVariable String userId, @RequestParam(value = "tgToken") String tgToken, @RequestParam("artHref") String artHref) throws IOException, ExecutionException, InterruptedException {
        var asyncHttpClient = new DefaultAsyncHttpClient();
        String url = "https://api.telegram.org/bot" +tgToken+ "/sendPhoto";

        String msg = Emojis.ROBOT + " Вот, что удалось сгенерировать ИИ Midjourney по вашему запросу.";

        Request request = new RequestBuilder().setUrl(url)
                .setHeader("accept", "application/json")
                .setHeader("User-Agent", "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)")
                .setHeader("content-type", "application/json")
                .setBody("{\"photo\":\"" +artHref+ "\",\"caption\":\"" +msg+ "\",\"disable_notification\":false,\"chat_id\":\"" +userId+ "\"}")
                .build();

        CompletableFuture<Response> future = asyncHttpClient.executeRequest(request).toCompletableFuture();
        Response response = future.get();
        if(response.getStatusText().equals("OK") && userService.getSentStatus(Long.parseLong(userId)).equals(AnswerEnum.NO)) {
            userService.setSentStatus(AnswerEnum.YES, Long.parseLong(userId));
            userService.setBotState(BotState.VALIDATED_ART_FROM_MIDJOURNEY, Long.parseLong(userId));
        }

        asyncHttpClient.close();
        return response.getResponseBody();
    }
}
