package backend.megamarket.notificationservice.mapper;

import backend.megamarket.notificationservice.dto.OrderEventDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;

/**
 * Класс десериализатора для списка объектов {@link OrderEventDto}.
 * Используется для преобразования массива байт из Kafka в список DTO заказов.
 */
public class OrderEventListDeserializer implements Deserializer<List<OrderEventDto>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Десериализует массив байт в список {@link OrderEventDto}.
     *
     * @param topic название Kafka топика
     * @param data  массив байт с сериализованными данными списка заказов
     * @return список объектов {@link OrderEventDto}
     * @throws RuntimeException если десериализация не удалась
     */
    @Override
    public List<OrderEventDto> deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<OrderEventDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OrderEvent list", e);
        }
    }
}