package backend.megamarket.service.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса на регистрацию нового пользователя.
 * <p>
 * Содержит необходимые данные для создания нового аккаунта: имя пользователя, email и пароль.
 */
@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequestDto {

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(max = 50, message = "Имя пользователя должно содержать до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    /**
     * Адрес электронной почты пользователя.
     */
    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    /**
     * Пароль пользователя.
     */
    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;
}