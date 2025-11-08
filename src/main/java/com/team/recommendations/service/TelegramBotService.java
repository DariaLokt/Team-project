package com.team.recommendations.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.telegram.TelegramBotUser;
import com.team.recommendations.repository.RecommendationsRepository;
import com.team.recommendations.repository.TelegramBotUsersRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class TelegramBotService implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private TelegramBot telegramBot;
    private TelegramBotUsersRepository telegramBotUsersRepository;
    private RecommendationsRepository recommendationsRepository;
    private DynamicRecommendationService dynamicRecommendationService;

    public TelegramBotService(TelegramBot telegramBot, TelegramBotUsersRepository telegramBotUsersRepository, RecommendationsRepository recommendationsRepository, DynamicRecommendationService dynamicRecommendationService) {
        this.telegramBot = telegramBot;
        this.telegramBotUsersRepository = telegramBotUsersRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.dynamicRecommendationService = dynamicRecommendationService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (update.message() != null && update.message().text() != null) {
                String messageText = update.message().text();
                Long chatId = update.message().chat().id();
                if (!telegramBotUsersRepository.existsById(chatId)) {
                    handleNewUser(chatId);
                }
                if (messageText.startsWith("/recommend")) {
                    handleRecommendCommand(chatId, messageText);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleRecommendCommand(Long chatId, String messageText) {
        logger.info("Method for handling /recommend was invoked");
        String userName = getUserNameFromMessage(messageText);
        if (userName == null || userName.isEmpty()) {
            sendMessage(chatId,"Введите user name");
            return;
        }
        saveUserNameToTelegramRepository(chatId,userName);
    }

    private void sendRecommendations(Long chatId, String userName) {
        Optional<String> fullNameOpt = recommendationsRepository.getFullNameByUserName(userName);
        if (fullNameOpt.isPresent()) {
            sendMessage(chatId, "Здравствуйте " + fullNameOpt.get());
        } else {
            sendMessage(chatId,"Вашей информации нет в базе");
            return;
        }
        Collection<Recommendation> recommendations = dynamicRecommendationService.getRecommendations(recommendationsRepository.getIdByUserName(userName));
        if (!recommendations.isEmpty()) {
            recommendations
                    .forEach(r -> sendMessage(chatId, "Вам рекомендован продукт: " + r.getName() + "\n Его описание: " + r.getText()));
        } else {
            sendMessage(chatId,"Вам пока ничего не подходит");
        }
    }

    private void saveUserNameToTelegramRepository(Long chatId, String userName) {
        if (!recommendationsRepository.checkUserName(userName)) {
            sendMessage(chatId,"Пользователь не найден");
            return;
        }
        TelegramBotUser user = telegramBotUsersRepository.findById(chatId).orElseThrow();
        user.setUserName(userName);
        telegramBotUsersRepository.save(user);
        logger.info("Username '{}' saved for chat_id: {}", userName, chatId);
        sendRecommendations(chatId,userName);
    }

    private String getUserNameFromMessage(String messageText) {
        logger.info("Method for getting user name from message was invoked");
        String[] parts = messageText.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return null;
        }
        return parts[1].trim();
    }

    private void handleNewUser(Long chatId) {
        sendMessage(chatId, "бот приветствует пользователя и печатает справку");
        TelegramBotUser user = new TelegramBotUser();
        user.setChatId(chatId);
        user.setGivenInstructions(true);
        telegramBotUsersRepository.save(user);
        logger.info("New user was added, chat_id: {}",chatId);
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage response = new SendMessage(chatId, message);
        telegramBot.execute(response);
        logger.info("Message {} sent to chat: {}", message,chatId);
    }
}

