package kz.yandex_practicum.kafka.filter_processor.blocklist;

import org.apache.kafka.common.serialization.Serdes;

public class BlocklistSerdes extends Serdes.WrapperSerde<Blocklist> {
    public BlocklistSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
} 