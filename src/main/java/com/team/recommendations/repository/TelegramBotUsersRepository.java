package com.team.recommendations.repository;

import com.team.recommendations.model.telegram.TelegramBotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramBotUsersRepository extends JpaRepository<TelegramBotUser, Long> {
}
