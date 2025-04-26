package kz.yandex_practicum.kafka.blocklist_producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Точка входа приложения для публикации списка заблокированных пользователей в Kafka.
 *
 * @author maenlest
 */
public class BlocklistProducerLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlocklistProducerLauncher.class);

    private static final String TOPIC_NAME;
    private static final Properties PROPERTIES;

    static {
        TOPIC_NAME = "blocklist"; // Название топика

        PROPERTIES = new Properties();
        PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092"); // Адреса брокеров Kafka
        PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Сериализатор ключа
        PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName()); // Сериализатор значения
        PROPERTIES.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждем подтверждения от всех реплик
        PROPERTIES.put(ProducerConfig.RETRIES_CONFIG, 3); // Количество попыток отправки сообщения в случае ошибки
    }

    public static void main(String[] args) {
        try (Producer<String, Blocklist> producer = new KafkaProducer<>(PROPERTIES)) {
            startPublishingBlocklist(producer);
        }
    }

    /**
     * Запускает процесс публикации списка заблокированных пользователей в Kafka.
     *
     * @param producer настроенный продюсер для публикации списка заблокированных пользователей.
     */
    private static void startPublishingBlocklist(Producer<String, Blocklist> producer) {
        while (true) {
            publish(producer);

            try {
                Thread.sleep(15_000); // Ждем 15 секунд перед публикацией следующего сообщения
            } catch (InterruptedException e) {
                LOGGER.error("Ошибка при публикации списка заблокированных пользователей (InterruptedException)", e);
            }
        }
    }

    /**
     * Публикует список заблокированных пользователей в Kafka.
     *
     * @param producer настроенный продюсер для публикации списка заблокированных пользователей.
     */
    private static void publish(Producer<String, Blocklist> producer) {
        Blocklist value = new Blocklist(); // Динамически генерируем новый список заблокированных пользователей

        LOGGER.info("Публикуем список заблокированных пользователей: {}", value);

        ProducerRecord<String, Blocklist> record = new ProducerRecord<>(TOPIC_NAME, value.getUser(), value);

        try {
            producer.send(record);
        } catch (Exception e) {
            LOGGER.error("Ошибка при публикации списка заблокированных пользователей", e);
        }
    }
}