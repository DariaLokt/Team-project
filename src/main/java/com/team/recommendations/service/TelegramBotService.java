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

/**
 * Service to deal with telegram bot
 * On the first encounter gives out greeting and instructions and registers user in the database
 * Upon receiving "/recommend" command registers username and sends personal recommendations
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class TelegramBotService implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final TelegramBot telegramBot;
    private final TelegramBotUsersRepository telegramBotUsersRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final DynamicRecommendationService dynamicRecommendationService;

    private static final String HELLO_MESSAGE_AND_INSTRUCTIONS = "Здравствуйте! Рады приветствовать Вас в нашем сервисе рекомендаций! \nВы можете написать сообщение вида /recommend username, где username - Ваш логин, чтобы получить персональные рекомендации";

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

    /**
     * Method for handling /recommend, checks username, then saves it to the repository and sends recommendations
     * @param chatId идентификатор чата
     * @param messageText сообщение пользователя
     * @see #getUserNameFromMessage(String)
     * @see #checkUserName(Long, String)
     * @see #saveUserNameToTelegramRepository(Long, String)
     * @see #sendRecommendations(Long, String)
     */
    private void handleRecommendCommand(Long chatId, String messageText) {
        logger.info("Method for handling /recommend was invoked");
        String userName = getUserNameFromMessage(messageText);
        if (checkUserName(chatId, userName)) {
            saveUserNameToTelegramRepository(chatId,userName);
            sendRecommendations(chatId,userName);
        }
    }

    /**
     * Method for checking the username
     * @param chatId идентификатор чата
     * @param userName логин пользователя
     * @return возвращает true если логин был написан в сообщении и существует в датабазе
     */
    private boolean checkUserName(Long chatId, String userName) {
        logger.info("Started checking username '{}'", userName);
        if (userName == null || userName.isEmpty()) {
            sendMessage(chatId,"Введите username");
            logger.info("No username in the query");
            return false;
        } else if (!recommendationsRepository.checkUserName(userName)) {
            sendMessage(chatId, "Пользователь не найден");
            logger.info("Username '{}' does not exist in database", userName);
            return false;
        } else {
            logger.info("Username '{}' is identified", userName);
            return true;
        }
    }

    /**
     * Method for sending recommendations to the chat
     * Method gets user's full name from the database, then gets recommendations from DynamicRecommendationService
     * @param chatId идентификатор чата
     * @param userName логин пользователя
     */
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

    /**
     * Method for saving username to the repository
     * @param chatId идентификатор чата
     * @param userName логин пользователя
     */
    private void saveUserNameToTelegramRepository(Long chatId, String userName) {
        TelegramBotUser user = telegramBotUsersRepository.findById(chatId).orElseThrow();
        if (user.getUserName().equals(userName)) {
            logger.info("This username '{}' was already saved for chat_id: {}", userName, chatId);
        } else {
            user.setUserName(userName);
            telegramBotUsersRepository.save(user);
            logger.info("Username '{}' saved for chat_id: {}", userName, chatId);
        }
    }

    /**
     * Method for getting username from message
     * @param messageText сообщение пользователя формата /recommend username
     * @return возвращает только username
     */
    private String getUserNameFromMessage(String messageText) {
        logger.info("Method for getting username from message was invoked");
        String[] parts = messageText.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return null;
        }
        return parts[1].trim();
    }

    /**
     * Method that greets first-time user and saves chat_id to the repository
     * @param chatId идентификатор чата
     */
    private void handleNewUser(Long chatId) {
        sendMessage(chatId, HELLO_MESSAGE_AND_INSTRUCTIONS);
        TelegramBotUser user = new TelegramBotUser();
        user.setChatId(chatId);
        user.setGivenInstructions(true);
        telegramBotUsersRepository.save(user);
        logger.info("New user was added, chat_id: {}",chatId);
    }

    /**
     * Method for sending a message to the chat
     * @param chatId идентификатор чата
     * @param message сообщение пользователя
     */
    private void sendMessage(Long chatId, String message) {
        SendMessage response = new SendMessage(chatId, message);
        telegramBot.execute(response);
        logger.info("Message {} sent to chat: {}", message,chatId);
    }
}

