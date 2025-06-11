package backend.megamarket.service.orderservice.entity.enums;

/**
 * Перечисление ролей пользователей в системе.
 * <p>
 * Используется для определения уровня доступа и прав пользователя.
 */
public enum Role {
    /**
     * Роль обычного пользователя.
     */
    ROLE_USER,

    /**
     * Роль администратора с расширенными правами.
     */
    ROLE_ADMIN
}
