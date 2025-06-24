package backend.megamarket.notificationservice.mapper;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Класс десериализатора для списка объектов {@link OrderEventDto}.
 * Используется для преобразования массива байт из Kafka в список DTO заказов.
 */
public class OrderEventListDeserializer implements Deserializer<OrderEventDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderEventDto deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, OrderEventDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OrderEventDto", e);
        }
    }
}
