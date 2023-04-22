package com.example.telegrambot.service.user;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.exceptions.ResourceNotFoundException;

import javax.sql.rowset.serial.SerialBlob;
import java.util.List;

public interface UserService {
    User getById(int id) throws ResourceNotFoundException;
    List<String> getMessageList(User user);
    void incert(User user);
    Boolean isUserExist(int id);
    void delete(User user);
    BotState getBotState(int id);
    void setBotState(BotState botState , int id);
    void setMessageUser(String message, int id);
    List<SerialBlob> getAllImages(int id);
    void setContestAnswer(AnswerEnum answer, int id);
}
