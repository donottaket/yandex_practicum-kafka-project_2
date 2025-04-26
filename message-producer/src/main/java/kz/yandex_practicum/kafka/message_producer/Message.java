package kz.yandex_practicum.kafka.message_producer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Структура сообщения.
 *
 * @author maenlest
 */
public class Message {
    private static final List<String> USER_LIST = List.of(
            "USER-1",
            "USER-2",
            "USER-3",
            "USER-4",
            "USER-5",
            "USER-6",
            "USER-7",
            "USER-8",
            "USER-9"
    );

    // Индекс слов нецензурной лексики
    // Необходим для генерации уникальных слов нецензурной лексики
    private static int BAD_WORD_INDEX = 1;

    private final String from; // Отправитель
    private final String to; // Получатель
    private final String text; // Текст сообщения
    private final UUID messageId; // Идентификатор сообщения

    /**
     * Конструктор по умолчанию.
     * Генерирует случайные значения для полей from, to и text, messageId.
     */
    public Message() {
        this.from = USER_LIST.get(ThreadLocalRandom.current().nextInt(USER_LIST.size())); // Случайный отправитель
        this.to = USER_LIST.get(ThreadLocalRandom.current().nextInt(USER_LIST.size())); // Случайный получатель
        this.text = "Random text = " + (Math.random() > 0.5 ? "ordinaryword" : "badword" + ThreadLocalRandom.current().nextInt(BAD_WORD_INDEX)); // Случайный текст сообщения с возможной (50% вероятностью) нецензурной лексикой
        this.messageId = UUID.randomUUID(); // Генерация уникального идентификатора сообщения

        ++BAD_WORD_INDEX;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", messageId=" + messageId +
                ", text='" + text + '\'' +
                '}';
    }
}