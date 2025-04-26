package kz.yandex_practicum.kafka.censure_producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Точка входа приложения для публикации правил цензуры в Kafka.
 *
 * @author maenlest
 */
public class CensureProducerLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(CensureProducerLauncher.class);

    private static final String TOPIC_NAME;
    private static final Properties PROPERTIES;

    private static int WORD_INDEX;

    static {
        TOPIC_NAME = "censure"; // Название топика

        WORD_INDEX = 1; // Индекс слов нецензурной лексики. Необходим для генерации уникальных слов нецензурной лексики

        PROPERTIES = new Properties();
        PROPERTIES.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092"); // Адреса брокеров Kafka
        PROPERTIES.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Сериализатор ключа
        PROPERTIES.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Сериализатор значения
        PROPERTIES.put(ProducerConfig.ACKS_CONFIG, "all"); // Ждем подтверждения от всех реплик
        PROPERTIES.put(ProducerConfig.RETRIES_CONFIG, 3); // Количество попыток отправки сообщения в случае ошибки
    }

    public static void main(String[] args) {
        try (Producer<String, String> producer = new KafkaProducer<>(PROPERTIES)) {
            startPublishingCensure(producer);
        }
    }

    /**
     * Запускает процесс публикации правил цензуры в Kafka.
     *
     * @param producer настроенный продюсер для правил цензуры в Kafka.
     */
    private static void startPublishingCensure(Producer<String, String> producer) {
        while (true) {
            publish(producer);

            try {
                Thread.sleep(15_000); // Ждем 15 секунд перед публикацией следующего сообщения
            } catch (InterruptedException e) {
                LOGGER.error("Ошибка при публикации допустимых синонимов запрещенных слов (InterruptedException)", e);
            }
        }
    }

    /**
     * Публикует допустимые синонимы запрещенных слов в Kafka.
     *
     * @param producer настроенный продюсер для публикации допустимых синонимов запрещенных слов.
     */
    private static void publish(Producer<String, String> producer) {
        for (int i = 0; i < 3; ++i) {
            LOGGER.info("Публикуем допустимые синонимы запрещенных слов: bad = '{}', good = '{}'", "badword" + WORD_INDEX, "goodword" + WORD_INDEX);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "badword" + WORD_INDEX, "goodword" + WORD_INDEX);

            try {
                producer.send(record);
            } catch (Exception e) {
                LOGGER.error("Ошибка при публикации допустимых синонимов запрещенных слов", e);
            }

            ++WORD_INDEX;
        }
    }
}