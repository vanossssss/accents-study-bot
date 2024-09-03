package accents.bot.accents_study_bot.service;

import accents.bot.accents_study_bot.config.BotConfig;
import accents.bot.accents_study_bot.database.User;
import accents.bot.accents_study_bot.database.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

import static accents.bot.accents_study_bot.config.TextComment.*;

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
            if(update.hasMessage()&&update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId);
                        break;
                    case "/test":
                        testCommandReceived(chatId);
                        break;
                    case "/help":
                        helpCommandReceived(chatId);
                        break;
                    default:
                        sendMessage(chatId, "Неизвестная команда, для полного списка команд введите /help");
                        break;
                }
            } else if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callbackData.equals("WRONG_BUTTON")) {
                    String text = "Не верно!";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int)messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        System.out.println("Error occurred");
                    }
                }

                else if (callbackData.equals("RIGHT_BUTTON")) {
                    String text = "Верно!";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int)messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        System.out.println("Error occurred");
                    }
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
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(TEST_QUESTION);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var wrongButton = new InlineKeyboardButton();

        wrongButton.setText("wrong");
        wrongButton.setCallbackData("WRONG_BUTTON");

        var rightButton = new InlineKeyboardButton();

        rightButton.setText("right");
        rightButton.setCallbackData("RIGHT_BUTTON");

        rowInline.add(wrongButton);
        rowInline.add(rightButton);

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    private void helpCommandReceived(long chatId) {
        sendMessage(chatId, HELP_COMMAND);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("/help");
        row.add("/test");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try{
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Error occurred");
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
