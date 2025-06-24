package backend.megamarket.service.orderservice.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки бина {@link ModelMapper}.
 * <p>
 * Предоставляет бин {@code ModelMapper} для автоматического маппинга объектов DTO и сущностей.
 */
@Configuration
public class MapperConfig {

    /**
     * Создает и настраивает бин {@link ModelMapper}.
     *
     * @return новый экземпляр {@code ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}