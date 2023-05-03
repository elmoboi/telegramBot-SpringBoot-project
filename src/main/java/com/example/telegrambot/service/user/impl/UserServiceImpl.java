package com.example.telegrambot.service.user.impl;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.exceptions.ResourceNotFoundException;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void incert(User user) {
        userRepository.save(user);
    }

    @Override
    public Boolean isUserExist(long id) {
        return userRepository.findUserByUserId(id);
    }

    @Override
    public BotState getBotState(long id) {
        return userRepository.findUserStatusById(id);
    }

    @Override
    public void setBotState(BotState botState ,long id) {
        userRepository.setBotStatusToUserById(botState,id);
    }

    @Override
    public void setMessageUser(String message, long id) {
        userRepository.updateUserMessage(message,id);
    }

    @Override
    public List<User> getUsersWaitingStatus(BotState botState) {
        return userRepository.getUserByStatus(botState);
    }

    @Override
    public Integer getAllCount() {
        int count = userRepository.findAll().size();
        return count;
    }

    @Override
    public void setSentStatus(AnswerEnum answerEnum, long id) {
        userRepository.setSentStatusToUserById(answerEnum,id);
    }

    @Override
    public AnswerEnum getSentStatus(long id) {
        return userRepository.findUserSentStatusById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
