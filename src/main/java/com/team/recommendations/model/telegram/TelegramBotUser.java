package com.team.recommendations.model.telegram;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "telegram_bot_users")
public class TelegramBotUser {
    @Id
    private Long chatId;

    private Boolean givenInstructions;
    private String userName;

    public TelegramBotUser() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Boolean getGivenInstructions() {
        return givenInstructions;
    }

    public void setGivenInstructions(Boolean givenInstructions) {
        this.givenInstructions = givenInstructions;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TelegramBotUser that = (TelegramBotUser) o;
        return Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chatId);
    }

    @Override
    public String toString() {
        return "TelegramBotUsers{" +
                "chatId=" + chatId +
                ", givenInstructions=" + givenInstructions +
                ", userName='" + userName + '\'' +
                '}';
    }
}
