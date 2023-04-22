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
            new SendMessage(String.valueOf(message.getChatId()),"1 К сожалению, бот не умеет общаться без " +
                    "активироанного чата с ChatGTP(для того, чтобы активировать этот чат и поговорить с ИИ, нажмите на соответствующую кнопку в меню)");
            e.printStackTrace();
        }
        return null;
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if(isFillingMidjourneyState(currentState)) {
            if(currentState.equals(BotState.WAITING_REQUEST_MIDJOURNEY)) {
                return messageHandlers.get(BotState.WAITING_REQUEST_MIDJOURNEY);
            } else if(currentState.equals(BotState.GOT_ART_FROM_MIDJOURNEY)) {
                return messageHandlers.get(BotState.WAITING_ART);
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
            case GOT_ART_FROM_MIDJOURNEY:
            case VALIDATE_ART_FROM_MIDJOURNEY:
            case PICK_ONE_FAVORITE:
            case PICKED_ONE_FAVORITE:
            case WAITING_TO_NOMINATE:
            case NOMINATED_ART:
                return true;
            default:
                return false;

        }
    }
}
