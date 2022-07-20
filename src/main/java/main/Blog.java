package main;

public class Blog {
    public static final String ERROR_MESSAGE_OUTDATED_LINK = "\"Ссылка для восстановления пароля устарела. <a href=\\\"../restore-password\\\">Запросить ссылку снова</a>\"";
    public static final String ERROR_MESSAGE_PASSWORD_LENGTH = "Пароль короче 6-ти символов";
    public static final String ERROR_MESSAGE_CAPTCHA_CODE = "Код с картинки введён неверно";
    public static final String ERROR_MESSAGE_IMAGE_SIZE_EXCEEDED = "Размер файла превышает допустимый размер";
    public static final String ERROR_MESSAGE_PHOTO_SIZE_EXCEEDED = "Фото слишком большое, нужно не более 5 Мб";
    public static final String ERROR_MESSAGE_POST_TITLE_IS_MISSING = "Заголовок не установлен";
    public static final String ERROR_MESSAGE_POST_TEXT_LENGTH = "Текст публикации слишком короткий";
    public static final String ERROR_MESSAGE_COMMENT_TEXT_LENGTH = "Текст комментария не задан или слишком короткий";
    public static final String ERROR_MESSAGE_EMAIL_REGISTERED = "Этот e-mail уже зарегистрирован";
    public static final String ERROR_MESSAGE_NAME_IS_INCORRECT = "Имя указано неверно";

    public static final int IMAGE_LIMIT_WEIGHT = 10485760;
    public static final int PHOTO_LIMIT_WEIGHT = 5242880;
    public static final int PHOTO_WIDTH = 90;
    public static final int PHOTO_HEIGHT = 90;
    public static final int IMAGE_MAX_WIDTH = 970;
    public static final int CAPTCHA_TIME_EXPIRED_MIN = 60;
    public static final int POST_MIN_TITLE_LENGTH = 1;
    public static final int POST_MIN_TEXT_LENGTH = 50;
    public static final int POST_ANNOUNCE_MAX_TEXT_LENGTH = 150;
    public static final int COMMENT_TEXT_LENGTH = 1;
    public static final int PASSWORD_MIN_LENGTH = 6;

    public static final String SETTINGS_MULTIUSER_MODE_TEXT = "Многопользовательский режим";
    public static final String SETTINGS_POST_PREMODERATION_TEXT = "Премодерация постов";
    public static final String SETTINGS_STATISTICS_IS_PUBLIC_TEXT = "Показывать всем статистику блога";
    public static final String YES_VALUE = "YES";
    public static final String NO_VALUE = "NO";

    public static final String PATH_FOR_AVATARS = "/avatars/";
    public static final String PATH_FOR_UPLOAD = "/upload/";
    public static final String EMAIL_URI_FOR_CHANGE_PASSWORD = "/login/change-password/";
    public static final String EMAIL_SUBJECT = "Восстановление пароля";

    public static final String REGEX_FOR_USER_NAME = "[\\w\\s\\S]+";
}
