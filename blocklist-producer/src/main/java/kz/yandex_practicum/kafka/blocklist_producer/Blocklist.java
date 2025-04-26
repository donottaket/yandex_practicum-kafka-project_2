package kz.yandex_practicum.kafka.blocklist_producer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Структура списка заблокированных пользователей.
 *
 * @author maenlest
 */
public class Blocklist {
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

    private final String user; // Пользователь
    private final Set<String> blocklist; // Список заблокированных пользователей

    /**
     * Конструктор, который создает случайный список заблокированных пользователей.
     */
    public Blocklist() {
        this.user = USER_LIST.get(ThreadLocalRandom.current().nextInt(USER_LIST.size())); // Случайный пользователь
        this.blocklist = new HashSet<>();

        for (int i = 1; i < ThreadLocalRandom.current().nextInt(USER_LIST.size()); ++i) {
            String blockedUser = USER_LIST.get(ThreadLocalRandom.current().nextInt(USER_LIST.size())); // Случайный заблокированный пользователь

            if (!blockedUser.equals(this.user)) {
                this.blocklist.add(blockedUser); // Добавляем заблокированного пользователя в список
            }
        }
    }

    public String getUser() {
        return user;
    }

    public Set<String> getBlocklist() {
        return blocklist;
    }

    @Override
    public String toString() {
        return "Blocklist{" +
                "user=" + user +
                ", blocklist=" + blocklist +
                '}';
    }
}