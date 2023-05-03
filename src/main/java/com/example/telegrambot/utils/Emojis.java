package com.example.telegrambot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emojis {
    ROBOT(EmojiParser.parseToUnicode(":robot_face:")),
    HOUR_GLASS(EmojiParser.parseToUnicode(":hourglass:")),
    CALENDAR(EmojiParser.parseToUnicode(":spiral_calendar_pad:")),
    EXCLAMATION(EmojiParser.parseToUnicode(":exclamation:")),
    MENU(EmojiParser.parseToUnicode(":control_knobs:")),
    ART(EmojiParser.parseToUnicode(":sunrise:")),
    SMS(EmojiParser.parseToUnicode(":speech_balloon:")),
    LOCK(EmojiParser.parseToUnicode(":lock:")),
    RIGTH_FINGER(EmojiParser.parseToUnicode(":point_right:")),
    UP_FINGER(EmojiParser.parseToUnicode(":point_up:")),
    KEYBOARD(EmojiParser.parseToUnicode(":keyboard:")),
    WINK(EmojiParser.parseToUnicode(":wink:")),
    FEARFUL(EmojiParser.parseToUnicode(":fearful:")),
    PENSIVE(EmojiParser.parseToUnicode(":pensive:")),
    ZZZ(EmojiParser.parseToUnicode(":zzz:")),
    CLOCK(EmojiParser.parseToUnicode(":clock4:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
