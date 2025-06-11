package backend.megamarket.notificationservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для создания и регистрации {@link ModelMapper}.
 * <p>
 * {@code ModelMapper} используется для маппинга
 * между объектами различных типов, например, DTO и Entity.
 */
@Configuration
public class MapperConfig {

    /**
     * Создаёт и регистрирует бин {@link ModelMapper} в контексте Spring.
     *
     * @return экземпляр {@code ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}