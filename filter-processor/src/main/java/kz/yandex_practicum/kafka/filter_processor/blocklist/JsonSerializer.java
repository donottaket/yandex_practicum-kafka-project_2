package kz.yandex_practicum.kafka.filter_processor.blocklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Кастомный сериализатор для отправки объектов в JSON в Kafka.
 *
 * @author maenlest
 */
public class JsonSerializer implements Serializer<Blocklist> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Blocklist data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}