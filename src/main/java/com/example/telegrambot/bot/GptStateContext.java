package com.example.telegrambot.bot;

import com.example.telegrambot.bot.handlers.InputMessageGPTHandler;
import com.example.telegrambot.enums.GptState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GptStateContext {
    private Map<GptState, InputMessageGPTHandler> messageGPTHandlers = new HashMap<>();

    public GptStateContext(List<InputMessageGPTHandler> messageGPTHandlers) {
        messageGPTHandlers.forEach(handler -> this.messageGPTHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessageToGPT(GptState gptState, Message message) throws Exception {
        try {
            InputMessageGPTHandler currentMessageGPTHandler = findMessageHandlerToGPT(gptState);
            log.info("messageGPTHandlers currentMessageGPTHandlers - " + currentMessageGPTHandler);
            if(currentMessageGPTHandler != null) {
                return currentMessageGPTHandler.handle(message);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputMessageGPTHandler findMessageHandlerToGPT(GptState currentGPTState) {
        if (isFillingGPTState(currentGPTState)) {
            if (currentGPTState.equals(GptState.ACTIVE)) {
                return messageGPTHandlers.get(GptState.ACTIVE);
            } else if(currentGPTState.equals(GptState.COMMUNICATING_WITH_GPT)) {
                return messageGPTHandlers.get(GptState.COMMUNICATING_WITH_GPT);
            }
        }

        return messageGPTHandlers.get(currentGPTState);
    }

    private boolean isFillingGPTState(GptState currentState) {
        switch (currentState) {
            case ACTIVE:
            case COMMUNICATING_WITH_GPT:
            case DISABLED:
                return true;
            default:
                return false;

        }
    }
}
