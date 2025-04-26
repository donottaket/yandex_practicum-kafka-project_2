package kz.yandex_practicum.kafka.filter_processor.message;

import java.util.UUID;

/**
 * Структура сообщения.
 *
 * @param from      отправитель.
 * @param to        получатель.
 * @param text      текст сообщения.
 * @param messageId идентификатор сообщения.
 * @author maenlest
 */
public record Message(String from, String to, String text, UUID messageId) {

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