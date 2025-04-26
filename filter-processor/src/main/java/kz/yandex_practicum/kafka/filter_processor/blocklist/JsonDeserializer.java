package kz.yandex_practicum.kafka.filter_processor.blocklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer implements Deserializer<Blocklist> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Blocklist deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, Blocklist.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации объекта Blocklist", e);
        }
    }
} 