package accents.bot.accents_study_bot.config;

public class TextComment {
    public static final String HELP_COMMAND =
            "Список команд:\n\n" +
                    "/start - Перезапустить бота\n" +
                    "/test - тестирование на проверку ударений\n" +
                    "/cancel - отмена тестирования\n" +
                    "/score - таблица результатов\n";

    public static final String START_COMMAND =
            "Привет, это телеграм-бот для проверки знаний ударений в словах,\n" +
                    "узнать о его функционале поподробнее ты можешь с помощью команды /help";

    public static final String TEST_QUESTION =
            "Как правильно ставится ударение в слове ";

    public static final String DURING_TEST =
            "Вы итак проходите тестирование.";

    public static final String CANCEL_SUCCESS =
            "Отмена прошла успешно!";

    public static final String UNKNOWN_COMMAND =
            "Неизвестная команда, для полного списка команд введите /help";
}
