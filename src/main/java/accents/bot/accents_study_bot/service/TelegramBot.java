package accents.bot.accents_study_bot.service;

import accents.bot.accents_study_bot.config.BotConfig;
import accents.bot.accents_study_bot.database.Accent;
import accents.bot.accents_study_bot.database.AccentRepository;
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

import java.util.ArrayList;
import java.util.List;

import static accents.bot.accents_study_bot.config.TextComment.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccentRepository accentRepository;
    final BotConfig config;
    private int score = 0;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
            if(update.hasMessage()&&update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                User user = userRepository.findByUserId(chatId);

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                        break;
                    case "/help":
                        helpCommandReceived(chatId);
                        break;
                    case "/test":
                        if(user.isFlagStartTest()) {
                            sendMessage(chatId, DURING_TEST);
                        } else {
                            testCommandReceived(chatId);
                        }
                        break;
                    case "/cancel":
                        cancelCommandReceived(chatId);
                        break;
                    case "/score":
                        scoreCommandReceived(chatId);
                        break;
                    default:
                        sendMessage(chatId, UNKNOWN_COMMAND);
                        break;
                }
            } else if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                User user = userRepository.findByUserId(chatId);

                if (user.isFlagStartTest()) {
                    if (callbackData.equals("WRONG_BUTTON")) {
                        user.setFlagStartTest(false);
                        user.setScore(score);
                        userRepository.save(user);

                        String text = "Не верно! Ваш результат: " + String.valueOf(user.getScore());
                        EditMessageText message = new EditMessageText();
                        message.setChatId(chatId);
                        message.setText(text);
                        message.setMessageId((int) messageId);
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            System.out.println("Error occurred");
                        }
                    } else if (callbackData.equals("RIGHT_BUTTON")) {
                        String text = "Верно! Следующий вопрос...";
                        score = score + 1;
                        EditMessageText message = new EditMessageText();
                        message.setChatId(chatId);
                        message.setText(text);
                        message.setMessageId((int) messageId);
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            System.out.println("Error occurred");
                        }
                        testCommandReceived(chatId);
                    }
                } else {
                    String text = "Вы не начинали новое тестирование!";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId);
                    message.setText(text);
                    message.setMessageId((int) messageId);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        System.out.println("Error occurred");
                    }
                }
            }
    }

    private void startCommandReceived(long chatId, String name) {
        registerUser(chatId, name);
        sendMessage(chatId, START_COMMAND);
    }

    private void helpCommandReceived(long chatId) {
        sendMessage(chatId, HELP_COMMAND);
    }

    private void registerUser(long chatId, String name) {
        if(!userRepository.existsById(chatId)) {
            User user = new User();
            user.setUserId(chatId);
            user.setName(name);
            user.setFlagStartTest(false);
            user.setScore(0);
            userRepository.save(user);
        }
    }

    private void testCommandReceived(long chatId) {
        User user = userRepository.findByUserId(chatId);

        user.setFlagStartTest(true);
        userRepository.save(user);

        Accent testAccent;
        testAccent = accentRepository.findByRandom();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(TEST_QUESTION + testAccent.getRight().toLowerCase() + "?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var wrongButton = new InlineKeyboardButton();

        wrongButton.setText(testAccent.getWrong());
        wrongButton.setCallbackData("WRONG_BUTTON");

        var rightButton = new InlineKeyboardButton();

        rightButton.setText(testAccent.getRight());
        rightButton.setCallbackData("RIGHT_BUTTON");

        if (Math.round(Math.random()) == 0) {
            rowInline.add(wrongButton);
            rowInline.add(rightButton);
        } else {
            rowInline.add(rightButton);
            rowInline.add(wrongButton);
        }

        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);

    }

    private void cancelCommandReceived(long chatId) {
        User user = userRepository.findByUserId(chatId);
        user.setFlagStartTest(false);
        userRepository.save(user);
        sendMessage(chatId, CANCEL_SUCCESS);
    }

    private void scoreCommandReceived(long chatId) {
        ArrayList<User> scoreBoard = userRepository.findByScore();
        String scoreBoardText = " Лучшие результаты: \n\n";
        for(int i = 0; i < scoreBoard.size(); i++) {
            scoreBoardText += String.valueOf(i + 1) + ". "+ scoreBoard.get(i).getName() + " - " + scoreBoard.get(i).getScore() + "\n";
        }
        sendMessage(chatId, scoreBoardText);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add("/start");
        row.add("/help");
        row.add("/test");
        row.add("/score");
        row.add("/cancel");

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
