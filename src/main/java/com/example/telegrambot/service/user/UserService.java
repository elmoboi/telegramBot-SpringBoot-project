package com.example.telegrambot.service.user;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;

import java.util.List;

public interface UserService {
    void incert(User user);
    Boolean isUserExist(long id);
    BotState getBotState(long id);
    GptState getGptState(long id);
    void setBotState(BotState botState , long id);
    void setGptState(GptState gptState , long id);
    void setMessageUser(String message, long id);
    public List<User> getUsersWaitingStatus(BotState botState);
    public Integer getAllCount(); //for paging
    void setSentStatus(AnswerEnum answerEnum, long id);
    AnswerEnum getSentStatus(long id);
    User findUserByUserId(long id);
}
