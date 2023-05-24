package com.example.telegrambot.bot.menu;

import com.example.telegrambot.enums.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotMenu {
    public static SendMessage sendInlineKeyBoardMessage(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Программа");
        inlineKeyboardButton4.setText("Сгенерировать арт у Midjourney " + Emojis.ART);
        inlineKeyboardButton5.setText("Начать чат с ChatGPT " + Emojis.SMS);
        inlineKeyboardButton6.setText("Как зарегистрировать Midjourney|ChatGPT?");

        inlineKeyboardButton1.setCallbackData("program");
        inlineKeyboardButton4.setCallbackData("midjourney");
        inlineKeyboardButton5.setCallbackData("chatGPT");
        inlineKeyboardButton6.setCallbackData("registerBots");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>(); //Программа
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>(); //Сгенерировать арт
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>(); //Начать чат с ГПТ
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>(); //Как зарегистрировать чатгпт и миджорни

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton5);
        keyboardButtonsRow4.add(inlineKeyboardButton6);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Вот мое меню" + Emojis.MENU);
        sm.setReplyMarkup(inlineKeyboardMarkup);
        return sm;
    }
}
