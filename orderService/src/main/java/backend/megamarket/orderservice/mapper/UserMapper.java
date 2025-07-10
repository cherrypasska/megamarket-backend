package backend.megamarket.orderservice.mapper;

import backend.megamarket.orderservice.dto.RefreshRequestDto;
import backend.megamarket.orderservice.dto.SignUpRequestDto;
import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования DTO, связанных с пользователем, в сущность {@link UserEntity}.
 * Также предоставляет вспомогательные методы для обновления данных пользователя.
 * <p>
 * Используется при регистрации, обновлении и валидации учетных данных пользователей.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    /**
     * Преобразует DTO регистрации {@link SignUpRequestDto} в сущность {@link UserEntity}.
     *
     * @param request объект DTO с данными регистрации
     * @return новая сущность пользователя
     */
    public UserEntity signUpToEntity(SignUpRequestDto request, PasswordEncoder passwordEncoder) {
        var user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.ROLE_USER)
                .build();
        return user;
    }

    /**
     * Обновляет поля существующего пользователя новыми значениями.
     * Используется при замене всех ключевых данных пользователя.
     *
     * @param user    текущая сущность пользователя
     * @param newUser обновлённая сущность с новыми данными
     * @return изменённая сущность пользователя
     */
    public UserEntity refreshUser(UserEntity user, UserEntity newUser) {
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setUsername(newUser.getUsername());
        return user;
    }

    /**
     * Обновляет основные поля пользователя: логин, email и пароль.
     * Предназначен для универсального обновления без изменения роли и других параметров.
     *
     * @param existing    существующая сущность пользователя
     * @param updatedUser сущность с новыми данными
     * @return обновлённая сущность пользователя
     */
    public UserEntity updateUser(UserEntity existing, UserEntity updatedUser) {
        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());
        return existing;
    }
}