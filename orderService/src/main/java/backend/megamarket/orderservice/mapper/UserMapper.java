package backend.megamarket.orderservice.mapper;

import backend.megamarket.orderservice.dto.RefreshRequestDto;
import backend.megamarket.orderservice.dto.SignUpRequestDto;
import backend.megamarket.orderservice.entity.UserEntity;
import backend.megamarket.orderservice.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Маппер для преобразования DTO, связанных с пользователем, в сущность {@link UserEntity}.
 * Также предоставляет вспомогательные методы для обновления данных пользователя.
 * <p>
 * Используется при регистрации, обновлении и валидации учетных данных пользователей.
 */
@Mapper
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Преобразует DTO регистрации {@link SignUpRequestDto} в сущность {@link UserEntity}.
     *
     * @param request объект DTO с данными регистрации
     * @return новая сущность пользователя
     */
    public UserEntity signUpToEntity(SignUpRequestDto request){
        var user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        return user;
    }

    /**
     * Создаёт объект {@link UserEntity}, представляющий текущего пользователя,
     * из данных запроса на обновление {@link RefreshRequestDto}.
     * Используется для поиска и сверки старых данных.
     *
     * @param request DTO с текущими данными пользователя
     * @return сущность текущего пользователя
     */
    public UserEntity refreshCurrentUser(RefreshRequestDto request) {
        var user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        return user;
    }

    /**
     * Создаёт объект {@link UserEntity}, представляющий обновлённого пользователя,
     * из новых данных, переданных в {@link RefreshRequestDto}.
     *
     * @param request DTO с новыми данными пользователя
     * @return новая сущность пользователя
     */
    public UserEntity refreshNewUser(RefreshRequestDto request) {
        var newUser = UserEntity.builder()
                .username(request.getNewUsername())
                .email(request.getNewEmail())
                .password(passwordEncoder.encode(request.getNewPassword()))
                .role(Role.ROLE_USER)
                .build();
        return newUser;
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