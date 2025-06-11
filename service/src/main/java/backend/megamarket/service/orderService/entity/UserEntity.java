package backend.megamarket.service.orderservice.entity;

import backend.megamarket.service.orderservice.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Сущность пользователя, представляющая данные пользователя в базе данных.
 * <p>
 * Реализует интерфейс {@link UserDetails} для интеграции с Spring Security.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    /**
     * Имя пользователя, используемое для аутентификации.
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * Пароль пользователя.
     */
    @Column(name = "user_password", nullable = false)
    private String password;

    /**
     * Электронная почта пользователя.
     */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * Роль пользователя, определяющая его права в системе.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    /**
     * Получить список прав доступа пользователя.
     *
     * @return коллекция с правами доступа пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Проверка, не истек ли срок действия аккаунта.
     *
     * @return всегда {@code true}, аккаунт всегда активен
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверка, не заблокирован ли аккаунт.
     *
     * @return всегда {@code true}, аккаунт не заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверка, не истек ли срок действия учетных данных.
     *
     * @return всегда {@code true}, учетные данные всегда действительны
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверка, включен ли аккаунт.
     *
     * @return всегда {@code true}, аккаунт всегда включен
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
