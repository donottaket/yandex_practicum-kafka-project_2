package kz.yandex_practicum.kafka.filter_processor;

import kz.yandex_practicum.kafka.filter_processor.blocklist.Blocklist;
import kz.yandex_practicum.kafka.filter_processor.blocklist.BlocklistSerdes;
import kz.yandex_practicum.kafka.filter_processor.message.Message;
import kz.yandex_practicum.kafka.filter_processor.message.MessageSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class MessageFilterProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFilterProcessor.class);

    private static final MessageSerdes messageSerdes = new MessageSerdes();
    private static final BlocklistSerdes blocklistSerde = new BlocklistSerdes();
    private static final WordAccumulatorSerde wordAccumulatorSerde = new WordAccumulatorSerde();
    private static final WordContextSerde wordContextSerde = new WordContextSerde();

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "message-filter-processor");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092");

        StreamsBuilder builder = new StreamsBuilder();

        // 1. Читаем GlobalKTable с блоклистом
        GlobalKTable<String, Blocklist> blocklistTable = builder.globalTable(
                "blocklist",
                Consumed.with(Serdes.String(), blocklistSerde)
        );

        // 2. Читаем GlobalKTable с запрещёнными словами
        GlobalKTable<String, String> censureTable = builder.globalTable(
                "censure",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        // 3. Поток сообщений
        KStream<String, Message> messageStream = builder.stream(
                "message",
                Consumed.with(Serdes.String(), messageSerdes)
        );

        // 4. Фильтрация по блоклисту
        KStream<String, Message> filteredStream = messageStream
                .join(
                        blocklistTable,
                        (messageKey, message) -> messageKey, // ключ для поиска в блоклист
                        (message, blocklist) -> {
                            if (blocklist == null) {
                                return message; // нет блоклиста — пропускаем
                            }

                            if (blocklist.blocklist().contains(message.from())) {
                                return null; // заблокирован — удаляем
                            }

                            return message;
                        }
                )
                .filter((key, message) -> message != null);

        // 5. Разбивка на слова
        KStream<String, WordContext> wordsStream = filteredStream
                .flatMap((key, message) -> {
                            List<KeyValue<String, WordContext>> splitWords = new ArrayList<>();
                            String[] wordArray = message.text().split("\\s+");

                            for (int i = 0; i < wordArray.length; ++i) {
                                splitWords.add(KeyValue.pair(
                                        wordArray[i],
                                        new WordContext(
                                                message.from(),
                                                message.to(),
                                                message.messageId(),
                                                i,
                                                wordArray[i]
                                        )
                                ));
                            }

                            return splitWords;
                        }
                );

        // 6. Замена плохих слов через join с цензурной таблицей
        KStream<String, WordContext> censoredWordsStream = wordsStream
                .leftJoin(
                        censureTable,
                        (word, context) -> word,
                        (context, replacement) -> {
                            if (replacement != null) {
                                context.setReplacement(replacement);
                            }

                            return context;
                        }
                );

        // 7. Группировка и сборка обратно в сообщения
        KStream<UUID, Message> censoredMessages = censoredWordsStream
                .groupBy(
                        (word, context) -> context.getMessageKey(), // ключ - уникальный id сообщения
                        Grouped.with(Serdes.UUID(), wordContextSerde)
                )
                .aggregate(
                        WordAccumulator::new,
                        (key, wordContext, accumulator) -> accumulator.addWord(wordContext),
                        Materialized.with(Serdes.UUID(), wordAccumulatorSerde)
                )
                .toStream()
                .mapValues(WordAccumulator::toMessage);

        // 8. Вывод в финальный топик
        censoredMessages.to("filtered", Produced.with(Serdes.UUID(), messageSerdes));

        // 9. Запуск приложения
        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Kafka Streams...");
            streams.close();
        }));
    }
} 