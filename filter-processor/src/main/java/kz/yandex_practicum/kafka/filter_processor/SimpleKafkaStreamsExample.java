package kz.yandex_practicum.kafka.filter_processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class SimpleKafkaStreamsExample {
    public static void main(String[] args) {
        try {
            // Конфигурация Kafka Streams
            Properties config = new Properties();
            config.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-kafka-streams-app");
            config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

            // Создание топологии
            StreamsBuilder builder = new StreamsBuilder();
            KStream<String, String> inputStream = builder.stream("input-topic");

            // Обработка данных
            KStream<String, String> processedStream = inputStream.mapValues(value -> "Processed: " + value);

            // Отправка обработанных данных в другой топик
            processedStream.to("output-topic");

            // Инициализация Kafka Streams
            KafkaStreams streams = new KafkaStreams(builder.build(), config);

            // Запуск приложения
            streams.start();

            System.out.println("Kafka Streams приложение запущено успешно.");
        } catch (Exception e) {
            System.err.println("Ошибка при запуске Kafka Streams приложения: " + e.getMessage());
        }
    }
} 