package com.example.telegrambot.repository;

import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("userRepository")
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Transactional
    @Query("select u.description from User u where u.name = ?1")
    List<String> getMessagesByName(String name);

    @Transactional
    @Query("select u.status from User u where u.user_id = ?1")
    BotState findUserStatusById(long id);

    @Modifying
    @Transactional
    @Query("update User u set u.status = ?1 where u.user_id = ?2")
    void setBotStatusToUserById(BotState botState ,long id);

    @Modifying
    @Transactional
    @Query("update User u set u.description = ?1 where u.user_id = ?2")
    void updateUserMessage(String msg, long id);

    @Transactional
    @Query("select count(u) = 1 from User u where u.user_id = ?1")
    boolean findUserByUserId(int id);
}
