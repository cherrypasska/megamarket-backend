package backend.megamarket.inventoryservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для регистрации фильтров в приложении.
 * <p>
 * Используется для регистрации фильтра валидации JWT для всех входящих HTTP-запросов,
 * направленных к API (URL с префиксом /api/*).
 */
@Configuration
public class FilterConfig {

    /**
     * Регистрирует фильтр {@link JwtValidationFilter} для проверки JWT-токена.
     * <p>
     * Фильтр будет применяться ко всем запросам, начинающимся с {@code /api/}.
     *
     * @param filter экземпляр фильтра {@link JwtValidationFilter}
     * @return объект регистрации фильтра
     */
    @Bean
    public FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter filter) {
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}