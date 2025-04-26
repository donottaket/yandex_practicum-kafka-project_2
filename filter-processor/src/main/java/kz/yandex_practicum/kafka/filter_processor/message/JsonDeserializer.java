package kz.yandex_practicum.kafka.filter_processor.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer implements Deserializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, Message.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации объекта Message", e);
        }
    }
} 