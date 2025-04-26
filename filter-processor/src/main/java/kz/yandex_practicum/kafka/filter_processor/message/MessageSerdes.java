package kz.yandex_practicum.kafka.filter_processor.message;

import org.apache.kafka.common.serialization.Serdes;

public class MessageSerdes extends Serdes.WrapperSerde<Message> {
    public MessageSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
} 