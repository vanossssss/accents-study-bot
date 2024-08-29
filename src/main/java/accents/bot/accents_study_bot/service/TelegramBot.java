package accents.bot.accents_study_bot.service;

import accents.bot.accents_study_bot.config.BotConfig;
import accents.bot.accents_study_bot.database.User;
import accents.bot.accents_study_bot.database.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static accents.bot.accents_study_bot.config.TextComment.HELP_COMMAND;
import static accents.bot.accents_study_bot.config.TextComment.START_COMMAND;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();

            if(update.hasMessage()&&update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId);
                        break;
                    case "/test":
                        break;
                    case "/help":
                        helpCommandReceived(chatId);
                        break;
                    default:
                        sendMessage(chatId, "Неизвестная команда, для полного списка команд введите /help");
                        break;
                }
            }
    }

    private void startCommandReceived(long chatId) {
        registerUser(chatId);
        sendMessage(chatId, START_COMMAND);
    }

    private void registerUser(long chatId) {
        if(userRepository.existsById(chatId)) {
            User user = new User();

            user.setUserId(chatId);
            user.setFlagStartTest(false);
            userRepository.save(user);
        }
    }

    private void testCommandReceived(long chatId) {

    }

    private void helpCommandReceived(long chatId) {
        sendMessage(chatId, HELP_COMMAND);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        }
        catch (TelegramApiException e ) {
            System.out.println("Непредвиденная ошибка при отправке сообщения, попробуйте ещё раз");
        }
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
