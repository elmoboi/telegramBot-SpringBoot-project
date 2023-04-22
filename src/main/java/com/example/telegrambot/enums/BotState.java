package com.example.telegrambot.enums;

public enum BotState {
    REGISTERED_USER,
    WAITING_REQUEST_MIDJOURNEY,
    ASK_QUERY_MIDJOURNEY,
    WAITING_ART,
    GOT_ART_FROM_MIDJOURNEY,
    VALIDATE_ART_FROM_MIDJOURNEY,
    VALIDATED_ART_FROM_MIDJOURNEY,
    PICK_ONE_FAVORITE,
    WAITING_PICK_POSITION_ART,
    PICKED_ONE_FAVORITE,
    WAITING_TO_NOMINATE,
    NOMINATED_ART

}
