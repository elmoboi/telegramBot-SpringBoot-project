package com.example.telegrambot.repository;

import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "conversationHistory")
    List<User> findUserByStatus(BotState botState);

    @Transactional
    @Query("select u.status from User u where u.userId = ?1")
    BotState findUserStatusById(long id);

    @Transactional
    @Query("select u.GPTstatus from User u where u.userId = ?1")
    GptState findUserGptStatusById(long id);

    @Transactional
    @Modifying
    @Query("update User u set u.status = ?1 where u.userId = ?2")
    void setBotStatusToUserById(BotState botState ,long id);

    @Modifying
    @Transactional
    @Query("update User u set u.GPTstatus = ?1 where u.userId = ?2")
    void setGptStatusToUserById(GptState gptState , long id);

    @Modifying
    @Transactional
    @Query("update User u set u.description = ?1 where u.userId = ?2")
    void updateUserMessage(String msg, long id);

    @Transactional
    @Query("select count(u) = 1 from User u where u.userId = ?1")
    boolean isUserByUserIdExist(long id);

    @Modifying
    @Transactional
    @Query("update User u set u.isAlreadySent = ?1 where u.userId = ?2")
    void setSentStatusToUserById(AnswerEnum answerEnum ,long id);

    @Transactional
    @Query("select u.isAlreadySent from User u where u.userId = ?1")
    AnswerEnum findUserSentStatusById(long id);

    User findUserByUserId(long userId);
}
