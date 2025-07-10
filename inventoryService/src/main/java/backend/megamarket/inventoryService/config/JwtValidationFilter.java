package backend.megamarket.inventoryservice.config;

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
 * Фильтр для проверки JWT токена в каждом HTTP-запросе к защищённым ресурсам.
 * <p>
 * Проверяет наличие токена, его подлинность и наличие нужной роли (только ADMIN).
 */
@Component
public class JwtValidationFilter implements Filter {

    /**
     * Ключ для подписи JWT.
     */
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Метод фильтрации, вызывается для каждого запроса.
     * Проверяет JWT в заголовке Authorization и его роль.
     *
     * @param request  исходный HTTP-запрос
     * @param response HTTP-ответ
     * @param chain    цепочка фильтров
     * @throws IOException      в случае ошибок ввода/вывода
     * @throws ServletException в случае ошибок выполнения фильтра
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

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Неверный токен");
            return;
        }

        String role = (String) claims.get("role");
        if (!"ROLE_ADMIN".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Требуется роль ADMIN");
            return;
        }

        chain.doFilter(request, response);
    }


    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}