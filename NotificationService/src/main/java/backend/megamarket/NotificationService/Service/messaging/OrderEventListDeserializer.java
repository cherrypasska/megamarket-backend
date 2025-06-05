package backend.megamarket.NotificationService.Service.messaging;

import backend.megamarket.NotificationService.Service.messaging.event.OrderEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;

public class OrderEventListDeserializer implements Deserializer<List<OrderEvent>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<OrderEvent> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<OrderEvent>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OrderEvent list", e);
        }
    }
}
