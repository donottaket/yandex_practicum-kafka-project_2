package kz.yandex_practicum.kafka.filter_processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class WordContextSerde implements Serde<WordContext> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<WordContext> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<WordContext> deserializer() {
        return (topic, bytes) -> {
            try {
                return objectMapper.readValue(bytes, WordContext.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}