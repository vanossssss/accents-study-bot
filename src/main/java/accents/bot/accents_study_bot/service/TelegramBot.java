package accents.bot.accents_study_bot.service;

import accents.bot.accents_study_bot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if(update.hasMessage()&&update.getMessage().hasText()) {
                Message inputMessage = update.getMessage();
                SendMessage outputMessage = new SendMessage();
                outputMessage.setChatId(inputMessage.getChatId());
                outputMessage.setText(inputMessage.getText());
                execute(outputMessage);
            }
        } catch (TelegramApiException e) {}
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotKey();
    }
}
