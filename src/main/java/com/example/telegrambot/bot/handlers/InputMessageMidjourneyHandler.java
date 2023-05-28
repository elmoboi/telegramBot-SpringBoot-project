package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageMidjourneyHandler {
    SendMessage handle(Message message);
    BotState getHandlerName();
}
