package kz.yandex_practicum.kafka.filter_processor.blocklist;

import java.util.Set;

/**
 * Структура списка заблокированных пользователей.
 *
 * @param user      пользователь.
 * @param blocklist список заблокированных пользователей.
 * @author maenlest
 */
public record Blocklist(String user, Set<String> blocklist) {

    @Override
    public String toString() {
        return "Blocklist{" +
                "user=" + user +
                ", blocklist=" + blocklist +
                '}';
    }
}