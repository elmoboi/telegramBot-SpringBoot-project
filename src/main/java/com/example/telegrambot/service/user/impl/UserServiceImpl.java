package com.example.telegrambot.service.user.impl;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.exceptions.ResourceNotFoundException;
import com.example.telegrambot.repository.UserRepository;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public List<String> getMessageList(User user) {
        String userName = user.getName();
        return userRepository.getMessagesByName(userName);
    }

    @Override
    public void incert(User user) {
        userRepository.save(user);
    }

    @Override
    public Boolean isUserExist(int id) {
        return userRepository.findUserByUserId(id);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public BotState getBotState(int id) {
        return userRepository.findUserStatusById(id);
    }

    @Override
    public void setBotState(BotState botState ,int id) {
        userRepository.setBotStatusToUserById(botState,id);
    }

    @Override
    public void setMessageUser(String message, int id) {
        userRepository.updateUserMessage(message,id);
    }

    @Override
    public List<SerialBlob> getAllImages(int id) {
        //TODO: прописать репу доставания всех фоток из базы данных
        return null;
    }

    @Override
    public void setContestAnswer(AnswerEnum answer, int id) {
        userRepository.setUserContestAnswer(answer,id);
    }
}
