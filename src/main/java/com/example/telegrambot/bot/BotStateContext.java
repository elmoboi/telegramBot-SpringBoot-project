package com.example.telegrambot.bot;

import com.example.telegrambot.bot.handlers.InputMessageHandler;
import com.example.telegrambot.enums.BotState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        try {
            InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
            log.info("messageHandlers currentMessageHandler - " + currentMessageHandler);
            if(currentMessageHandler != null) {
                return currentMessageHandler.handle(message);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if(isFillingMidjourneyState(currentState)) {
            if(currentState.equals(BotState.WAITING_REQUEST_MIDJOURNEY)) {
                return messageHandlers.get(BotState.WAITING_REQUEST_MIDJOURNEY);
            }
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingMidjourneyState(BotState currentState) {
        switch (currentState) {
            case REGISTERED_USER:
            case WAITING_REQUEST_MIDJOURNEY:
            case ASK_QUERY_MIDJOURNEY:
            case WAITING_ART:
            case VALIDATE_ART_FROM_MIDJOURNEY:
                return true;
            default:
                return false;

        }
    }
}
