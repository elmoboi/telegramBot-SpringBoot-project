package com.example.telegrambot.bot.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class ArtMenu {
    public static SendMessage sendInlineKeyBoardMessage(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Зарегистрировать арт для конкурса");
        inlineKeyboardButton2.setText("Попробовать еще раз");
        inlineKeyboardButton3.setText("Выбрать 1 вариант");
        inlineKeyboardButton4.setText("Выбрать 2 вариант");
        inlineKeyboardButton5.setText("Выбрать 3 вариант");
        inlineKeyboardButton6.setText("Выбрать 4 вариант");

        inlineKeyboardButton1.setCallbackData("acceptArt");
        inlineKeyboardButton2.setCallbackData("tryAnother");
        inlineKeyboardButton3.setCallbackData("pickFirst");
        inlineKeyboardButton4.setCallbackData("pickSecond");
        inlineKeyboardButton5.setCallbackData("pickThird");
        inlineKeyboardButton6.setCallbackData("pickFourth");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton5);
        keyboardButtonsRow3.add(inlineKeyboardButton6);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText("Вот мое меню");
        sm.setReplyMarkup(inlineKeyboardMarkup);
        return sm;
    }
}
