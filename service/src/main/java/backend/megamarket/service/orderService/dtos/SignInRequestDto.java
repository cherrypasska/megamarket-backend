package backend.megamarket.service.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса аутентификации пользователя.
 * <p>
 * Содержит данные, необходимые для входа пользователя в систему — имя пользователя и пароль.
 */
@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequestDto {

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(max = 50, message = "Имя пользователя должно содержать до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    /**
     * Пароль пользователя.
     */
    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
