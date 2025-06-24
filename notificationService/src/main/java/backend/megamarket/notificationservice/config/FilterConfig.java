package backend.megamarket.notificationservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс, регистрирующий фильтр в контексте Spring Boot-приложения.
 * Используется для настройки фильтра валидации JWT токенов, применяемого к защищённым API-маршрутам.
 */
@Configuration
public class FilterConfig {
    /**
     * Регистрирует фильтр {@link JwtValidationFilter}, который проверяет JWT токены
     * для всех входящих HTTP-запросов по пути, начинающемуся с "/api/".
     *
     * @param filter экземпляр фильтра {@link JwtValidationFilter}
     * @return конфигурация регистрации фильтра
     */
    @Bean
    public FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter filter) {
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}