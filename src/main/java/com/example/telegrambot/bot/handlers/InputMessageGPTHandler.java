package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.GptState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageGPTHandler {
    SendMessage handle(Message message) throws Exception;
    GptState getHandlerName();
}
