package com.macro.tgbot.service;

import com.macro.tgbot.dto.WeatherDetails;
import com.macro.tgbot.util.WeatherUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.text.MessageFormat;
import java.util.Objects;

@Service
@Slf4j
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.name}")
    private String botName;

    private final ApiService apiService;
    private final WeatherUtil weatherUtil;

    public BotService(ApiService apiService, WeatherUtil weatherUtil) {
        this.apiService = apiService;
        this.weatherUtil = weatherUtil;
        log.info("bot started....");
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equalsIgnoreCase("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(update.getMessage().getChatId().toString());
                message.setText("Welcome to weather bot \uD83C\uDF27. type the plcae to get weather details");
                sendMessageToChat(message);
            } else {
                String place = update.getMessage().getText();
                WeatherDetails weatherDetails = apiService.getWeatherDetails(place);
                if (Objects.nonNull(weatherDetails)) {
                    String aqiDescription = weatherUtil.getAqiDescription(weatherDetails.getAqi());
                    SendMessage sendMessage = new SendMessage();
                    String message = MessageFormat.format("place: {0} \nState: {1} \nCountry: {2} \nWeather: {3}\nAqi: {4} - {5} ", weatherDetails.getPlace(), weatherDetails.getState()
                            , weatherDetails.getCountry(), weatherDetails.getWeather(), weatherDetails.getAqi(), aqiDescription);
                    sendMessage.setChatId(update.getMessage().getChatId().toString());
                    sendMessage.setText(message);
                    sendMessageToChat(sendMessage);
                }else{
                    SendMessage sendMessage = new SendMessage();
                    String message = "sry couldn't get the weather details \ud83d\ude22, please check whether the place entered is a valid one!! ";
                    sendMessage.setChatId(update.getMessage().getChatId().toString());
                    sendMessage.setText(message);
                    sendMessageToChat(sendMessage);
                }
            }

        }

    }

    private void sendMessageToChat(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
