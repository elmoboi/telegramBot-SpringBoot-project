package com.example.telegrambot.service.user;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;

import javax.sql.rowset.serial.SerialBlob;
import java.util.List;

public interface UserService {
    void incert(User user);
    Boolean isUserExist(long id);
    BotState getBotState(long id);
    void setBotState(BotState botState , long id);
    void setMessageUser(String message, long id);
    public List<User> getUsersWaitingStatus(BotState botState);
    public Integer getAllCount();
    void setSentStatus(AnswerEnum answerEnum, long id);
    AnswerEnum getSentStatus(long id);
    List<User> getAll();

}
