package backend.megamarket.notificationservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;

/**
 * Фильтр для проверки JWT токенов в HTTP-запросах.
 * <p>
 * Проверяет наличие заголовка Authorization, валидирует токен,
 * а также проверяет наличие роли "ROLE_ADMIN". В случае неуспешной
 * валидации возвращает соответствующий HTTP-статус (401 или 403).
 */
@Component
public class JwtValidationFilter implements Filter {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Обрабатывает входящий HTTP-запрос: извлекает JWT токен, валидирует его
     * и проверяет наличие прав доступа.
     *
     * @param request  входящий запрос
     * @param response исходящий ответ
     * @param chain    цепочка фильтров
     * @throws IOException      если происходит ошибка ввода/вывода
     * @throws ServletException если происходит ошибка на уровне сервлета
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Требуется авторизация");
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            String role = (String) claims.get("role");
            if (!"ROLE_ADMIN".equals(role)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Требуется роль ADMIN");
                return;
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Неверный токен");
        }
    }

    /**
     * Получает объект {@link Key}, используемый для проверки подписи JWT.
     *
     * @return ключ для подписи JWT
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}